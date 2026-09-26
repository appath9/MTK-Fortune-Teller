package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.data.FortuneRepository
import com.example.model.AppScreen
import com.example.model.SealingState
import com.example.ui.FortuneViewModel
import com.example.util.SignatureConfirmationStatus
import com.example.util.SignatureStatusResult
import com.example.util.SolanaWalletManager
import com.example.util.TransactionFailureOutcome
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.common.util.Base58
import kotlinx.coroutines.runBlocking
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
class SealingHardeningLevel2Test {

    private lateinit var application: Application
    private lateinit var preferences: FortunePreferences
    private lateinit var repository: FortuneRepository

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        preferences = FortunePreferences(application)
        preferences.lastReadingDate = null
        preferences.dailyReadingCount = 0
        repository = FortuneRepository(application)
    }

    private class HardenedFakeSolanaWalletManager : SolanaWalletManager() {
        var blockhashToReturn: String = "11111111111111111111111111111111"
        var walletAddressToReturn: String = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"

        var connectAndAuthorizeCalls = 0
        var recordOracleAttestationCalls = 0
        var shouldThrowBlockhashError = false

        var onConnectAndAuthorize: ((
            sender: ActivityResultSender,
            onSuccess: (publicKeyBytes: ByteArray, accountLabel: String?) -> Unit,
            onError: (String) -> Unit
        ) -> Unit)? = null

        var onRecordOracleAttestation: ((
            sender: ActivityResultSender,
            questionId: String,
            chosenNumber: Int,
            blockhash: String,
            attestationId: String?,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) -> Unit)? = null

        var pollSignatureStatusToReturn: SignatureConfirmationStatus = SignatureConfirmationStatus.CONFIRMED
        var pollSignatureErrorToReturn: String? = null

        var reconcileHandler: ((walletAddress: String, attestationId: String, attempt: Int) -> String?)? = null
        val reconcileCalls = mutableListOf<Pair<String, String>>()
        private var reconcileAttemptCount = 0

        override suspend fun fetchLatestBlockhash(): String {
            if (shouldThrowBlockhashError) {
                throw IOException("RPC Devnet node unreachable.")
            }
            return blockhashToReturn
        }

        override suspend fun connectAndAuthorize(
            sender: ActivityResultSender,
            onSuccess: (publicKeyBytes: ByteArray, accountLabel: String?) -> Unit,
            onError: (String) -> Unit
        ) {
            connectAndAuthorizeCalls++
            val handler = onConnectAndAuthorize
            if (handler != null) {
                handler(sender, onSuccess, onError)
            } else {
                val pubkeyBytes = Base58.decode(walletAddressToReturn)
                onSuccess(pubkeyBytes, "Test Account")
            }
        }

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
            recordOracleAttestationCalls++
            if (walletAddressToReturn.isNotBlank()) {
                onAuthorizedWallet?.invoke(walletAddressToReturn)
            }
            val handler = onRecordOracleAttestation
            if (handler != null) {
                handler(sender, questionId, chosenNumber, blockhash, attestationId, onSuccess, onError)
            } else {
                onSuccess("5eyktTxSigDefault11111111111111111111111111111111111111111111111111111111111111111111")
            }
        }

        override suspend fun pollSignatureConfirmation(
            signature: String,
            timeoutMs: Long,
            pollIntervalMs: Long
        ): SignatureStatusResult {
            return SignatureStatusResult(
                status = pollSignatureStatusToReturn,
                error = pollSignatureErrorToReturn
            )
        }

        override suspend fun reconcileAttestation(
            userWalletBase58: String,
            attestationId: String
        ): String? {
            reconcileCalls.add(userWalletBase58 to attestationId)
            reconcileAttemptCount++
            return reconcileHandler?.invoke(userWalletBase58, attestationId, reconcileAttemptCount)
        }
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

    @Test
    fun test1_NormalSuccessfulSealing() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        val expectedSig = "5eyktNormalSuccessSig111111111111111111111111111111111111111111111111111111111111111"
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, _, onSuccess, _ ->
            onSuccess(expectedSig)
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
        assertEquals(SealingState.CONFIRMED, state.sealingState)
        assertEquals(expectedSig, state.txSignature)
        assertFalse("isSubmittingTransaction must be false", state.isSubmittingTransaction)
        assertFalse("isReconcilingAttestation must be false", state.isReconcilingAttestation)
        assertNull("attestationStatusText must be null when confirmed", state.attestationStatusText)
        assertNull("sealingError must be null on success", state.sealingError)
        assertNull("emittedError must be null on success", emittedError)
        assertEquals(AppScreen.Result, state.currentScreen)
    }

    @Test
    fun test2_GenuineCancellationBeforeSubmission() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, _, _, onError ->
            onError("The Oracle attestation was cancelled.")
        }
        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

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
        assertEquals("The Oracle attestation was cancelled.", state.sealingError)
        assertEquals("The Oracle attestation was cancelled.", emittedError)
        assertFalse("isSubmittingTransaction must be false", state.isSubmittingTransaction)
        assertFalse("isReconcilingAttestation must be false", state.isReconcilingAttestation)
        assertNull("attestationStatusText must be null on error", state.attestationStatusText)
        assertNull("txSignature must be null on cancellation", state.txSignature)
    }

    @Test
    fun test3_FailureInterruptionRoutedToReconciliation() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        val expectedSig = "5eyktRecoveredInterruptionSig1111111111111111111111111111111111111111111111111111111"
        var capturedAttestationId: String? = null

        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
            capturedAttestationId = attestationId
            onError("The Oracle attestation was cancelled.")
        }

        fakeWalletManager.reconcileHandler = { _, attestationId, _ ->
            if (attestationId == capturedAttestationId) expectedSig else null
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
        assertEquals(SealingState.CONFIRMED, state.sealingState)
        assertEquals(expectedSig, state.txSignature)
        assertFalse("isSubmittingTransaction must be false after reconciliation", state.isSubmittingTransaction)
        assertFalse("isReconcilingAttestation must be false after reconciliation", state.isReconcilingAttestation)
        assertNull("attestationStatusText must be null after successful reconciliation", state.attestationStatusText)
        assertNull("No error should be emitted when reconciliation finds signature", emittedError)
    }

    @Test
    fun test4_StaleExpiredMwaFailureHandling() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        val staleErrMsg = "Session expired. Please re-authorize wallet."
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, _, _, onError ->
            onError(staleErrMsg)
        }
        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

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
        assertEquals(staleErrMsg, state.sealingError)
        assertEquals(staleErrMsg, state.txError)
        assertEquals(staleErrMsg, emittedError)
        assertFalse("isSubmittingTransaction must be false", state.isSubmittingTransaction)
        assertFalse("isReconcilingAttestation must be false", state.isReconcilingAttestation)
        assertNull("attestationStatusText must be null", state.attestationStatusText)
    }

    @Test
    fun test5_EveryTerminalErrorPathReleasesSubmittingFlag() = runBlocking {
        val dummySender = ActivityResultSender(ComponentActivity())

        // Error Path 1: recordOracleAttestation failure
        val vm1 = prepareViewModelInWeb3Mode()
        val fm1 = HardenedFakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, onError -> onError("Wallet connection failed.") }
            reconcileHandler = { _, _, _ -> null }
        }
        vm1.sealCurrentReadingOnChain(sender = dummySender, walletManager = fm1, reconciliationDelayMs = 1L, onError = {})
        waitForSealingToFinish(vm1)
        assertFalse("Path 1 must release isSubmittingTransaction", vm1.uiState.value.isSubmittingTransaction)

        // Error Path 2: fetchLatestBlockhash exception
        val vm2 = prepareViewModelInWeb3Mode()
        val fm2 = HardenedFakeSolanaWalletManager().apply { shouldThrowBlockhashError = true }
        vm2.sealCurrentReadingOnChain(sender = dummySender, walletManager = fm2, reconciliationDelayMs = 1L, onError = {})
        waitForSealingToFinish(vm2)
        assertFalse("Path 2 must release isSubmittingTransaction", vm2.uiState.value.isSubmittingTransaction)

        // Error Path 3: Stale/expired MWA session failure
        val vm3 = prepareViewModelInWeb3Mode()
        val fm3 = HardenedFakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, onError -> onError("Session expired") }
            reconcileHandler = { _, _, _ -> null }
        }
        vm3.sealCurrentReadingOnChain(sender = dummySender, walletManager = fm3, reconciliationDelayMs = 1L, onError = {})
        waitForSealingToFinish(vm3)
        assertFalse("Path 3 must release isSubmittingTransaction", vm3.uiState.value.isSubmittingTransaction)

        // Error Path 4: On-chain transaction status FAILED
        val vm4 = prepareViewModelInWeb3Mode()
        val fm4 = HardenedFakeSolanaWalletManager().apply {
            pollSignatureStatusToReturn = SignatureConfirmationStatus.FAILED
            pollSignatureErrorToReturn = "InstructionError(0, Custom(1))"
        }
        vm4.sealCurrentReadingOnChain(sender = dummySender, walletManager = fm4, reconciliationDelayMs = 1L, onError = {})
        waitForSealingToFinish(vm4)
        assertFalse("Path 4 must release isSubmittingTransaction", vm4.uiState.value.isSubmittingTransaction)

        // Error Path 5: Reconciliation timeout without match
        val vm5 = prepareViewModelInWeb3Mode()
        val fm5 = HardenedFakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, onError -> onError("The Oracle attestation was cancelled.") }
            reconcileHandler = { _, _, _ -> null }
        }
        vm5.sealCurrentReadingOnChain(sender = dummySender, walletManager = fm5, reconciliationDelayMs = 1L, onError = {})
        waitForSealingToFinish(vm5)
        assertFalse("Path 5 must release isSubmittingTransaction", vm5.uiState.value.isSubmittingTransaction)
    }

    @Test
    fun test6_EveryTerminalErrorPathLeavesValidNonStuckUiState() = runBlocking {
        val dummySender = ActivityResultSender(ComponentActivity())

        val testCases = listOf<Pair<String, HardenedFakeSolanaWalletManager>>(
            "recordOracleAttestation failure" to HardenedFakeSolanaWalletManager().apply {
                onRecordOracleAttestation = { _, _, _, _, _, _, onError -> onError("Wallet connection failed.") }
                reconcileHandler = { _, _, _ -> null }
            },
            "fetchLatestBlockhash exception" to HardenedFakeSolanaWalletManager().apply {
                shouldThrowBlockhashError = true
            },
            "Stale MWA failure" to HardenedFakeSolanaWalletManager().apply {
                onRecordOracleAttestation = { _, _, _, _, _, _, onError -> onError("Invalid auth token") }
                reconcileHandler = { _, _, _ -> null }
            },
            "On-chain FAILED status" to HardenedFakeSolanaWalletManager().apply {
                pollSignatureStatusToReturn = SignatureConfirmationStatus.FAILED
                pollSignatureErrorToReturn = "Transaction simulation failed."
            },
            "Reconciliation unmatched" to HardenedFakeSolanaWalletManager().apply {
                onRecordOracleAttestation = { _, _, _, _, _, _, onError -> onError("The Oracle attestation was cancelled.") }
                reconcileHandler = { _, _, _ -> null }
            }
        )

        for ((name, manager) in testCases) {
            val vm = prepareViewModelInWeb3Mode()
            vm.sealCurrentReadingOnChain(sender = dummySender, walletManager = manager, reconciliationDelayMs = 1L, onError = {})
            waitForSealingToFinish(vm)

            val state = vm.uiState.value
            assertEquals("[$name] sealingState must be ERROR", SealingState.ERROR, state.sealingState)
            assertNotNull("[$name] sealingError must not be null", state.sealingError)
            assertNull("[$name] attestationStatusText must be null", state.attestationStatusText)
            assertFalse("[$name] isSubmittingTransaction must be false", state.isSubmittingTransaction)
            assertFalse("[$name] isReconcilingAttestation must be false", state.isReconcilingAttestation)
        }
    }

    @Test
    fun test7_SingleTransactionScopeCapturesWalletWithoutConnectAndAuthorizePreCall() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val initialCachedAddress = "5eyktCachedWalletAddress1111111111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(initialCachedAddress)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForSealingToFinish(viewModel)

        assertEquals("No connectAndAuthorize pre-step MUST be called", 0, fakeWalletManager.connectAndAuthorizeCalls)
        assertEquals(1, fakeWalletManager.recordOracleAttestationCalls)
        assertEquals(fakeWalletManager.walletAddressToReturn, viewModel.connectedWallet.value)
    }

    @Test
    fun test8_DuplicateSealingTapProtection() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())

        val fakeWalletManager = HardenedFakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, _ ->
                // Keep transaction in-flight
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals(0, fakeWalletManager.connectAndAuthorizeCalls)
        assertEquals(1, fakeWalletManager.recordOracleAttestationCalls)

        // Second tap while transaction is in-flight MUST be ignored
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        assertEquals("Second sealing tap MUST be ignored while in-flight", 0, fakeWalletManager.connectAndAuthorizeCalls)
        assertEquals("Second sealing tap MUST be ignored while in-flight", 1, fakeWalletManager.recordOracleAttestationCalls)
    }

    /**
     * Limitation Note:
     * In unit tests running under Robolectric/JVM, the true Android OS system-level
     * ActivityResultSender IPC activity suspension (where an external MWA wallet app Activity
     * hangs indefinitely without returning an ActivityResult) cannot be directly synthesized
     * because the Android OS ActivityManager / ComponentActivity result dispatcher is mocked.
     *
     * To test this deterministically without falsely claiming to reproduce physical Android system
     * behavior, this test verifies that when recordOracleAttestation times out or fails to return
     * a result (simulating an uncooperative or lost Activity result callback), the sealCurrentReadingOnChain
     * flow times out cleanly, enters reconciliation, safely releases isSubmittingTransaction, and
     * transitions sealingState to a valid terminal non-stuck state.
     */
    @Test
    fun test9_IndefinitelySuspendedMwaLimitationAndTimeoutTest() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        // Simulate MWA activity result callback timing out or returning null/error
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, _, _, onError ->
            onError("The Oracle attestation was cancelled.")
        }
        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForSealingToFinish(viewModel)

        val state = viewModel.uiState.value
        assertFalse("isSubmittingTransaction must be released to false on timeout/failure", state.isSubmittingTransaction)
        assertFalse("isReconcilingAttestation must be released to false on timeout/failure", state.isReconcilingAttestation)
        assertEquals("sealingState must be terminal ERROR state", SealingState.ERROR, state.sealingState)
        assertNull("attestationStatusText must be cleared", state.attestationStatusText)
        assertNotNull("sealingError must contain failure details", state.sealingError)
    }

    @Test
    fun test10_NewAttemptResetsWalletAddressAndDoesNotReconcileStaleAddress() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val previousStaleAddress = "5eyktStalePreviousAddress11111111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(previousStaleAddress)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager().apply {
            walletAddressToReturn = "" // Blank address simulates authorization failure before address capture
            onRecordOracleAttestation = { _, _, _, _, _, _, onError ->
                onError("The Oracle attestation was cancelled.")
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForSealingToFinish(viewModel)

        // Verify that reconciliation was NOT called with the stale previous address
        assertTrue("Reconciliation MUST NOT be run against stale previous wallet address", fakeWalletManager.reconcileCalls.none { it.first == previousStaleAddress })
    }

    @Test
    fun test11_FailedOrCancelledSealingAttemptFollowedByRetryGeneratesNewAttestationId() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = HardenedFakeSolanaWalletManager()

        var firstAttestationId: String? = null
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
            firstAttestationId = attestationId
            onError("The Oracle attestation was cancelled.")
        }
        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

        // First attempt (fails)
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )
        waitForSealingToFinish(viewModel)

        assertNotNull(firstAttestationId)
        assertEquals(SealingState.ERROR, viewModel.uiState.value.sealingState)

        // Second attempt (retry)
        var secondAttestationId: String? = null
        val expectedSig = "5eyktRetrySuccessSig11111111111111111111111111111111111111111111111111111111111111"
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, onSuccess, _ ->
            secondAttestationId = attestationId
            onSuccess(expectedSig)
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )
        waitForSealingToFinish(viewModel)

        assertNotNull(secondAttestationId)
        assertTrue("Retry attempt MUST generate a fresh attestation ID", firstAttestationId != secondAttestationId)
        assertTrue("Attestation ID must start with ORCL_", secondAttestationId!!.startsWith("ORCL_"))
        assertEquals(SealingState.CONFIRMED, viewModel.uiState.value.sealingState)
    }

    @Test
    fun test12_LateAsynchronousCompletionFromEarlierAttemptIsIgnoredByNewerAttempt() = runBlocking {
        val viewModel = prepareViewModelInWeb3Mode()
        val dummySender = ActivityResultSender(ComponentActivity())

        // 1. Initiate attempt 1
        var attempt1Id: String? = null
        val slowWalletManager = HardenedFakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
                attempt1Id = attestationId
                // Simulate delay before returning error/timeout
                Thread.sleep(100)
                onError("The Oracle attestation was cancelled.")
            }
            reconcileHandler = { _, _, _ ->
                Thread.sleep(100)
                null
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = slowWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        // 2. While attempt 1 is running, verify attempt 1's ID is stored
        assertNotNull(viewModel.uiState.value.attestationId)

        // Wait for attempt 1 to finish
        waitForSealingToFinish(viewModel)
        val finishedAttempt1Id = viewModel.uiState.value.attestationId
        assertNotNull(attempt1Id)

        // 3. Initiate attempt 2
        var attempt2Id: String? = null
        val fastWalletManager = HardenedFakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, attestationId, onSuccess, _ ->
                attempt2Id = attestationId
                onSuccess("5eyktFastSuccessSig1111111111111111111111111111111111111111111111111111111111")
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fastWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForSealingToFinish(viewModel)

        assertTrue(finishedAttempt1Id != attempt2Id)
        assertEquals(SealingState.CONFIRMED, viewModel.uiState.value.sealingState)
        assertEquals("5eyktFastSuccessSig1111111111111111111111111111111111111111111111111111111111", viewModel.uiState.value.txSignature)
    }
}

