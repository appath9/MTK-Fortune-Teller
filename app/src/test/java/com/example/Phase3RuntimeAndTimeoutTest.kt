package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.model.SealingState
import com.example.ui.FortuneViewModel
import com.example.util.SignatureConfirmationStatus
import com.example.util.SignatureStatusResult
import com.example.util.SolanaWalletManager
import com.example.util.TransactionFailureOutcome
import com.example.util.classifyTransactionFailure
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.protocol.JsonRpc20Client
import com.solana.mobilewalletadapter.common.ProtocolContract
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class Phase3RuntimeAndTimeoutTest {

    private lateinit var application: Application
    private lateinit var preferences: FortunePreferences

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        preferences = FortunePreferences(application)
        preferences.lastReadingDate = null
        preferences.dailyReadingCount = 0
    }

    private class TestWalletManager : SolanaWalletManager() {
        var walletAddressToReturn: String = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"

        var onRecordOracleAttestation: ((
            sender: ActivityResultSender,
            questionId: String,
            chosenNumber: Int,
            blockhash: String,
            attestationId: String?,
            onAuthorizedWallet: ((walletBase58: String) -> Unit)?,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit,
            onErrorOutcome: ((TransactionFailureOutcome) -> Unit)?
        ) -> Unit)? = null

        var onSendOracleOffering: ((
            sender: ActivityResultSender,
            blockhash: String,
            treasuryAddress: String,
            amountLamports: Long,
            offeringId: String?,
            onAuthorizedWallet: ((walletBase58: String) -> Unit)?,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit,
            onErrorOutcome: ((TransactionFailureOutcome) -> Unit)?
        ) -> Unit)? = null

        val reconcileAttestationCalls = mutableListOf<Pair<String, String>>()
        val reconcileOfferingCalls = mutableListOf<Pair<String, String>>()

        var reconcileAttestationHandler: ((walletAddress: String, attestationId: String) -> String?)? = null
        var reconcileOfferingHandler: ((walletAddress: String, offeringId: String) -> String?)? = null

        override suspend fun fetchLatestBlockhash(): String = "11111111111111111111111111111111"

        override suspend fun recordOracleAttestation(
            sender: ActivityResultSender,
            questionId: String,
            chosenNumber: Int,
            blockhash: String,
            attestationId: String?,
            onAuthorizedWallet: ((walletBase58: String) -> Unit)?,
            onSuccess: (txSignature: String) -> Unit,
            onError: (String) -> Unit,
            onErrorOutcome: ((TransactionFailureOutcome) -> Unit)?
        ) {
            if (walletAddressToReturn.isNotBlank()) {
                onAuthorizedWallet?.invoke(walletAddressToReturn)
            }
            val handler = onRecordOracleAttestation
            if (handler != null) {
                handler(sender, questionId, chosenNumber, blockhash, attestationId, onAuthorizedWallet, onSuccess, onError, onErrorOutcome)
            } else {
                onSuccess("5eyktDefaultAttestationSig1111111111111111111111111111111111111111111111111111111")
            }
        }

        override suspend fun sendOracleOffering(
            sender: ActivityResultSender,
            blockhash: String,
            treasuryAddress: String,
            amountLamports: Long,
            offeringId: String?,
            onAuthorizedWallet: ((walletBase58: String) -> Unit)?,
            onSuccess: (offeringTxSig: String) -> Unit,
            onError: (String) -> Unit,
            onErrorOutcome: ((TransactionFailureOutcome) -> Unit)?
        ) {
            if (walletAddressToReturn.isNotBlank()) {
                onAuthorizedWallet?.invoke(walletAddressToReturn)
            }
            val handler = onSendOracleOffering
            if (handler != null) {
                handler(sender, blockhash, treasuryAddress, amountLamports, offeringId, onAuthorizedWallet, onSuccess, onError, onErrorOutcome)
            } else {
                onSuccess("5eyktDefaultOfferingSig1111111111111111111111111111111111111111111111111111111")
            }
        }

        override suspend fun pollSignatureConfirmation(
            signature: String,
            timeoutMs: Long,
            pollIntervalMs: Long
        ): SignatureStatusResult {
            return SignatureStatusResult(SignatureConfirmationStatus.CONFIRMED, null)
        }

        override suspend fun reconcileAttestation(
            userWalletBase58: String,
            attestationId: String
        ): String? {
            reconcileAttestationCalls.add(userWalletBase58 to attestationId)
            return reconcileAttestationHandler?.invoke(userWalletBase58, attestationId)
        }

        override suspend fun reconcileOffering(
            userWalletBase58: String,
            offeringId: String
        ): String? {
            reconcileOfferingCalls.add(userWalletBase58 to offeringId)
            return reconcileOfferingHandler?.invoke(userWalletBase58, offeringId)
        }
    }

    private fun prepareViewModelInWeb3Mode(): FortuneViewModel {
        val viewModel = FortuneViewModel(application)
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(7)
        return viewModel
    }

    private fun waitForSealingToFinish(viewModel: FortuneViewModel, maxWaitMs: Long = 5000L) {
        val startTime = System.currentTimeMillis()
        while ((viewModel.uiState.value.isSubmittingTransaction || viewModel.uiState.value.isReconcilingAttestation) &&
            System.currentTimeMillis() - startTime < maxWaitMs
        ) {
            Thread.sleep(50)
            ShadowLooper.idleMainLooper()
        }
        Thread.sleep(100)
        ShadowLooper.idleMainLooper()
    }

    private fun waitForOfferingToFinish(viewModel: FortuneViewModel, maxWaitMs: Long = 5000L) {
        val startTime = System.currentTimeMillis()
        while (viewModel.uiState.value.isOfferingInFlight &&
            System.currentTimeMillis() - startTime < maxWaitMs
        ) {
            Thread.sleep(50)
            ShadowLooper.idleMainLooper()
        }
        Thread.sleep(100)
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun testA_SealingMwaFailure_CancellationException_NoCapturedSignature() = runBlocking {
        val exc = CancellationException("User declined transaction in MWA")
        val outcome = classifyTransactionFailure(
            e = exc,
            capturedSignature = null,
            defaultCancelMessage = "The Oracle attestation was cancelled.",
            isFromMwaFailureResult = true
        )

        assertTrue(
            "MWA Failure CancellationException with no captured signature must classify as ExplicitUserDeclined",
            outcome is TransactionFailureOutcome.ExplicitUserDeclined
        )
        assertEquals("The Oracle attestation was cancelled.", outcome.message)

        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = TestWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, _, _, onErrorOutcome ->
                onErrorOutcome?.invoke(outcome)
            }
            reconcileAttestationHandler = { _, _ -> null }
        }

        var emittedError: String? = null
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = { err -> emittedError = err }
        )

        waitForSealingToFinish(viewModel)

        val state = viewModel.uiState.value
        assertEquals(SealingState.ERROR, state.sealingState)
        assertNotNull(emittedError)
        assertEquals(0, fakeWalletManager.reconcileAttestationCalls.size)
    }

    @Test
    fun testB_SealingMwaFailure_CancellationException_CapturedSignaturePresent() = runBlocking {
        val exc = CancellationException("User cancelled after signature captured")
        val capturedSig = "5eyktCapturedSig11111111111111111111111111111111111111111111111111111111111"
        val outcome = classifyTransactionFailure(
            e = exc,
            capturedSignature = capturedSig,
            defaultCancelMessage = "The Oracle attestation was cancelled.",
            isFromMwaFailureResult = true
        )

        assertTrue(
            "MWA Failure CancellationException with captured signature MUST classify as UnknownTransactionOutcome",
            outcome is TransactionFailureOutcome.UnknownTransactionOutcome
        )

        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = TestWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, _, _, onErrorOutcome ->
                onErrorOutcome?.invoke(outcome)
            }
            reconcileAttestationHandler = { _, _ -> capturedSig }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForSealingToFinish(viewModel)

        assertTrue(
            "Reconciliation must be preserved when signature was captured",
            fakeWalletManager.reconcileAttestationCalls.size > 0
        )
        assertEquals(SealingState.CONFIRMED, viewModel.uiState.value.sealingState)
    }

    @Test
    fun testC_Sealing_UnrelatedException() = runBlocking {
        val networkExc = IOException("Network socket closed")
        val outcome = classifyTransactionFailure(
            e = networkExc,
            capturedSignature = null,
            defaultCancelMessage = "The Oracle attestation was cancelled.",
            defaultErrorMessage = "Network socket closed",
            isFromMwaFailureResult = true
        )

        assertTrue(
            "Unrelated Exception must classify as UnknownTransactionOutcome",
            outcome is TransactionFailureOutcome.UnknownTransactionOutcome
        )
        assertEquals("Network socket closed", outcome.message)
    }

    @Test
    fun testE_Sealing_ErrorNotSigned_ReturnsExplicitUserDeclined() = runBlocking {
        val jsonRpcExc = JsonRpc20Client.JsonRpc20RemoteException(
            ProtocolContract.ERROR_NOT_SIGNED,
            "User declined to sign",
            null
        )
        val outcome = classifyTransactionFailure(
            e = jsonRpcExc,
            capturedSignature = null,
            defaultCancelMessage = "The Oracle attestation was cancelled.",
            isFromMwaFailureResult = true
        )

        assertTrue(
            "ERROR_NOT_SIGNED JSON-RPC exception MUST classify as ExplicitUserDeclined",
            outcome is TransactionFailureOutcome.ExplicitUserDeclined
        )
        assertEquals("The Oracle attestation was cancelled.", outcome.message)
    }

    @Test
    fun testD_Sealing_OuterCoroutineCancellation_Or_TimeoutGenerated() = runBlocking {
        val outerExc = CancellationException("ViewModel scope cancelled")
        val outerOutcome = classifyTransactionFailure(
            e = outerExc,
            capturedSignature = null,
            defaultCancelMessage = "The Oracle attestation was cancelled.",
            isFromMwaFailureResult = false
        )

        assertTrue(
            "CancellationException with no captured signature classifies as ExplicitUserDeclined",
            outerOutcome is TransactionFailureOutcome.ExplicitUserDeclined
        )

        val timeoutExc = try {
            withTimeout(1L) { delay(1000L) }
            error("Unreachable")
        } catch (e: TimeoutCancellationException) {
            e
        }

        val timeoutOutcome = classifyTransactionFailure(
            e = timeoutExc,
            capturedSignature = null,
            defaultCancelMessage = "The Oracle attestation was cancelled.",
            isFromMwaFailureResult = true
        )

        assertTrue(
            "TimeoutCancellationException MUST NOT classify as ExplicitUserDeclined",
            timeoutOutcome is TransactionFailureOutcome.UnknownTransactionOutcome
        )
    }

    @Test
    fun testE_OracleOffering_Timeout_MapsToUnknownOutcome_PreservesReconciliation() = runBlocking {
        val timeoutOutcome = TransactionFailureOutcome.UnknownTransactionOutcome(
            message = "The Oracle offering was cancelled."
        )

        val viewModel = FortuneViewModel(application)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = TestWalletManager().apply {
            onSendOracleOffering = { _, _, _, _, _, _, _, _, onErrorOutcome ->
                onErrorOutcome?.invoke(timeoutOutcome)
            }
            reconcileOfferingHandler = { _, _ -> null }
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        assertTrue(
            "Reconciliation path must be preserved on offering timeout",
            fakeWalletManager.reconcileOfferingCalls.size > 0
        )
        assertEquals("The Oracle offering was cancelled.", viewModel.uiState.value.offeringError)
    }

    @Test
    fun testF_OracleOffering_SuccessfulTransaction_Unchanged() = runBlocking {
        val viewModel = FortuneViewModel(application)
        val dummySender = ActivityResultSender(ComponentActivity())
        val expectedSig = "5eyktOfferingSuccessSig1111111111111111111111111111111111111111111111111111111"
        val fakeWalletManager = TestWalletManager().apply {
            onSendOracleOffering = { _, _, _, _, _, _, onSuccess, _, _ ->
                onSuccess(expectedSig)
            }
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        val state = viewModel.uiState.value
        assertTrue("Offering must be confirmed on success", state.offeringConfirmed)
        assertFalse("Offering must not be in flight when confirmed", state.isOfferingInFlight)
        assertEquals(expectedSig, state.offeringTxSignature)
        assertNull("offeringError must be null on success", state.offeringError)
    }

    @Test
    fun testG_OracleOffering_LateResultAfterTimeout_NoDuplicateTerminalResult() = runBlocking {
        val viewModel = FortuneViewModel(application)
        val dummySender = ActivityResultSender(ComponentActivity())
        val expectedSig = "5eyktOfferingSuccessSig1111111111111111111111111111111111111111111111111111111"

        var savedOnErrorOutcome: ((TransactionFailureOutcome) -> Unit)? = null
        var savedOnSuccess: ((String) -> Unit)? = null

        val fakeWalletManager = TestWalletManager().apply {
            onSendOracleOffering = { _, _, _, _, _, _, onSuccess, _, onErrorOutcome ->
                savedOnSuccess = onSuccess
                savedOnErrorOutcome = onErrorOutcome
            }
            reconcileOfferingHandler = { _, _ -> null }
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        // 1. Trigger timeout outcome
        savedOnErrorOutcome?.invoke(TransactionFailureOutcome.UnknownTransactionOutcome("The Oracle offering was cancelled."))
        waitForOfferingToFinish(viewModel)

        val stateAfterTimeout = viewModel.uiState.value
        assertEquals("The Oracle offering was cancelled.", stateAfterTimeout.offeringError)
        assertFalse(stateAfterTimeout.offeringConfirmed)

        // 2. Late success result arrives after terminal state is already reached
        savedOnSuccess?.invoke(expectedSig)
        ShadowLooper.idleMainLooper()

        // 3. State must remain in error (late success ignored because state is terminal)
        val stateAfterLateSuccess = viewModel.uiState.value
        assertEquals("Late success must not overwrite terminal error state", "The Oracle offering was cancelled.", stateAfterLateSuccess.offeringError)
        assertFalse("Late success must not set offeringConfirmed on terminal error", stateAfterLateSuccess.offeringConfirmed)
    }
}
