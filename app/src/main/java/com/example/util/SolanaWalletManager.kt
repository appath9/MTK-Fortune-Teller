package com.example.util

import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.programs.SystemProgram
import com.solana.publickey.SolanaPublicKey
import com.solana.transaction.AccountMeta
import com.solana.transaction.Message
import com.solana.transaction.Transaction
import com.solana.transaction.TransactionInstruction
import com.solana.mobilewalletadapter.clientlib.protocol.JsonRpc20Client
import com.solana.mobilewalletadapter.common.ProtocolContract
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

sealed class TransactionFailureOutcome {
    abstract val message: String

    data class ExplicitUserDeclined(
        override val message: String
    ) : TransactionFailureOutcome()

    data class UnknownTransactionOutcome(
        override val message: String,
        val cause: Throwable? = null
    ) : TransactionFailureOutcome()
}

fun Throwable.findJsonRpcRemoteException(): JsonRpc20Client.JsonRpc20RemoteException? {
    var current: Throwable? = this
    val visited = mutableSetOf<Throwable>()
    while (current != null && visited.add(current)) {
        if (current is JsonRpc20Client.JsonRpc20RemoteException) {
            return current
        }
        current = current.cause
    }
    return null
}

fun Throwable.getCauseChainClassesString(): String {
    val chain = mutableListOf<String>()
    var current: Throwable? = this
    val visited = mutableSetOf<Throwable>()
    while (current != null && visited.add(current)) {
        chain.add(current::class.java.name)
        current = current.cause
    }
    return chain.joinToString(" -> ")
}

fun Throwable.getCauseChainMessagesString(): String {
    val chain = mutableListOf<String>()
    var current: Throwable? = this
    val visited = mutableSetOf<Throwable>()
    while (current != null && visited.add(current)) {
        chain.add("${current::class.java.name}: ${current.message ?: "(null)"}")
        current = current.cause
    }
    return chain.joinToString(" -> ")
}

fun Throwable.hasTimeoutCancellationException(): Boolean {
    var current: Throwable? = this
    val visited = mutableSetOf<Throwable>()
    while (current != null && visited.add(current)) {
        if (current is TimeoutCancellationException) {
            return true
        }
        current = current.cause
    }
    return false
}

fun Throwable.hasCancellationException(): Boolean {
    var current: Throwable? = this
    val visited = mutableSetOf<Throwable>()
    while (current != null && visited.add(current)) {
        if (current is CancellationException) {
            return true
        }
        current = current.cause
    }
    return false
}

@Suppress("UNUSED_PARAMETER")
fun classifyTransactionFailure(
    e: Exception,
    capturedSignature: String?,
    defaultCancelMessage: String = "The Oracle attestation was cancelled.",
    defaultErrorMessage: String = "Unable to confirm the Oracle attestation. Verifying on-chain...",
    isFromMwaFailureResult: Boolean = false
): TransactionFailureOutcome {
    val rawMsg = e.message?.takeIf { it.isNotBlank() }
        ?: e.cause?.message?.takeIf { it.isNotBlank() }

    val isTimeoutCancellation = e.hasTimeoutCancellationException()
    val isCancellation = e.hasCancellationException()

    val containsRejection = isCancellation ||
            rawMsg == null ||
            rawMsg.contains("cancel", ignoreCase = true) ||
            rawMsg.contains("declin", ignoreCase = true) ||
            rawMsg.contains("reject", ignoreCase = true)

    val msg = if (containsRejection) {
        defaultErrorMessage
    } else {
        rawMsg
    }

    if (!capturedSignature.isNullOrBlank()) {
        return TransactionFailureOutcome.UnknownTransactionOutcome(
            message = msg,
            cause = e
        )
    }

    val jsonRpcException = e.findJsonRpcRemoteException()

    return when {
        jsonRpcException != null && jsonRpcException.code == ProtocolContract.ERROR_NOT_SIGNED -> {
            TransactionFailureOutcome.ExplicitUserDeclined(
                message = defaultCancelMessage
            )
        }
        isCancellation && !isTimeoutCancellation -> {
            TransactionFailureOutcome.ExplicitUserDeclined(
                message = defaultCancelMessage
            )
        }
        else -> {
            TransactionFailureOutcome.UnknownTransactionOutcome(
                message = msg,
                cause = e
            )
        }
    }
}

enum class SignatureConfirmationStatus {
    NOT_FOUND,
    PROCESSED,
    CONFIRMED,
    FINALIZED,
    FAILED
}

