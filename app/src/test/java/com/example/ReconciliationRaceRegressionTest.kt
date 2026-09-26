package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.data.FortuneRepository
import com.example.model.AppScreen
import com.example.model.SealingState
import com.example.ui.FortuneViewModel
import com.example.util.SolanaWalletManager
import com.example.util.TransactionFailureOutcome
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.common.util.Base58
import com.solana.publickey.SolanaPublicKey
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

@RunWith(RobolectricTestRunner::class)
class ReconciliationRaceRegressionTest {

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

    private class FakeSolanaWalletManager : SolanaWalletManager() {
        var blockhashToReturn: String = "11111111111111111111111111111111"
        var walletAddressToReturn: String = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"

        var connectAndAuthorizeCalls = 0

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

        var onRecordOracleAttestationWithOutcome: ((
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

        var reconcileHandler: ((walletAddress: String, attestationId: String, attempt: Int) -> String?)? = null

        val reconcileCalls = mutableListOf<Pair<String, String>>()
        private var reconcileAttemptCount = 0

        override suspend fun fetchLatestBlockhash(): String = blockhashToReturn

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
            if (walletAddressToReturn.isNotBlank()) {
                onAuthorizedWallet?.invoke(walletAddressToReturn)
            }
            val outcomeHandler = onRecordOracleAttestationWithOutcome
            if (outcomeHandler != null) {
                outcomeHandler(sender, questionId, chosenNumber, blockhash, attestationId, onAuthorizedWallet, onSuccess, onError, onErrorOutcome)
            } else {
                val handler = onRecordOracleAttestation
                if (handler != null) {
                    handler(sender, questionId, chosenNumber, blockhash, attestationId, onSuccess, onError)
                } else {
                    val outcome = TransactionFailureOutcome.UnknownTransactionOutcome("The Oracle attestation was cancelled.")
                    onErrorOutcome?.invoke(outcome)
                    onError("The Oracle attestation was cancelled.")
                }
            }
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

    private fun waitForReconciliationToFinish(viewModel: FortuneViewModel, maxWaitMs: Long = 5000L) {
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

    @Test
    fun testA_PostApprovalInterruptionWithSuccessfulReconciliation() = runBlocking {
        val viewModel = FortuneViewModel(application)

        // Exhaust free readings to enter Web3 mode
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(3)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        val expectedTxSig = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        var capturedAttestationId: String? = null

        // 2. Simulate MWA transact operation being interrupted/cancelled AFTER transaction submission attempt
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
            capturedAttestationId = attestationId
            onError("The Oracle attestation was cancelled.")
        }

        // 4. Mock/fake reconcileAttestation() to return matching Devnet signature for exact attestationId
        fakeWalletManager.reconcileHandler = { wallet, attestationId, _ ->
            if (wallet == walletAddress && attestationId == capturedAttestationId) {
                expectedTxSig
            } else null
        }

        var emittedError: String? = null

        // 3. Initiate sealing flow which enters reconciliation on interruption
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = { err -> emittedError = err }
        )

        waitForReconciliationToFinish(viewModel)

        // 1 & 5. Verify reconciliation received correct wallet address and attestationId
        assertNotNull("attestationId must be non-null", capturedAttestationId)
        assertTrue("reconcileAttestation must have been called", fakeWalletManager.reconcileCalls.isNotEmpty())
        assertEquals(walletAddress, fakeWalletManager.reconcileCalls.first().first)
        assertEquals(capturedAttestationId, fakeWalletManager.reconcileCalls.first().second)

        // 6. Verify matching transaction/signature is accepted by production flow
        assertEquals(expectedTxSig, viewModel.uiState.value.txSignature)

        // 7. Verify sealed reading is persisted to Room
        val persistedRecord = repository.getRecordByAttestationId(capturedAttestationId!!)
        assertNotNull("Confirmed reading MUST be persisted to Room", persistedRecord)
        assertEquals("Q7", persistedRecord?.questionId)
        assertEquals(3, persistedRecord?.chosenNumber)
        assertEquals(expectedTxSig, persistedRecord?.txSignature)

        // 8. Verify final sealing state becomes CONFIRMED
        assertEquals(SealingState.CONFIRMED, viewModel.uiState.value.sealingState)
        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)

        // 9. Verify "The Oracle attestation was cancelled." is NOT emitted
        assertNull("Cancellation error should NOT be emitted when reconciliation succeeds", emittedError)
        assertNull(viewModel.uiState.value.sealingError)
        assertNull(viewModel.uiState.value.txError)

        // 10. Verify confirmed reading remains available through existing history/persistence path
        ShadowLooper.idleMainLooper()
        val recordFromDb = repository.getRecordByAttestationId(capturedAttestationId!!)
        assertNotNull(recordFromDb)
    }

