package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.model.AccessTier
import com.example.model.AppScreen
import com.example.ui.FortuneViewModel
import com.example.util.SolanaWalletManager
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FreeSealCtaRegressionTest {

    private lateinit var application: Application
    private lateinit var preferences: FortunePreferences

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        preferences = FortunePreferences(application)
        preferences.lastReadingDate = null
        preferences.dailyReadingCount = 0
        preferences.isMockSeekerEliteEnabled = false
    }

    @Test
    fun test1_FreeCompletedReadingCanInvokeSealCurrentReadingOnChain() {
        val viewModel = FortuneViewModel(application)
        // User is on FREE reading #1
        assertEquals(2, preferences.getDailyReadingsRemaining())
        assertEquals(AccessTier.FREE, viewModel.uiState.value.accessTier)

        // User holds Q1 and completes local reveal
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(4)

        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)
        assertFalse(viewModel.uiState.value.isWeb3Mode)
        assertNull(viewModel.uiState.value.txSignature)

        val dummySender = ActivityResultSender(ComponentActivity())
        val dummyWalletManager = SolanaWalletManager()

        // User taps "Seal on Solana" CTA on ResultScreen
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = dummyWalletManager,
            onError = {}
        )

        // Verify transition to Web3 mode and active submission
        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals("Q1", viewModel.uiState.value.selectedQuestionId)
        assertEquals(4, viewModel.uiState.value.finalChoice)
    }

    @Test
    fun test2_SeekerEliteCompletedReadingCanInvokeSealCurrentReadingOnChain() {
        preferences.isMockSeekerEliteEnabled = true
        val viewModel = FortuneViewModel(application)
        assertEquals(AccessTier.SEEKER_ELITE, viewModel.uiState.value.accessTier)

        viewModel.onQuestionHoldConfirmed("Q9")
        viewModel.selectFinalChoice(7)

        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)
        assertEquals("Q9", viewModel.uiState.value.selectedQuestionId)
        assertEquals(7, viewModel.uiState.value.finalChoice)

        val dummySender = ActivityResultSender(ComponentActivity())
        val dummyWalletManager = SolanaWalletManager()

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = dummyWalletManager,
            onError = {}
        )

        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals("Q9", viewModel.uiState.value.selectedQuestionId)
        assertEquals(7, viewModel.uiState.value.finalChoice)
    }

    @Test
    fun test3_ExistingPostQuotaWeb3BehaviorRemainsUnchanged() {
        val viewModel = FortuneViewModel(application)
        // Consume 2 free readings
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        // Without prior wallet connection, hold triggers connection prompt
        viewModel.onQuestionHoldConfirmed("Q5")
        assertTrue(viewModel.uiState.value.isQuotaExhaustedDialogVisible)

        // Proceeding from quota dialog transitions to AnimationPlaceholder in Web3 mode
        viewModel.proceedToWeb3ModeFromQuotaDialog()

        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun test4_CancellationAndReconciliationBehaviorRemainsUnchanged() {
        val viewModel = FortuneViewModel(application)
        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")

        viewModel.onQuestionHoldConfirmed("Q2")
        viewModel.selectFinalChoice(2)

        val dummySender = ActivityResultSender(ComponentActivity())
        val dummyWalletManager = SolanaWalletManager()

        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = dummyWalletManager,
            onError = {}
        )

        // Submitting flag is active
        assertTrue(viewModel.uiState.value.isSubmittingTransaction)

        // Back during submission/reconciliation is absorbed and preserves state
        val backHandled = viewModel.handleBack()
        assertTrue(backHandled)
        assertEquals("Q2", viewModel.uiState.value.selectedQuestionId)
        assertEquals(2, viewModel.uiState.value.finalChoice)
    }

    @Test
    fun test5_ResultRevealStatePreservationOnHistoryNavigation() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q3")
        viewModel.selectFinalChoice(5)

        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)
        assertFalse(viewModel.uiState.value.isResultRevealed)

        // Result is revealed once countdown completes
        viewModel.markResultRevealed()
        assertTrue(viewModel.uiState.value.isResultRevealed)

        // User navigates to History
        viewModel.navigateToHistory()
        assertEquals(AppScreen.History, viewModel.uiState.value.currentScreen)
        assertTrue(viewModel.uiState.value.isResultRevealed)

        // User presses Back (returns to Result)
        viewModel.handleBack()
        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)
        assertTrue(viewModel.uiState.value.isResultRevealed) // Countdown won't restart

        // User taps Try Again
        viewModel.onTryAgain()
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)
        assertFalse(viewModel.uiState.value.isResultRevealed)
    }
}