data class SignatureStatusResult(
    val status: SignatureConfirmationStatus,
    val error: String? = null
)

open class SolanaWalletManager {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val walletAdapter = MobileWalletAdapter(
        connectionIdentity = ConnectionIdentity(
            identityUri = Uri.parse("https://fortuneteller.solana.app"),
            iconUri = Uri.parse("favicon.ico"),
            identityName = "Sacred Numerology Web3"
        )
    )

    open suspend fun fetchLatestBlockhash(): String = withContext(Dispatchers.IO) {
        val jsonBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "getLatestBlockhash")
            put("params", JSONArray().put(JSONObject().apply {
                put("commitment", "confirmed")
            }))
        }.toString()

        val request = Request.Builder()
            .url("https://api.devnet.solana.com")
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("RPC request failed with code ${response.code}")
        }
        val responseString = response.body?.string()
            ?: throw IOException("Empty response from Devnet RPC")

        val jsonResponse = JSONObject(responseString)
        if (jsonResponse.has("error")) {
            val errorObj = jsonResponse.optJSONObject("error")
            val errMsg = errorObj?.optString("message") ?: "RPC Error"
            throw IOException("Devnet RPC error: $errMsg")
        }

        val resultObj = jsonResponse.getJSONObject("result")
        val valueObj = resultObj.getJSONObject("value")
        valueObj.getString("blockhash")
    }

    open suspend fun connectAndAuthorize(
        sender: ActivityResultSender,
        onSuccess: (publicKeyBytes: ByteArray, accountLabel: String?) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            when (val result = walletAdapter.transact(sender) { authResult -> authResult }) {
                is TransactionResult.Success -> {
                    val account = result.authResult.accounts.firstOrNull()
                    if (account != null) {
                        onSuccess(account.publicKey, account.accountLabel)
                    } else {
                        onError("No accounts found in wallet.")
                    }
                }
                is TransactionResult.NoWalletFound -> {
                    onError("No Solana wallet app found. Install Phantom or Solflare.")
                }
                is TransactionResult.Failure -> {
                    onError(result.e.message ?: "Wallet connection failed.")
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError(e.message ?: "Wallet connection failed.")
        }
    }

    open suspend fun recordOracleAttestation(
        sender: ActivityResultSender,
        questionId: String,
        chosenNumber: Int,
        blockhash: String,
        attestationId: String? = null,
        onAuthorizedWallet: ((walletBase58: String) -> Unit)? = null,
        onSuccess: (txSignature: String) -> Unit,
        onError: (String) -> Unit,
        onErrorOutcome: ((TransactionFailureOutcome) -> Unit)? = null
    ) {
        require(questionId.isNotBlank()) { "Question ID cannot be blank." }
        require(chosenNumber in 0..9) { "Chosen number must be strictly between 0 and 9." }
        require(blockhash.isNotBlank()) { "Blockhash cannot be blank." }

        val actualAttestationId = attestationId ?: "ORCL_${UUID.randomUUID()}"
        val opStartTs = System.currentTimeMillis()

        var countOnAuthorizedWallet = 0
        var countOnSuccess = 0
        var countOnError = 0
        var countOnErrorOutcome = 0

        val wrappedOnAuthorizedWallet: ((String) -> Unit)? = if (onAuthorizedWallet != null) {
            { wallet ->
                countOnAuthorizedWallet++
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=CALLBACK_INVOKED callback=onAuthorizedWallet hasAuthorizedWallet=true callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                }
                onAuthorizedWallet.invoke(wallet)
            }
        } else null

        val wrappedOnSuccess: (String) -> Unit = { sig ->
            countOnSuccess++
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=CALLBACK_INVOKED callback=onSuccess hasCapturedSignature=true callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            onSuccess(sig)
        }

        val wrappedOnError: (String) -> Unit = { msg ->
            countOnError++
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=CALLBACK_INVOKED callback=onError message=\"$msg\" callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            onError(msg)
        }

        val wrappedOnErrorOutcome: ((TransactionFailureOutcome) -> Unit)? = if (onErrorOutcome != null) {
            { outcome ->
                countOnErrorOutcome++
                val outcomeName = outcome::class.java.simpleName
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=CALLBACK_INVOKED callback=onErrorOutcome outcome=$outcomeName message=\"${outcome.message}\" callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                }
                onErrorOutcome.invoke(outcome)
            }
        } else null

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=OPERATION_START ts=$opStartTs")
        }

        val memoJson = JSONObject().apply {
            put("id", actualAttestationId)
            put("app", "MTK-Oracle")
            put("q", questionId.trim())
            put("num", chosenNumber)
            put("ts", System.currentTimeMillis() / 1000L)
        }.toString()

        var currentAttemptSignature: String? = null

        try {
            val mwaScope = CoroutineScope(Dispatchers.IO + Job())
            val deferred = mwaScope.async {
                walletAdapter.transact(sender) { authResult ->
                    val account = authResult.accounts.firstOrNull()
                        ?: throw IllegalStateException("No accounts found in connected wallet.")

                    val userPublicKey = SolanaPublicKey(account.publicKey)
                    val userPkBase58 = SolanaWalletGatekeeper.encodeToBase58(userPublicKey.bytes)
                    wrappedOnAuthorizedWallet?.invoke(userPkBase58)

                    val memoProgramId = SolanaPublicKey.from("MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr")

                    val accountMeta = AccountMeta(userPublicKey, isSigner = true, isWritable = true)
                    val instruction = TransactionInstruction(
                        programId = memoProgramId,
                        accounts = listOf(accountMeta),
                        data = memoJson.toByteArray(Charsets.UTF_8)
                    )

                    val message = Message.Builder()
                        .addInstruction(instruction)
                        .setRecentBlockhash(blockhash)
                        .build()

                    val transaction = Transaction(message)
                    val serializedTx = transaction.serialize()

                    // Non-sensitive Transaction Diagnostics logging (structural only)
                    if (BuildConfig.DEBUG) {
                        val decodedTx = Transaction.from(serializedTx)
                        val decodedMsg = decodedTx.message
                        val accountKeysSize = decodedMsg.accounts.size

                        Log.d("SolanaTxDebug", "=== Solana Transaction Diagnostics ===")
                        Log.d("SolanaTxDebug", "Serialized Tx Byte Length: ${serializedTx.size}")
                        Log.d("SolanaTxDebug", "Header signatureCount: ${decodedMsg.signatureCount}")
                        Log.d("SolanaTxDebug", "Header readOnlyAccounts: ${decodedMsg.readOnlyAccounts}")
                        Log.d("SolanaTxDebug", "Header readOnlyNonSigners: ${decodedMsg.readOnlyNonSigners}")
                        Log.d("SolanaTxDebug", "AccountKeys Count: $accountKeysSize")
                        decodedMsg.instructions.forEachIndexed { iIdx, ix ->
                            Log.d("SolanaTxDebug", "Instruction[$iIdx] programIdIndex: ${ix.programIdIndex} (valid: ${ix.programIdIndex.toInt() < accountKeysSize})")
                            ix.accountIndices.forEachIndexed { aIdx, accIdx ->
                                Log.d("SolanaTxDebug", "Instruction[$iIdx] accountIndex[$aIdx]: $accIdx (valid: ${accIdx.toInt() < accountKeysSize})")
                            }
                        }
                    }

                    val signResult = signAndSendTransactions(arrayOf(serializedTx))
                    val rawSig = signResult.signatures.firstOrNull()
                        ?: throw IllegalStateException("No signature returned from wallet.")

                    val sigBase58 = SolanaWalletGatekeeper.encodeToBase58(rawSig)
                    currentAttemptSignature = sigBase58
                    sigBase58
                }
            }

            val result = try {
                withTimeoutOrNull(25_000L) {
                    deferred.await()
                }
            } finally {
                deferred.cancel()
            }

            if (result == null) {
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=MWA_TIMEOUT path=\"withTimeoutOrNull returning null\" timeoutMs=25000 hasCapturedSignature=${!currentAttemptSignature.isNullOrBlank()} callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                }
                val outcome = TransactionFailureOutcome.UnknownTransactionOutcome(
                    message = "Unable to confirm the Oracle attestation. Verifying on-chain..."
                )
                if (wrappedOnErrorOutcome != null) {
                    wrappedOnErrorOutcome.invoke(outcome)
                } else {
                    wrappedOnError("Unable to confirm the Oracle attestation. Verifying on-chain...")
                }
                return
            }

            when (result) {
                is TransactionResult.Success -> {
                    currentAttemptSignature = result.payload
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=MWA_SUCCESS path=\"TransactionResult.Success\" hasCapturedSignature=true callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                    }
                    wrappedOnSuccess(result.payload)
                }
                is TransactionResult.NoWalletFound -> {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=MWA_NO_WALLET path=\"TransactionResult.NoWalletFound\" hasCapturedSignature=${!currentAttemptSignature.isNullOrBlank()} callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                    }
                    val msg = "No Solana wallet app found. Install Phantom or Solflare."
                    val outcome = TransactionFailureOutcome.UnknownTransactionOutcome(msg)
                    if (wrappedOnErrorOutcome != null) {
                        wrappedOnErrorOutcome.invoke(outcome)
                    } else {
                        wrappedOnError(msg)
                    }
                }
                is TransactionResult.Failure -> {
                    val jsonRpcCode = result.e.findJsonRpcRemoteException()?.code
                    val outcome = classifyTransactionFailure(
                        e = result.e,
                        capturedSignature = currentAttemptSignature,
                        defaultCancelMessage = "The Oracle attestation was cancelled.",
                        defaultErrorMessage = "Unable to confirm the Oracle attestation. Verifying on-chain...",
                        isFromMwaFailureResult = true
                    )
                    val classificationName = outcome::class.java.simpleName
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=MWA_FAILURE path=\"TransactionResult.Failure\" exceptionClass=${result.e::class.java.name} exceptionMessage=\"${result.e.message}\" jsonRpcCode=${jsonRpcCode ?: "none"} causeChainClasses=\"${result.e.getCauseChainClassesString()}\" causeChainMessages=\"${result.e.getCauseChainMessagesString()}\" hasCapturedSignature=${!currentAttemptSignature.isNullOrBlank()} classification=$classificationName callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                    }
                    val isCancelled = outcome is TransactionFailureOutcome.ExplicitUserDeclined
                    val errorMsg = if (isCancelled) {
                        "The Oracle attestation was cancelled."
                    } else {
                        outcome.message
                    }
                    if (wrappedOnErrorOutcome != null) {
                        wrappedOnErrorOutcome.invoke(outcome)
                    } else {
                        wrappedOnError(errorMsg)
                    }
                }
            }
        } catch (e: CancellationException) {
            val jsonRpcCode = e.findJsonRpcRemoteException()?.code
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=MWA_CANCELLATION path=\"coroutine cancellation path\" exceptionClass=${e::class.java.name} exceptionMessage=\"${e.message}\" jsonRpcCode=${jsonRpcCode ?: "none"} causeChainClasses=\"${e.getCauseChainClassesString()}\" causeChainMessages=\"${e.getCauseChainMessagesString()}\" hasCapturedSignature=${!currentAttemptSignature.isNullOrBlank()} callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            throw e
        } catch (e: Exception) {
            val jsonRpcCode = e.findJsonRpcRemoteException()?.code
            val outcome = classifyTransactionFailure(
                e = e,
                capturedSignature = currentAttemptSignature,
                defaultCancelMessage = "The Oracle attestation was cancelled.",
                defaultErrorMessage = "Unable to confirm the Oracle attestation. Verifying on-chain...",
                isFromMwaFailureResult = false
            )
            val classificationName = outcome::class.java.simpleName
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$actualAttestationId event=MWA_OUTER_EXCEPTION path=\"outer exception/catch\" exceptionClass=${e::class.java.name} exceptionMessage=\"${e.message}\" jsonRpcCode=${jsonRpcCode ?: "none"} causeChainClasses=\"${e.getCauseChainClassesString()}\" causeChainMessages=\"${e.getCauseChainMessagesString()}\" hasCapturedSignature=${!currentAttemptSignature.isNullOrBlank()} classification=$classificationName callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            val isCancelled = outcome is TransactionFailureOutcome.ExplicitUserDeclined
            val errorMsg = if (isCancelled) {
                "The Oracle attestation was cancelled."
            } else {
                outcome.message
            }
            if (wrappedOnErrorOutcome != null) {
                wrappedOnErrorOutcome.invoke(outcome)
            } else {
                wrappedOnError(errorMsg)
            }
        }
    }

    open suspend fun getTransactionDetails(signature: String): String? = withContext(Dispatchers.IO) {
        if (signature.isBlank()) return@withContext null

        try {
            val jsonBody = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "getTransaction")
                put("params", JSONArray().apply {
                    put(signature)
                    put(JSONObject().apply {
                        put("encoding", "jsonParsed")
                        put("maxSupportedTransactionVersion", 0)
                    })
                })
            }.toString()

            val request = Request.Builder()
                .url("https://api.devnet.solana.com")
                .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null
            val responseString = response.body?.string() ?: return@withContext null

            val jsonResponse = JSONObject(responseString)
            val resultObj = jsonResponse.optJSONObject("result") ?: return@withContext null

            val metaObj = resultObj.optJSONObject("meta")
            val logMessages = metaObj?.optJSONArray("logMessages")?.toString() ?: ""
            val txObj = resultObj.optJSONObject("transaction")?.toString() ?: ""

            "$logMessages $txObj"
        } catch (e: Exception) {
            Log.w("SolanaWalletManager", "Error fetching transaction details", e)
            null
        }
    }

    open suspend fun reconcileAttestation(
        userWalletBase58: String,
        attestationId: String
    ): String? = withContext(Dispatchers.IO) {
        if (userWalletBase58.isBlank() || attestationId.isBlank()) return@withContext null

        val jsonBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "getSignaturesForAddress")
            put("params", JSONArray().apply {
                put(userWalletBase58)
                put(JSONObject().apply {
                    put("limit", 10)
                    put("commitment", "confirmed")
                })
            })
        }.toString()

        val request = Request.Builder()
            .url("https://api.devnet.solana.com")
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("RPC request failed with code ${response.code}")
        }
        val responseString = response.body?.string()
            ?: throw IOException("Empty response from Devnet RPC")

        val jsonResponse = JSONObject(responseString)
        if (jsonResponse.has("error")) {
            val errorObj = jsonResponse.optJSONObject("error")
            val errMsg = errorObj?.optString("message") ?: "RPC Error"
            throw IOException("Devnet RPC error: $errMsg")
        }

        val resultArr = jsonResponse.optJSONArray("result") ?: return@withContext null
        val targetPattern = "\"id\":\"$attestationId\""

        val candidateSignatures = mutableListOf<String>()

        for (i in 0 until resultArr.length()) {
            val item = resultArr.optJSONObject(i) ?: continue
            val err = item.opt("err")
            if (err != null && err != JSONObject.NULL) continue // Require err == null

            val sig = item.optString("signature", "")
            if (sig.isBlank()) continue

            val memo = item.optString("memo", "")
            // Tier 1: Fast path checking parsed memo in getSignaturesForAddress
            if (memo.contains(targetPattern) || memo.contains(attestationId)) {
                return@withContext sig
            }

            candidateSignatures.add(sig)
        }

        // Tier 2 Fallback: Inspect getTransaction log messages / instructions if memo was null/empty in getSignaturesForAddress
        for (sig in candidateSignatures) {
            val txDetails = getTransactionDetails(sig) ?: continue
            if (txDetails.contains(targetPattern) || txDetails.contains(attestationId)) {
                return@withContext sig
            }
        }

        null
    }

    open suspend fun reconcileOffering(
        userWalletBase58: String,
        offeringId: String
    ): String? = withContext(Dispatchers.IO) {
        if (userWalletBase58.isBlank() || offeringId.isBlank()) return@withContext null

        val jsonBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "getSignaturesForAddress")
            put("params", JSONArray().apply {
                put(userWalletBase58)
                put(JSONObject().apply {
                    put("limit", 10)
                    put("commitment", "confirmed")
                })
            })
        }.toString()

        val request = Request.Builder()
            .url("https://api.devnet.solana.com")
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("RPC request failed with code ${response.code}")
        }
        val responseString = response.body?.string()
            ?: throw IOException("Empty response from Devnet RPC")

        val jsonResponse = JSONObject(responseString)
        if (jsonResponse.has("error")) {
            val errorObj = jsonResponse.optJSONObject("error")
            val errMsg = errorObj?.optString("message") ?: "RPC Error"
            throw IOException("Devnet RPC error: $errMsg")
        }

        val resultArr = jsonResponse.optJSONArray("result") ?: return@withContext null
        val targetPattern = "\"id\":\"$offeringId\""

        val candidateSignatures = mutableListOf<String>()

        for (i in 0 until resultArr.length()) {
            val item = resultArr.optJSONObject(i) ?: continue
            val err = item.opt("err")
            if (err != null && err != JSONObject.NULL) continue // Require err == null

            val sig = item.optString("signature", "")
            if (sig.isBlank()) continue

            val memo = item.optString("memo", "")
            // Tier 1: Fast path checking parsed memo in getSignaturesForAddress
            if (memo.contains(targetPattern) || memo.contains(offeringId)) {
                return@withContext sig
            }

            candidateSignatures.add(sig)
        }

        // Tier 2 Fallback: Inspect getTransaction log messages / instructions if memo was null/empty in getSignaturesForAddress
        for (sig in candidateSignatures) {
            val txDetails = getTransactionDetails(sig) ?: continue
            if (txDetails.contains(targetPattern) || txDetails.contains(offeringId)) {
                return@withContext sig
            }
        }

        null
    }

    open suspend fun sendOracleOffering(
        sender: ActivityResultSender,
        blockhash: String,
        treasuryAddress: String = SolanaAppConfig.DEVNET_ORACLE_TREASURY,
        amountLamports: Long = SolanaAppConfig.OFFERING_LAMPORTS,
        offeringId: String? = null,
        onAuthorizedWallet: ((walletBase58: String) -> Unit)? = null,
        onSuccess: (offeringTxSig: String) -> Unit,
        onError: (String) -> Unit,
        onErrorOutcome: ((TransactionFailureOutcome) -> Unit)? = null
    ) {
        if (!SolanaWalletGatekeeper.isValidBase58PublicKey(treasuryAddress)) {
            onError("Developer Devnet treasury address is not configured. Please set DEVNET_ORACLE_TREASURY in SolanaAppConfig.")
            return
        }
        require(blockhash.isNotBlank()) { "Blockhash cannot be blank." }

        val actualOfferingId = offeringId ?: "OFFERING_${UUID.randomUUID()}"
        val opStartTs = System.currentTimeMillis()

        var countOnAuthorizedWallet = 0
        var countOnSuccess = 0
        var countOnError = 0
        var countOnErrorOutcome = 0

        val wrappedOnAuthorizedWallet: ((String) -> Unit)? = if (onAuthorizedWallet != null) {
            { wallet ->
                countOnAuthorizedWallet++
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=CALLBACK_INVOKED callback=onAuthorizedWallet hasAuthorizedWallet=true callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                }
                onAuthorizedWallet.invoke(wallet)
            }
        } else null

        val wrappedOnSuccess: (String) -> Unit = { sig ->
            countOnSuccess++
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=CALLBACK_INVOKED callback=onSuccess hasCapturedSignature=true callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            onSuccess(sig)
        }

        val wrappedOnError: (String) -> Unit = { msg ->
            countOnError++
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=CALLBACK_INVOKED callback=onError message=\"$msg\" callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            onError(msg)
        }

        val wrappedOnErrorOutcome: ((TransactionFailureOutcome) -> Unit)? = if (onErrorOutcome != null) {
            { outcome ->
                countOnErrorOutcome++
                val outcomeName = outcome::class.java.simpleName
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=CALLBACK_INVOKED callback=onErrorOutcome outcome=$outcomeName message=\"${outcome.message}\" callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                }
                onErrorOutcome.invoke(outcome)
            }
        } else null

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=OPERATION_START ts=$opStartTs")
        }

        val treasuryPublicKey = SolanaPublicKey.from(treasuryAddress)

        val memoJson = JSONObject().apply {
            put("id", actualOfferingId)
            put("app", "MTK-Oracle-Offering")
            put("ts", System.currentTimeMillis() / 1000L)
        }.toString()

        var currentAttemptOfferingSignature: String? = null

        try {
            val mwaScope = CoroutineScope(Dispatchers.IO + Job())
            val deferred = mwaScope.async {
                walletAdapter.transact(sender) { authResult ->
                    val account = authResult.accounts.firstOrNull()
                        ?: throw IllegalStateException("No accounts found in connected wallet.")

                    val userPublicKey = SolanaPublicKey(account.publicKey)
                    val userPkBase58 = SolanaWalletGatekeeper.encodeToBase58(userPublicKey.bytes)
                    wrappedOnAuthorizedWallet?.invoke(userPkBase58)

                    val transferInstruction = SystemProgram.transfer(
                        fromPublicKey = userPublicKey,
                        toPublickKey = treasuryPublicKey,
                        lamports = amountLamports
                    )

                    val memoProgramId = SolanaPublicKey.from("MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr")
                    val memoAccountMeta = AccountMeta(userPublicKey, isSigner = true, isWritable = true)
                    val memoInstruction = TransactionInstruction(
                        programId = memoProgramId,
                        accounts = listOf(memoAccountMeta),
                        data = memoJson.toByteArray(Charsets.UTF_8)
                    )

                    val message = Message.Builder()
                        .addInstruction(transferInstruction)
                        .addInstruction(memoInstruction)
                        .setRecentBlockhash(blockhash)
                        .build()

                    val unsignedTx = Transaction(message)
                    val serializedTx = unsignedTx.serialize()

                    val signResult = signAndSendTransactions(arrayOf(serializedTx))
                    val rawSig = signResult.signatures.firstOrNull()
                        ?: throw IllegalStateException("No signature returned from wallet.")

                    val sigBase58 = SolanaWalletGatekeeper.encodeToBase58(rawSig)
                    currentAttemptOfferingSignature = sigBase58
                    sigBase58
                }
            }

            val result = try {
                withTimeoutOrNull(25_000L) {
                    deferred.await()
                }
            } finally {
                deferred.cancel()
            }

            if (result == null) {
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=MWA_TIMEOUT path=\"withTimeoutOrNull returning null\" timeoutMs=25000 hasCapturedSignature=${!currentAttemptOfferingSignature.isNullOrBlank()} callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                }
                val outcome = TransactionFailureOutcome.UnknownTransactionOutcome(
                    message = "Unable to confirm the Oracle offering. Verifying on-chain..."
                )
                if (wrappedOnErrorOutcome != null) {
                    wrappedOnErrorOutcome.invoke(outcome)
                } else {
                    wrappedOnError("Unable to confirm the Oracle offering. Verifying on-chain...")
                }
                return
            }

            when (result) {
                is TransactionResult.Success -> {
                    currentAttemptOfferingSignature = result.payload
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=MWA_SUCCESS path=\"TransactionResult.Success\" hasCapturedSignature=true callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                    }
                    wrappedOnSuccess(result.payload)
                }
                is TransactionResult.NoWalletFound -> {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=MWA_NO_WALLET path=\"TransactionResult.NoWalletFound\" hasCapturedSignature=${!currentAttemptOfferingSignature.isNullOrBlank()} callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                    }
                    val msg = "No Solana wallet app found. Install Phantom or Solflare."
                    val outcome = TransactionFailureOutcome.UnknownTransactionOutcome(msg)
                    if (wrappedOnErrorOutcome != null) {
                        wrappedOnErrorOutcome.invoke(outcome)
                    } else {
                        wrappedOnError(msg)
                    }
                }
                is TransactionResult.Failure -> {
                    val jsonRpcCode = result.e.findJsonRpcRemoteException()?.code
                    val outcome = classifyTransactionFailure(
                        e = result.e,
                        capturedSignature = currentAttemptOfferingSignature,
                        defaultCancelMessage = "The Oracle offering was cancelled.",
                        defaultErrorMessage = "Unable to confirm the Oracle offering. Verifying on-chain...",
                        isFromMwaFailureResult = true
                    )
                    val classificationName = outcome::class.java.simpleName
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=MWA_FAILURE path=\"TransactionResult.Failure\" exceptionClass=${result.e::class.java.name} exceptionMessage=\"${result.e.message}\" jsonRpcCode=${jsonRpcCode ?: "none"} causeChainClasses=\"${result.e.getCauseChainClassesString()}\" causeChainMessages=\"${result.e.getCauseChainMessagesString()}\" hasCapturedSignature=${!currentAttemptOfferingSignature.isNullOrBlank()} classification=$classificationName callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
                    }
                    val isCancelled = outcome is TransactionFailureOutcome.ExplicitUserDeclined
                    val errorMsg = if (isCancelled) {
                        "The Oracle offering was cancelled."
                    } else {
                        outcome.message
                    }
                    if (wrappedOnErrorOutcome != null) {
                        wrappedOnErrorOutcome.invoke(outcome)
                    } else {
                        wrappedOnError(errorMsg)
                    }
                }
            }
        } catch (e: CancellationException) {
            val jsonRpcCode = e.findJsonRpcRemoteException()?.code
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=MWA_CANCELLATION path=\"coroutine cancellation path\" exceptionClass=${e::class.java.name} exceptionMessage=\"${e.message}\" jsonRpcCode=${jsonRpcCode ?: "none"} causeChainClasses=\"${e.getCauseChainClassesString()}\" causeChainMessages=\"${e.getCauseChainMessagesString()}\" hasCapturedSignature=${!currentAttemptOfferingSignature.isNullOrBlank()} callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            throw e
        } catch (e: Exception) {
            val jsonRpcCode = e.findJsonRpcRemoteException()?.code
            val outcome = classifyTransactionFailure(
                e = e,
                capturedSignature = currentAttemptOfferingSignature,
                defaultCancelMessage = "The Oracle offering was cancelled.",
                defaultErrorMessage = "Unable to confirm the Oracle offering. Verifying on-chain...",
                isFromMwaFailureResult = false
            )
            val classificationName = outcome::class.java.simpleName
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$actualOfferingId event=MWA_OUTER_EXCEPTION path=\"outer exception/catch\" exceptionClass=${e::class.java.name} exceptionMessage=\"${e.message}\" jsonRpcCode=${jsonRpcCode ?: "none"} causeChainClasses=\"${e.getCauseChainClassesString()}\" causeChainMessages=\"${e.getCauseChainMessagesString()}\" hasCapturedSignature=${!currentAttemptOfferingSignature.isNullOrBlank()} classification=$classificationName callbackCounts=\"onAuthorizedWallet=$countOnAuthorizedWallet, onSuccess=$countOnSuccess, onError=$countOnError, onErrorOutcome=$countOnErrorOutcome\"")
            }
            val isCancelled = outcome is TransactionFailureOutcome.ExplicitUserDeclined
            val errorMsg = if (isCancelled) {
                "The Oracle offering was cancelled."
            } else {
                outcome.message
            }
            if (wrappedOnErrorOutcome != null) {
                wrappedOnErrorOutcome.invoke(outcome)
            } else {
                wrappedOnError(errorMsg)
            }
        }
    }

    open suspend fun getSignatureStatus(signature: String): SignatureStatusResult = withContext(Dispatchers.IO) {
        val jsonBody = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "getSignatureStatuses")
            put("params", JSONArray().apply {
                put(JSONArray().put(signature))
                put(JSONObject().apply {
                    put("searchTransactionHistory", true)
                })
            })
        }.toString()

        val request = Request.Builder()
            .url("https://api.devnet.solana.com")
            .post(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("RPC request failed with code ${response.code}")
        }
        val responseString = response.body?.string()
            ?: throw IOException("Empty response from Devnet RPC")

        val jsonResponse = JSONObject(responseString)
        if (jsonResponse.has("error")) {
            val errorObj = jsonResponse.optJSONObject("error")
            val errMsg = errorObj?.optString("message") ?: "RPC Error"
            throw IOException("Devnet RPC error: $errMsg")
        }

        val resultObj = jsonResponse.optJSONObject("result")
        val valueArray = resultObj?.optJSONArray("value")
        if (valueArray == null || valueArray.isNull(0)) {
            return@withContext SignatureStatusResult(SignatureConfirmationStatus.NOT_FOUND)
        }

        val statusObj = valueArray.optJSONObject(0)
            ?: return@withContext SignatureStatusResult(SignatureConfirmationStatus.NOT_FOUND)

        if (!statusObj.isNull("err")) {
            val errVal = statusObj.get("err").toString()
            return@withContext SignatureStatusResult(
                status = SignatureConfirmationStatus.FAILED,
                error = errVal
            )
        }

        val confirmationStatus = statusObj.optString("confirmationStatus", "")
        val mappedStatus = when (confirmationStatus.lowercase()) {
            "finalized" -> SignatureConfirmationStatus.FINALIZED
            "confirmed" -> SignatureConfirmationStatus.CONFIRMED
            "processed" -> SignatureConfirmationStatus.PROCESSED
            else -> SignatureConfirmationStatus.PROCESSED
        }

        SignatureStatusResult(status = mappedStatus)
    }

    open suspend fun pollSignatureConfirmation(
        signature: String,
        timeoutMs: Long = 35_000L,
        pollIntervalMs: Long = 1000L
    ): SignatureStatusResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                val result = getSignatureStatus(signature)
                if (result.status == SignatureConfirmationStatus.CONFIRMED ||
                    result.status == SignatureConfirmationStatus.FINALIZED ||
                    result.status == SignatureConfirmationStatus.FAILED) {
                    return@withContext result
                }
            } catch (e: Exception) {
                Log.w("SolanaWalletManager", "Error polling signature status, retrying...", e)
            }
            delay(pollIntervalMs)
        }
        SignatureStatusResult(SignatureConfirmationStatus.NOT_FOUND)
    }
}