    @Test
    fun testB_GenuineCancellationWithoutMatchingOnChainAttestation() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(3)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        var capturedAttestationId: String? = null

        // 1. Simulate MWA cancellation/interruption
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
            capturedAttestationId = attestationId
            onError("The Oracle attestation was cancelled.")
        }

        // 2. Make reconcileAttestation() return no matching transaction for all bounded reconciliation attempts
        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

        var emittedError: String? = null

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = { err -> emittedError = err }
        )

        waitForReconciliationToFinish(viewModel)

        // Verify bounded 8 attempts were performed
        assertEquals(8, fakeWalletManager.reconcileCalls.size)

        // 3. Verify production flow eventually reports genuine cancellation/error
        assertEquals("The Oracle attestation was cancelled.", emittedError)
        assertEquals("The Oracle attestation was cancelled.", viewModel.uiState.value.sealingError)
        assertEquals("The Oracle attestation was cancelled.", viewModel.uiState.value.txError)
        assertEquals(SealingState.ERROR, viewModel.uiState.value.sealingState)

        // 4. Verify NO Room record is created
        assertNotNull(capturedAttestationId)
        val recordInRoom = repository.getRecordByAttestationId(capturedAttestationId!!)
        assertNull("No Room record should be created for genuine cancellation", recordInRoom)

        // 5. Verify final state is not CONFIRMED
        assertNull(viewModel.uiState.value.txSignature)
        assertTrue(viewModel.uiState.value.sealingState != SealingState.CONFIRMED)
    }

    @Test
    fun testC_ReconciliationDelayWithEventualDiscoveryOnLaterAttempt() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q9")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(5)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        val expectedTxSig = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        var capturedAttestationId: String? = null

        // 1. Simulate MWA interruption
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
            capturedAttestationId = attestationId
            onError("The Oracle attestation was cancelled.")
        }

        // 2 & 3. Make reconcileAttestation() return null on attempts 1 & 2, then matching transaction on attempt 3
        fakeWalletManager.reconcileHandler = { _, _, attempt ->
            if (attempt >= 3) {
                expectedTxSig
            } else {
                null
            }
        }

        var emittedError: String? = null

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = { err -> emittedError = err }
        )

        waitForReconciliationToFinish(viewModel)

        // 4. Verify production retry/reconciliation logic eventually finds it (3 attempts performed)
        assertEquals(3, fakeWalletManager.reconcileCalls.size)

        // 5. Verify reading becomes CONFIRMED and is persisted to Room
        assertEquals(expectedTxSig, viewModel.uiState.value.txSignature)
        assertEquals(SealingState.CONFIRMED, viewModel.uiState.value.sealingState)
        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)

        assertNotNull(capturedAttestationId)
        val persistedRecord = repository.getRecordByAttestationId(capturedAttestationId!!)
        assertNotNull("Confirmed reading MUST be persisted to Room after delayed reconciliation discovery", persistedRecord)
        assertEquals("Q9", persistedRecord?.questionId)
        assertEquals(5, persistedRecord?.chosenNumber)
        assertEquals(expectedTxSig, persistedRecord?.txSignature)

        // 6. Verify it is NOT incorrectly reported as cancellation before later matching transaction is found
        assertNull("Cancellation error must NOT be emitted when reconciliation eventually succeeds", emittedError)
        assertNull(viewModel.uiState.value.sealingError)
        assertNull(viewModel.uiState.value.txError)
    }

    @Test
    fun testD_MwaTimeoutEntersReconciliationAndSucceedsIfOnChainTxExists() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q4")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(2)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        val expectedTxSig = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        var capturedAttestationId: String? = null

        // 1. Simulate MWA timeout after 25s which calls onError("The Oracle attestation was cancelled.")
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, attestationId, _, onError ->
            capturedAttestationId = attestationId
            onError("The Oracle attestation was cancelled.")
        }

        // 2. Reconciler finds the signature submitted on-chain prior to timeout
        fakeWalletManager.reconcileHandler = { wallet, attestationId, _ ->
            if (wallet == walletAddress && attestationId == capturedAttestationId) {
                expectedTxSig
            } else null
        }

        var emittedError: String? = null

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = { err -> emittedError = err }
        )

        waitForReconciliationToFinish(viewModel)

        // 3. Verify reconciliation executed and unblocked stuck state into CONFIRMED
        assertTrue(fakeWalletManager.reconcileCalls.isNotEmpty())
        assertEquals(expectedTxSig, viewModel.uiState.value.txSignature)
        assertEquals(SealingState.CONFIRMED, viewModel.uiState.value.sealingState)
        assertNull("No error should be emitted when timeout reconciliation succeeds", emittedError)

        // 4. Verify reading was persisted to Room
        assertNotNull(capturedAttestationId)
        val persistedRecord = repository.getRecordByAttestationId(capturedAttestationId!!)
        assertNotNull("Confirmed reading MUST be persisted to Room after timeout reconciliation", persistedRecord)
        assertEquals("Q4", persistedRecord?.questionId)
        assertEquals(2, persistedRecord?.chosenNumber)
    }

    @Test
    fun test1_Web3ReadingNormalTransactionSuccess() {
        val viewModel = FortuneViewModel(application)
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()

        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertEquals("Q7", viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun test3_BackDuringReconciliationMustNotClearSelectedQuestionId() {
        val viewModel = FortuneViewModel(application)
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(5)

        val dummySender = ActivityResultSender(ComponentActivity())
        val dummyWalletManager = FakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, _ ->
                // Keep transaction in-flight
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = dummyWalletManager,
            onError = {}
        )

        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals("Q7", viewModel.uiState.value.selectedQuestionId)

        // Trigger Back during submission/reconciliation
        viewModel.handleBack()
        assertEquals("Q7", viewModel.uiState.value.selectedQuestionId)

        // Direct onTryAgain() call during submission/reconciliation is ignored
        viewModel.onTryAgain()
        assertEquals("Q7", viewModel.uiState.value.selectedQuestionId)
    }

    @Test
    fun test4_BackDuringReconciliationMustNotClearFinalChoice() {
        val viewModel = FortuneViewModel(application)
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")
        viewModel.onQuestionHoldConfirmed("Q12")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(8)

        val dummySender = ActivityResultSender(ComponentActivity())
        val dummyWalletManager = FakeSolanaWalletManager().apply {
            onRecordOracleAttestation = { _, _, _, _, _, _, _ ->
                // Keep transaction in-flight
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = dummyWalletManager,
            onError = {}
        )

        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals(8, viewModel.uiState.value.finalChoice)

        viewModel.handleBack()
        assertEquals(8, viewModel.uiState.value.finalChoice)
    }

    @Test
    fun test6_GenuineCancellationBeforeSubmissionDoesNotProduceSuccessfulResultScreen() {
        val viewModel = FortuneViewModel(application)
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")
        viewModel.onQuestionHoldConfirmed("Q3")
        viewModel.proceedToWeb3ModeFromQuotaDialog()

        assertNull(viewModel.uiState.value.txSignature)
        assertFalse(viewModel.uiState.value.currentScreen == AppScreen.Result)
    }

    @Test
    fun test7_StaleSessionNewSealingMustTriggerFreshConnectAndAuthorize() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val cachedWalletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        // 1. Simulate existing cached wallet public key (e.g. from previous session or Oracle Offering)
        viewModel.onWalletConnected(cachedWalletAddress)
        assertNotNull(viewModel.connectedWallet.value)

        viewModel.onQuestionHoldConfirmed("Q10")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(7)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        var recordAttestationCalled = false
        fakeWalletManager.onRecordOracleAttestation = { _, _, _, _, _, onSuccess, _ ->
            recordAttestationCalled = true
            onSuccess("5eyktTxSig111111111111111111111111111111111111111111111111111111111111111111111111111111111")
        }

        // 2. Initiate new sealing transaction when _connectedWallet is already non-null
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            onError = {}
        )

        // 3. Verify sealing does NOT call connectAndAuthorize pre-step, and recordOracleAttestation is called directly
        assertEquals("Single transaction scope MUST NOT invoke connectAndAuthorize pre-step", 0, fakeWalletManager.connectAndAuthorizeCalls)
        assertTrue("recordOracleAttestation MUST be called directly", recordAttestationCalled)
        assertEquals(fakeWalletManager.walletAddressToReturn, viewModel.connectedWallet.value)
    }

    @Test
    fun testProtocolConfirmedErrorNotSigned_ZeroReconciliationCallsAndImmediateCancellation() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(3)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        var capturedAttestationId: String? = null

        fakeWalletManager.onRecordOracleAttestationWithOutcome = { _, _, _, _, attestationId, _, _, onError, onErrorOutcome ->
            capturedAttestationId = attestationId
            val outcome = TransactionFailureOutcome.ExplicitUserDeclined("The Oracle attestation was cancelled.")
            onErrorOutcome?.invoke(outcome)
            onError("The Oracle attestation was cancelled.")
        }

        var emittedError: String? = null

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = { err -> emittedError = err }
        )

        waitForReconciliationToFinish(viewModel)

        assertEquals(0, fakeWalletManager.reconcileCalls.size)
        assertEquals("The Oracle attestation was cancelled.", emittedError)
        assertEquals("The Oracle attestation was cancelled.", viewModel.uiState.value.sealingError)
        assertEquals("The Oracle attestation was cancelled.", viewModel.uiState.value.txError)
        assertEquals(SealingState.ERROR, viewModel.uiState.value.sealingState)
        assertFalse(viewModel.uiState.value.isSubmittingTransaction)

        assertNotNull(capturedAttestationId)
        val recordInRoom = repository.getRecordByAttestationId(capturedAttestationId!!)
        assertNull("No Room record should be created for verified pre-signing cancellation", recordInRoom)
    }

    @Test
    fun testErrorAuthorizationFailed_ExecutesFullReconciliation() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(3)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        fakeWalletManager.onRecordOracleAttestationWithOutcome = { _, _, _, _, _, _, _, onError, onErrorOutcome ->
            val outcome = TransactionFailureOutcome.UnknownTransactionOutcome("Auth token invalid")
            onErrorOutcome?.invoke(outcome)
            onError("Auth token invalid")
        }

        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForReconciliationToFinish(viewModel)

        assertEquals(8, fakeWalletManager.reconcileCalls.size)
    }

    @Test
    fun testInterruptedException_ExecutesFullReconciliation() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(3)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        fakeWalletManager.onRecordOracleAttestationWithOutcome = { _, _, _, _, _, _, _, onError, onErrorOutcome ->
            val outcome = TransactionFailureOutcome.UnknownTransactionOutcome("Request was interrupted")
            onErrorOutcome?.invoke(outcome)
            onError("Request was interrupted")
        }

        fakeWalletManager.reconcileHandler = { _, _, _ -> null }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForReconciliationToFinish(viewModel)

        assertEquals(8, fakeWalletManager.reconcileCalls.size)
    }

    @Test
    fun testStaleSignatureFromPreviousAttempt_DoesNotAffectNewAttempt() = runBlocking {
        val viewModel = FortuneViewModel(application)

        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        val walletAddress = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(walletAddress)
        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        viewModel.selectFinalChoice(3)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeSolanaWalletManager()

        fakeWalletManager.onRecordOracleAttestationWithOutcome = { _, _, _, _, _, _, _, onError, onErrorOutcome ->
            val outcome = TransactionFailureOutcome.ExplicitUserDeclined("The Oracle attestation was cancelled.")
            onErrorOutcome?.invoke(outcome)
            onError("The Oracle attestation was cancelled.")
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForReconciliationToFinish(viewModel)

        assertEquals(0, fakeWalletManager.reconcileCalls.size)
        assertEquals(SealingState.ERROR, viewModel.uiState.value.sealingState)
        assertNull(viewModel.uiState.value.txSignature)

        fakeWalletManager.onRecordOracleAttestationWithOutcome = { _, _, _, _, _, _, _, onError, onErrorOutcome ->
            val outcome = TransactionFailureOutcome.ExplicitUserDeclined("The Oracle attestation was cancelled.")
            onErrorOutcome?.invoke(outcome)
            onError("The Oracle attestation was cancelled.")
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onError = {}
        )

        waitForReconciliationToFinish(viewModel)

        assertEquals(0, fakeWalletManager.reconcileCalls.size)
        assertNull(viewModel.uiState.value.txSignature)
    }
}
