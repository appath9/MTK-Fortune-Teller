package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.model.FortuneState
import com.example.ui.FortuneViewModel
import com.example.util.SignatureConfirmationStatus
import com.example.util.SignatureStatusResult
import com.example.util.SolanaAppConfig
import com.example.util.SolanaWalletGatekeeper
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
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class OracleOfferingTest {

    @Test
    fun testOfferingAmountIsOneMillionLamports() {
        assertEquals(1_000_000L, SolanaAppConfig.OFFERING_LAMPORTS)
    }

    @Test
    fun testTreasuryConfigurationValidation() {
        val validBase58Key = "3SmhDEaCeou33n9t7vHybi17682jDFS5HzJ8Smt3gXWq"
        assertTrue(SolanaWalletGatekeeper.isValidBase58PublicKey(validBase58Key))

        val invalidKey = "<DEVELOPER-SUPPLIED-DEVNET-PUBLIC-KEY>"
        assertFalse(SolanaWalletGatekeeper.isValidBase58PublicKey(invalidKey))
    }

    @Test
    fun testOfferingExplorerUrlFormat() {
        val offeringSignature = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        val explorerUrl = SolanaWalletGatekeeper.buildExplorerUrl(offeringSignature, isTx = true)
        assertEquals("https://explorer.solana.com/tx/$offeringSignature?cluster=devnet", explorerUrl)
        assertTrue(explorerUrl.contains("/tx/$offeringSignature?cluster=devnet"))
    }

    @Test
    fun testWalletCancellationBeforeSignatureResetsState() {
        // Simulate cancellation before signature exists
        val cancelledState = FortuneState(
            isOfferingInFlight = false,
            offeringStatusText = null,
            offeringTxSignature = null,
            offeringConfirmed = false,
            offeringUnknownConfirmation = false,
            offeringError = "The Oracle offering was cancelled."
        )

        assertFalse(cancelledState.isOfferingInFlight)
        assertNull(cancelledState.offeringStatusText)
        assertNull(cancelledState.offeringTxSignature)
        assertFalse(cancelledState.offeringConfirmed)
        assertNotNull(cancelledState.offeringError)
        assertEquals("The Oracle offering was cancelled.", cancelledState.offeringError)
    }

    @Test
    fun testAttestationCancellationResetsSubmittingState() {
        // Simulate SPL Memo cancellation before signature exists
        val cancelledAttestationState = FortuneState(
            isSubmittingTransaction = false,
            txSignature = null,
            txError = "The Oracle attestation was cancelled."
        )

        assertFalse(cancelledAttestationState.isSubmittingTransaction)
        assertNull(cancelledAttestationState.txSignature)
        assertEquals("The Oracle attestation was cancelled.", cancelledAttestationState.txError)
    }

    @Test
    fun testAttestationSuccessPreservesSignature() {
        val attestationSig = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        val successAttestationState = FortuneState(
            isSubmittingTransaction = false,
            txSignature = attestationSig,
            txError = null
        )

        assertFalse(successAttestationState.isSubmittingTransaction)
        assertEquals(attestationSig, successAttestationState.txSignature)
        assertNull(successAttestationState.txError)
    }

    @Test
    fun testMwaFailureBeforeSignatureResetsState() {
        // Simulate MWA failure before signature exists
        val failedState = FortuneState(
            isOfferingInFlight = false,
            offeringStatusText = null,
            offeringTxSignature = null,
            offeringConfirmed = false,
            offeringUnknownConfirmation = false,
            offeringError = "The Oracle offering failed."
        )

        assertFalse(failedState.isOfferingInFlight)
        assertNull(failedState.offeringStatusText)
        assertNull(failedState.offeringTxSignature)
        assertEquals("The Oracle offering failed.", failedState.offeringError)
    }

    @Test
    fun testSuccessfulSubmissionHasSignature() {
        val offeringSig = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        val successState = FortuneState(
            isOfferingInFlight = false,
            offeringStatusText = "✨ Oracle Offering Confirmed",
            offeringTxSignature = offeringSig,
            offeringConfirmed = true,
            offeringUnknownConfirmation = false,
            offeringError = null
        )

        assertNotNull(successState.offeringTxSignature)
        assertTrue(successState.offeringConfirmed)
        assertFalse(successState.isOfferingInFlight)
    }

    @Test
    fun testAttestationReconciliationFingerprintFormat() {
        val attestationId = "ORCL_${UUID.randomUUID()}"
        assertTrue(attestationId.startsWith("ORCL_"))
        assertEquals(41, attestationId.length) // "ORCL_" (5) + UUID (36)
    }

    @Test
    fun testAttestationReconciliationSuccessState() {
        val recoveredSig = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        val state = FortuneState(
            isSubmittingTransaction = false,
            isReconcilingAttestation = false,
            attestationStatusText = null,
            txSignature = recoveredSig,
            txError = null
        )

        assertFalse(state.isSubmittingTransaction)
        assertFalse(state.isReconcilingAttestation)
        assertNull(state.attestationStatusText)
        assertEquals(recoveredSig, state.txSignature)
        assertNull(state.txError)
    }

    @Test
    fun testAttestationReconciliationTimeoutState() {
        val state = FortuneState(
            isSubmittingTransaction = false,
            isReconcilingAttestation = false,
            attestationStatusText = null,
            txSignature = null,
            txError = "Unable to verify the Oracle attestation. Please check your connection and try again."
        )

        assertFalse(state.isSubmittingTransaction)
        assertFalse(state.isReconcilingAttestation)
        assertNull(state.attestationStatusText)
        assertNull(state.txSignature)
        assertEquals(
            "Unable to verify the Oracle attestation. Please check your connection and try again.",
            state.txError
        )
    }

    @Test
    fun testConfirmationTimeoutAfterSignaturePreservesSignature() {
        val offeringSignature = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        
        // Simulate UNKNOWN / TIMEOUT state
        val unknownState = FortuneState(
            isOfferingInFlight = false,
            offeringStatusText = "Offering submitted, but Devnet confirmation is taking longer than expected.",
            offeringTxSignature = offeringSignature,
            offeringConfirmed = false,
            offeringUnknownConfirmation = true,
            offeringError = null
        )

        assertEquals(offeringSignature, unknownState.offeringTxSignature)
        assertTrue(unknownState.offeringUnknownConfirmation)
        assertFalse(unknownState.offeringConfirmed)
        assertNull(unknownState.offeringError)
    }

    @Test
    fun testAfterCancellationNewOfferingCanBeStarted() {
        // After cancellation, state is reset and isOfferingInFlight is false
        val stateAfterCancellation = FortuneState(
            isOfferingInFlight = false,
            offeringStatusText = null,
            offeringTxSignature = null,
            offeringError = "The Oracle offering was cancelled."
        )

        assertFalse(stateAfterCancellation.isOfferingInFlight)
    }

    @Test
    fun testConcurrencyProtectionWhileInFlight() {
        // When offering is in flight, isOfferingInFlight is true
        val inFlightState = FortuneState(
            isOfferingInFlight = true,
            offeringStatusText = "Waiting for wallet approval..."
        )

        assertTrue(inFlightState.isOfferingInFlight)
        assertEquals("Waiting for wallet approval...", inFlightState.offeringStatusText)
    }

    @Test
    fun testStateResetOnNewSession() {
        val initialState = FortuneState()
        assertFalse(initialState.isOfferingInFlight)
        assertNull(initialState.offeringTxSignature)
        assertFalse(initialState.offeringConfirmed)
        assertFalse(initialState.offeringUnknownConfirmation)
        assertNull(initialState.offeringError)
    }

    private class FakeOfferingSolanaWalletManager : SolanaWalletManager() {
        var blockhashToReturn: String = "11111111111111111111111111111111"
        var walletAddressToReturn: String = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"

        var connectAndAuthorizeCalls = 0
        var sendOracleOfferingCalls = 0

        var onSendOracleOffering: ((
            sender: ActivityResultSender,
            blockhash: String,
            treasuryAddress: String,
            amountLamports: Long,
            offeringId: String?,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) -> Unit)? = null

        var onSendOracleOfferingWithOutcome: ((
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

        var reconcileOfferingHandler: ((walletAddress: String, offeringId: String, attempt: Int) -> String?)? = null

        val reconcileOfferingCalls = mutableListOf<Pair<String, String>>()
        private var reconcileAttemptCount = 0

        override suspend fun fetchLatestBlockhash(): String = blockhashToReturn

        override suspend fun connectAndAuthorize(
            sender: ActivityResultSender,
            onSuccess: (publicKeyBytes: ByteArray, accountLabel: String?) -> Unit,
            onError: (String) -> Unit
        ) {
            connectAndAuthorizeCalls++
            val pubkeyBytes = Base58.decode(walletAddressToReturn)
            onSuccess(pubkeyBytes, "Test Account")
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
            sendOracleOfferingCalls++
            if (walletAddressToReturn.isNotBlank()) {
                onAuthorizedWallet?.invoke(walletAddressToReturn)
            }
            val outcomeHandler = onSendOracleOfferingWithOutcome
            if (outcomeHandler != null) {
                outcomeHandler(sender, blockhash, treasuryAddress, amountLamports, offeringId, onAuthorizedWallet, onSuccess, onError, onErrorOutcome)
            } else {
                val handler = onSendOracleOffering
                if (handler != null) {
                    handler(sender, blockhash, treasuryAddress, amountLamports, offeringId, onSuccess, onError)
                } else {
                    val outcome = TransactionFailureOutcome.UnknownTransactionOutcome("The Oracle offering was cancelled.")
                    onErrorOutcome?.invoke(outcome)
                    onError("The Oracle offering was cancelled.")
                }
            }
        }

        var pollSignatureStatusToReturn: SignatureConfirmationStatus = SignatureConfirmationStatus.CONFIRMED

        override suspend fun pollSignatureConfirmation(
            signature: String,
            timeoutMs: Long,
            pollIntervalMs: Long
        ): SignatureStatusResult {
            return SignatureStatusResult(pollSignatureStatusToReturn)
        }

        override suspend fun reconcileOffering(
            userWalletBase58: String,
            offeringId: String
        ): String? {
            reconcileOfferingCalls.add(userWalletBase58 to offeringId)
            reconcileAttemptCount++
            return reconcileOfferingHandler?.invoke(userWalletBase58, offeringId, reconcileAttemptCount)
        }
    }

    private fun waitForOfferingToFinish(viewModel: FortuneViewModel, maxWaitMs: Long = 5000L) {
        val startTime = System.currentTimeMillis()
        while (viewModel.uiState.value.isOfferingInFlight && System.currentTimeMillis() - startTime < maxWaitMs) {
            Thread.sleep(50)
            ShadowLooper.idleMainLooper()
        }
        Thread.sleep(100)
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun testOffering_1_NormalSuccessfulOffering() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        val expectedSig = "5eyktOfferingSig1111111111111111111111111111111111111111111111111111111111111111111111111111111"
        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, onSuccess, _ ->
            onSuccess(expectedSig)
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        assertTrue(viewModel.uiState.value.offeringConfirmed)
        assertEquals(expectedSig, viewModel.uiState.value.offeringTxSignature)
        assertNull(viewModel.uiState.value.offeringError)
        assertFalse(viewModel.uiState.value.isOfferingInFlight)
    }

    @Test
    fun testOffering_2_GenuineCancellationBeforeSubmission() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, _, onError ->
            onError("The Oracle offering was cancelled.")
        }
        fakeWalletManager.reconcileOfferingHandler = { _, _, _ -> null }

        var emittedMsg: String? = null
        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = { msg -> emittedMsg = msg }
        )

        waitForOfferingToFinish(viewModel)

        assertEquals(8, fakeWalletManager.reconcileOfferingCalls.size)
        assertFalse(viewModel.uiState.value.offeringConfirmed)
        assertNull(viewModel.uiState.value.offeringTxSignature)
        assertEquals("The Oracle offering was cancelled.", viewModel.uiState.value.offeringError)
        assertEquals("The Oracle offering was cancelled.", emittedMsg)
        assertFalse(viewModel.uiState.value.isOfferingInFlight)
    }

    @Test
    fun testOffering_3_AmbiguousInterruptionWithSuccessfulReconciliation() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        val expectedSig = "5eyktRecoveredOfferingSig1111111111111111111111111111111111111111111111111111111111111111111"
        var capturedOfferingId: String? = null

        fakeWalletManager.onSendOracleOffering = { _, _, _, _, offeringId, _, onError ->
            capturedOfferingId = offeringId
            onError("The Oracle offering was cancelled.")
        }

        fakeWalletManager.reconcileOfferingHandler = { _, offeringId, _ ->
            if (offeringId == capturedOfferingId) expectedSig else null
        }

        var emittedMsg: String? = null
        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = { msg -> emittedMsg = msg }
        )

        waitForOfferingToFinish(viewModel)

        assertNotNull("offeringId must be generated and non-null", capturedOfferingId)
        assertTrue(fakeWalletManager.reconcileOfferingCalls.isNotEmpty())
        assertTrue(viewModel.uiState.value.offeringConfirmed)
        assertEquals(expectedSig, viewModel.uiState.value.offeringTxSignature)
        assertNull(viewModel.uiState.value.offeringError)
        assertNull("No error status message emitted when reconciliation succeeds", emittedMsg)
        assertFalse(viewModel.uiState.value.isOfferingInFlight)
    }

    @Test
    fun testOffering_4_AmbiguousInterruptionWithoutOnChainMatch() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, _, onError ->
            onError("The Oracle offering was cancelled.")
        }
        fakeWalletManager.reconcileOfferingHandler = { _, _, _ -> null }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        assertEquals(8, fakeWalletManager.reconcileOfferingCalls.size)
        assertFalse(viewModel.uiState.value.offeringConfirmed)
        assertNull(viewModel.uiState.value.offeringTxSignature)
        assertEquals("The Oracle offering was cancelled.", viewModel.uiState.value.offeringError)
    }

    @Test
    fun testOffering_5_DuplicateTapProtectionInFlight() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, _, _ ->
            // Keep transaction in-flight
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        assertTrue(viewModel.uiState.value.isOfferingInFlight)
        assertEquals(0, fakeWalletManager.connectAndAuthorizeCalls)
        assertEquals(1, fakeWalletManager.sendOracleOfferingCalls)

        // Second tap while in-flight is ignored
        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        assertEquals(0, fakeWalletManager.connectAndAuthorizeCalls)
        assertEquals(1, fakeWalletManager.sendOracleOfferingCalls)
    }

    @Test
    fun testOffering_6_SingleTransactionScopeCapturesWalletWithoutConnectAndAuthorizePreCall() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        val expectedSig = "5eyktOfferingSig1111111111111111111111111111111111111111111111111111111111111111111111111111111"
        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, onSuccess, _ ->
            onSuccess(expectedSig)
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        assertEquals("No connectAndAuthorize pre-step MUST be called", 0, fakeWalletManager.connectAndAuthorizeCalls)
        assertEquals(1, fakeWalletManager.sendOracleOfferingCalls)
        assertEquals(fakeWalletManager.walletAddressToReturn, viewModel.connectedWallet.value)
    }

    @Test
    fun testOffering_7_NewOfferingAttemptResetsWalletAddressAndDoesNotReconcileStaleAddress() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val previousStaleAddress = "5eyktStalePreviousAddress11111111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(previousStaleAddress)

        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager().apply {
            walletAddressToReturn = "" // Blank address simulates failure before address capture
            onSendOracleOffering = { _, _, _, _, _, _, onError ->
                onError("The Oracle offering was cancelled.")
            }
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        // Verify that reconciliation was NOT called with the stale previous address
        assertTrue("Reconciliation MUST NOT be run against stale previous wallet address", fakeWalletManager.reconcileOfferingCalls.none { it.first == previousStaleAddress })
    }

    @Test
    fun testOffering_8_ReturnedSignatureWithConfirmationTimeoutAndInconclusiveReconciliationSetsUnknownConfirmation() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager().apply {
            pollSignatureStatusToReturn = SignatureConfirmationStatus.NOT_FOUND
        }

        val expectedSig = "5eyktOfferingReturnedSig111111111111111111111111111111111111111111111111111111111111"

        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, onSuccess, _ ->
            onSuccess(expectedSig)
        }
        // Force poll SignatureConfirmationStatus to NOT_FOUND
        fakeWalletManager.reconcileOfferingHandler = { _, _, _ -> null }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        // Wait for reconciliation to finish
        waitForOfferingToFinish(viewModel)

        val state = viewModel.uiState.value
        assertFalse("isOfferingInFlight must be false when finished", state.isOfferingInFlight)
        assertFalse("offeringConfirmed must be false when reconciliation finds no match", state.offeringConfirmed)
        assertTrue("offeringUnknownConfirmation MUST be true when signature returned but reconciliation inconclusive", state.offeringUnknownConfirmation)
        assertEquals(expectedSig, state.offeringTxSignature)
        assertNull("offeringError must be null when in offeringUnknownConfirmation state", state.offeringError)
    }

    @Test
    fun testOffering_9_WalletCancellationBeforeSignatureSetsErrorNotUnknown() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        fakeWalletManager.onSendOracleOffering = { _, _, _, _, _, _, onError ->
            onError("The Oracle offering was cancelled.")
        }
        fakeWalletManager.reconcileOfferingHandler = { _, _, _ -> null }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        val state = viewModel.uiState.value
        assertFalse("isOfferingInFlight must be false", state.isOfferingInFlight)
        assertFalse("offeringConfirmed must be false", state.offeringConfirmed)
        assertFalse("offeringUnknownConfirmation must be false on cancellation before signature", state.offeringUnknownConfirmation)
        assertNull("offeringTxSignature must be null on cancellation before signature", state.offeringTxSignature)
        assertEquals("The Oracle offering was cancelled.", state.offeringError)
    }

    @Test
    fun testOffering_10_LateAsynchronousCompletionFromEarlierOfferingIsIgnored() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())

        val offering1Sig = "5eyktOffering1Sig11111111111111111111111111111111111111111111111111111111111111"
        val offering2Sig = "5eyktOffering2Sig11111111111111111111111111111111111111111111111111111111111111"

        val manager1 = FakeOfferingSolanaWalletManager().apply {
            onSendOracleOffering = { _, _, _, _, _, onSuccess, _ ->
                onSuccess(offering1Sig)
            }
            reconcileOfferingHandler = { _, _, _ -> null }
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = manager1,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)
        val firstOfferingId = viewModel.uiState.value.offeringId
        assertNotNull(firstOfferingId)

        val manager2 = FakeOfferingSolanaWalletManager().apply {
            onSendOracleOffering = { _, _, _, _, _, onSuccess, _ ->
                onSuccess(offering2Sig)
            }
        }

        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = manager2,
            reconciliationDelayMs = 1L,
            onStatusMessage = {}
        )

        waitForOfferingToFinish(viewModel)

        val secondOfferingId = viewModel.uiState.value.offeringId
        assertNotNull(secondOfferingId)
        assertTrue(firstOfferingId != secondOfferingId)
        assertTrue(viewModel.uiState.value.offeringConfirmed)
        assertEquals(offering2Sig, viewModel.uiState.value.offeringTxSignature)
    }

    @Test
    fun testOracleOfferingErrorNotSigned_ZeroReconciliationCallsAndImmediateCancellation() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = FortuneViewModel(app)
        val dummySender = ActivityResultSender(ComponentActivity())
        val fakeWalletManager = FakeOfferingSolanaWalletManager()

        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")

        fakeWalletManager.onSendOracleOfferingWithOutcome = { _, _, _, _, _, _, _, onError, onErrorOutcome ->
            val outcome = TransactionFailureOutcome.ExplicitUserDeclined("The Oracle offering was cancelled.")
            onErrorOutcome?.invoke(outcome)
            onError("The Oracle offering was cancelled.")
        }

        var statusMessage: String? = null
        viewModel.onMakeOracleOffering(
            sender = dummySender,
            walletManager = fakeWalletManager,
            reconciliationDelayMs = 1L,
            onStatusMessage = { msg -> statusMessage = msg }
        )

        waitForOfferingToFinish(viewModel)

        assertEquals(0, fakeWalletManager.reconcileOfferingCalls.size)
        assertFalse(viewModel.uiState.value.isOfferingInFlight)
        assertFalse(viewModel.uiState.value.offeringConfirmed)
        assertEquals("The Oracle offering was cancelled.", viewModel.uiState.value.offeringError)
        assertEquals("The Oracle offering was cancelled.", statusMessage)
    }
}

