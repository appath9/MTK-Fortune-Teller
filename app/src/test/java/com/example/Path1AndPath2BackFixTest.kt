package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.model.SealingState
import com.example.ui.FortuneViewModel
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Path1AndPath2BackFixTest {

    private lateinit var application: Application
    private lateinit var preferences: FortunePreferences

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        preferences = FortunePreferences(application)
        preferences.lastReadingDate = null
        preferences.dailyReadingCount = 0
    }

    @Test
    fun testClassifier_CancellationExceptionWithNullSignature_returnsExplicitUserDeclined() {
        val outcome = classifyTransactionFailure(
            e = CancellationException("User cancelled in wallet"),
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("CancellationException pre-signature MUST return ExplicitUserDeclined", outcome is TransactionFailureOutcome.ExplicitUserDeclined)
        assertEquals("The Oracle attestation was cancelled.", outcome.message)
    }

    @Test
    fun testClassifier_CancellationExceptionInCauseChainWithNullSignature_returnsExplicitUserDeclined() {
        val wrappedException = RuntimeException("Outer exception", CancellationException("Inner cancellation"))
        val outcome = classifyTransactionFailure(
            e = wrappedException,
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("CancellationException in cause chain pre-signature MUST return ExplicitUserDeclined", outcome is TransactionFailureOutcome.ExplicitUserDeclined)
        assertEquals("The Oracle attestation was cancelled.", outcome.message)
    }

    @Test
    fun testClassifier_TimeoutCancellationExceptionWithNullSignature_returnsUnknownTransactionOutcome() {
        val timeoutException = try {
            runBlocking {
                withTimeout(1L) { delay(100L) }
                null
            }
        } catch (e: TimeoutCancellationException) {
            e
        }!!
        val outcome = classifyTransactionFailure(
            e = timeoutException,
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("TimeoutCancellationException MUST return UnknownTransactionOutcome for safe reconciliation", outcome is TransactionFailureOutcome.UnknownTransactionOutcome)
    }

    @Test
    fun testClassifier_TimeoutCancellationExceptionInCauseChainWithNullSignature_returnsUnknownTransactionOutcome() {
        val timeoutException = try {
            runBlocking {
                withTimeout(1L) { delay(100L) }
                null
            }
        } catch (e: TimeoutCancellationException) {
            e
        }!!
        val wrappedTimeout = RuntimeException("Outer exception", timeoutException)
        val outcome = classifyTransactionFailure(
            e = wrappedTimeout,
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("TimeoutCancellationException in cause chain MUST return UnknownTransactionOutcome for safe reconciliation", outcome is TransactionFailureOutcome.UnknownTransactionOutcome)
    }

    @Test
    fun testClassifier_CancellationExceptionWithCapturedSignature_returnsUnknownTransactionOutcome() {
        val outcome = classifyTransactionFailure(
            e = CancellationException("Job cancelled"),
            capturedSignature = "5eyktCapturedSig1111111111111111111111111111111111111111111111111111111111",
            isFromMwaFailureResult = false
        )
        assertTrue("Captured signature MUST force UnknownTransactionOutcome even on CancellationException", outcome is TransactionFailureOutcome.UnknownTransactionOutcome)
    }

    @Test
    fun testClassifier_TimeoutCancellationExceptionWithCapturedSignature_returnsUnknownTransactionOutcome() {
        val timeoutException = try {
            runBlocking {
                withTimeout(1L) { delay(100L) }
                null
            }
        } catch (e: TimeoutCancellationException) {
            e
        }!!
        val outcome = classifyTransactionFailure(
            e = timeoutException,
            capturedSignature = "5eyktCapturedSig1111111111111111111111111111111111111111111111111111111111",
            isFromMwaFailureResult = false
        )
        assertTrue("Captured signature MUST force UnknownTransactionOutcome even on TimeoutCancellationException", outcome is TransactionFailureOutcome.UnknownTransactionOutcome)
    }

    @Test
    fun testClassifier_ErrorNotSignedWithNullSignature_returnsExplicitUserDeclined() {
        val remoteEx = JsonRpc20Client.JsonRpc20RemoteException(
            ProtocolContract.ERROR_NOT_SIGNED,
            "User declined to sign",
            null
        )
        val outcome = classifyTransactionFailure(
            e = RuntimeException("RPC error", remoteEx),
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("ERROR_NOT_SIGNED MUST return ExplicitUserDeclined", outcome is TransactionFailureOutcome.ExplicitUserDeclined)
        assertEquals("The Oracle attestation was cancelled.", outcome.message)
    }

    @Test
    fun testPath1Classifier_InterruptedExceptionWithNullSignature_returnsUnknownTransactionOutcome() {
        val outcome = classifyTransactionFailure(
            e = InterruptedException("MWA request interrupted"),
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("InterruptedException pre-signature MUST be UnknownTransactionOutcome for safe reconciliation", outcome is TransactionFailureOutcome.UnknownTransactionOutcome)
        assertEquals("MWA request interrupted", outcome.message)
    }

    @Test
    fun testClassifier_NonCancellationUnknownFailure_returnsUnknownTransactionOutcome() {
        val outcome = classifyTransactionFailure(
            e = IllegalStateException("Unexpected state error"),
            capturedSignature = null,
            isFromMwaFailureResult = true
        )
        assertTrue("Non-cancellation unknown failure MUST return UnknownTransactionOutcome", outcome is TransactionFailureOutcome.UnknownTransactionOutcome)
        assertEquals("Unexpected state error", outcome.message)
    }

    @Test
    fun testPath2ViewModel_WalletConnectingExplicitBack_cancelsJobAndClearsUiStateWithoutModifyingQuota() = runBlocking {
        val viewModel = FortuneViewModel(application)
        val dummySender = ActivityResultSender(ComponentActivity())

        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(0)

        val freeBefore = preferences.getFreeReadingsRemaining()

        val slowWalletManager = object : SolanaWalletManager() {
            override suspend fun fetchLatestBlockhash(): String {
                delay(10_000L)
                return "11111111111111111111111111111111"
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = slowWalletManager,
            onError = {}
        )

        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals(SealingState.WALLET_CONNECTING, viewModel.uiState.value.sealingState)
        assertNull(viewModel.uiState.value.txSignature)

        val handled = viewModel.handleBack()
        assertTrue("handleBack MUST return true during WALLET_CONNECTING", handled)

        assertFalse("isSubmittingTransaction MUST be false after Back", viewModel.uiState.value.isSubmittingTransaction)
        assertFalse("isReconcilingAttestation MUST be false", viewModel.uiState.value.isReconcilingAttestation)
        assertEquals(SealingState.IDLE, viewModel.uiState.value.sealingState)
        assertNull("attestationId MUST be cleared", viewModel.uiState.value.attestationId)

        assertEquals("Free readings quota MUST NOT be modified by cancellation", freeBefore, preferences.getFreeReadingsRemaining())
    }

    @Test
    fun testProtectedStates_SigningDevnetMemoAndReconciling_areProtectedFromBackCancellation() = runBlocking {
        val viewModel = FortuneViewModel(application)

        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(0)

        val dummySender = ActivityResultSender(ComponentActivity())
        val mockWalletManager = object : SolanaWalletManager() {
            override suspend fun fetchLatestBlockhash(): String = "11111111111111111111111111111111"
            override suspend fun recordOracleAttestation(
                sender: ActivityResultSender,
                questionId: String,
                chosenNumber: Int,
                blockhash: String,
                attestationId: String?,
                onAuthorizedWallet: ((String) -> Unit)?,
                onSuccess: (String) -> Unit,
                onError: (String) -> Unit,
                onErrorOutcome: ((TransactionFailureOutcome) -> Unit)?
            ) {
                onAuthorizedWallet?.invoke("5eyktMockAddress111111111111111111111111111111111111111111111111111111111")
                delay(10_000L)
            }
        }

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = mockWalletManager,
            onError = {}
        )

        var attempts = 0
        while (viewModel.uiState.value.sealingState == SealingState.WALLET_CONNECTING && attempts < 50) {
            delay(20L)
            attempts++
        }

        assertEquals(SealingState.SIGNING_DEVNET_MEMO, viewModel.uiState.value.sealingState)

        val handled = viewModel.handleBack()
        assertTrue("handleBack MUST consume Back event", handled)

        assertTrue("isSubmittingTransaction MUST remain true for SIGNING_DEVNET_MEMO", viewModel.uiState.value.isSubmittingTransaction)
        assertEquals("sealingState MUST remain SIGNING_DEVNET_MEMO", SealingState.SIGNING_DEVNET_MEMO, viewModel.uiState.value.sealingState)
    }
}
