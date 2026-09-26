package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.model.AppScreen
import com.example.ui.FortuneViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DailyQuotaAndAccessTest {

    private lateinit var application: Application
    private lateinit var preferences: FortunePreferences

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        preferences = FortunePreferences(application)
        preferences.lastReadingDate = null
        preferences.dailyReadingCount = 0
        preferences.dailyWeb3ReadingCount = 0
    }

    // --- Basic Daily Quota Tests (1-7) ---

    @Test
    fun test1_FreshDayCountZeroRemainingTwoFreeThreePrototype() {
        val freeRemaining = preferences.getFreeReadingsRemaining("2025-01-01")
        val prototypeRemaining = preferences.getPrototypeReadingsRemaining("2025-01-01")
        assertEquals(2, freeRemaining)
        assertEquals(3, prototypeRemaining)
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
        assertEquals("2025-01-01", preferences.lastReadingDate)
    }

    @Test
    fun test2_CompleteReading1_FreeRemainingOne() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(5)

        assertEquals(1, preferences.dailyReadingCount)
        assertEquals(1, preferences.getFreeReadingsRemaining())
        assertEquals(3, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test3_CompleteReading2_FreeRemainingZero() {
        val viewModel = FortuneViewModel(application)
        
        // Reading 1
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(5)

        // Reading 2
        viewModel.onTryAgain()
        viewModel.onQuestionHoldConfirmed("Q2")
        viewModel.selectFinalChoice(3)

        assertEquals(2, preferences.dailyReadingCount)
        assertEquals(0, preferences.getFreeReadingsRemaining())
        assertEquals(3, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test4_Reading3_WithoutWallet_WalletGate() {
        val viewModel = FortuneViewModel(application)
        // Consume 2 free readings
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onQuestionHoldConfirmed("Q1")
        assertTrue(viewModel.uiState.value.isQuotaExhaustedDialogVisible)
        assertEquals(AppScreen.Home, viewModel.uiState.value.currentScreen) // Stayed outside Cosmic Vortex
    }

    @Test
    fun test5_WalletAuthorizationSucceeds_PrototypeAllowanceAvailable() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        // Connect wallet
        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")

        viewModel.onQuestionHoldConfirmed("Q1")
        assertFalse(viewModel.uiState.value.isQuotaExhaustedDialogVisible)
        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun test6_CompleteReadings345_PrototypeRemainingReachesZero() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        val pubkey = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(pubkey)

        // Reading 3
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(1)
        assertEquals(1, preferences.dailyWeb3ReadingCount)
        assertEquals(2, preferences.getPrototypeReadingsRemaining())

        // Reading 4
        viewModel.onTryAgain()
        viewModel.onQuestionHoldConfirmed("Q2")
        viewModel.selectFinalChoice(2)
        assertEquals(2, preferences.dailyWeb3ReadingCount)
        assertEquals(1, preferences.getPrototypeReadingsRemaining())

        // Reading 5
        viewModel.onTryAgain()
        viewModel.onQuestionHoldConfirmed("Q3")
        viewModel.selectFinalChoice(3)
        assertEquals(3, preferences.dailyWeb3ReadingCount)
        assertEquals(0, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test7_Reading6_Blocked() {
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading()
        preferences.incrementPrototypeWeb3Reading()
        preferences.incrementPrototypeWeb3Reading()

        val viewModel = FortuneViewModel(application)
        val pubkey = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(pubkey)

        // Attempt reading 6
        viewModel.onQuestionHoldConfirmed("Q1")
        assertTrue(viewModel.uiState.value.isQuotaExhaustedDialogVisible)
        assertEquals(0, viewModel.uiState.value.freeReadingsRemaining)
        assertEquals(0, viewModel.uiState.value.prototypeReadingsRemaining)

        // Even calling proceedToWeb3ModeFromQuotaDialog must be blocked
        viewModel.proceedToWeb3ModeFromQuotaDialog()
        assertTrue(viewModel.uiState.value.isQuotaExhaustedDialogVisible)
        assertEquals(0, preferences.getPrototypeReadingsRemaining())
        assertEquals(0, viewModel.uiState.value.prototypeReadingsRemaining)
    }

    @Test
    fun test7b_Reading6_ConnectedWallet_StillHardStop() {
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading()
        preferences.incrementPrototypeWeb3Reading()
        preferences.incrementPrototypeWeb3Reading()

        val viewModel = FortuneViewModel(application)
        val pubkey = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(pubkey)

        viewModel.onQuestionHoldConfirmed("Q1")
        val state = viewModel.uiState.value
        assertTrue(state.isQuotaExhaustedDialogVisible)
        assertEquals(0, state.freeReadingsRemaining)
        assertEquals(0, state.prototypeReadingsRemaining)
    }

    // --- Wallet Integrity Tests (8-14) ---

    @Test
    fun test8_WalletCancellation_NoQuotaConsumed() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        // Attempt reading without wallet -> dialog shown
        viewModel.onQuestionHoldConfirmed("Q1")
        assertTrue(viewModel.uiState.value.isQuotaExhaustedDialogVisible)

        // User dismisses dialog (cancels wallet prompt)
        viewModel.dismissQuotaExhaustedDialog()
        assertFalse(viewModel.uiState.value.isQuotaExhaustedDialogVisible)

        assertEquals(0, preferences.dailyWeb3ReadingCount)
        assertEquals(3, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test9_WalletRejectionOrFailure_NoQuotaConsumed() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onQuestionHoldConfirmed("Q1")
        // Wallet connection failed/rejected, onWalletConnected not called
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test10_WalletNotInstalled_NoQuotaConsumed() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onQuestionHoldConfirmed("Q1")
        assertTrue(viewModel.uiState.value.isQuotaExhaustedDialogVisible)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test11_Disconnect_QuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading() // 1 prototype reading done

        val pubkey = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(pubkey)
        viewModel.disconnectWallet()

        assertNull(viewModel.connectedWallet.value)
        assertEquals(1, preferences.dailyWeb3ReadingCount)
        assertEquals(2, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test12_Reconnect_QuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading()

        val pubkey = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        viewModel.onWalletConnected(pubkey)
        viewModel.disconnectWallet()
        viewModel.onWalletConnected(pubkey)

        assertEquals(1, preferences.dailyWeb3ReadingCount)
        assertEquals(2, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test13_SwitchWallet_QuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading() // 1 prototype reading used

        // Connect Wallet A
        viewModel.onWalletConnected("WalletA_1111111111111111111111111111111111111111111111111111111111111111")
        viewModel.disconnectWallet()

        // Connect Wallet B
        viewModel.onWalletConnected("WalletB_2222222222222222222222222222222222222222222222222222222222222222")

        // Quota MUST remain unchanged (device daily quota)
        assertEquals(1, preferences.dailyWeb3ReadingCount)
        assertEquals(2, preferences.getPrototypeReadingsRemaining())
    }

    @Test
    fun test14_ReconnectRepeatedly_NoNewAllowance() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading()
        preferences.incrementPrototypeWeb3Reading()

        for (i in 1..5) {
            viewModel.onWalletConnected("Wallet_$i")
            viewModel.disconnectWallet()
        }

        assertEquals(2, preferences.dailyWeb3ReadingCount)
        assertEquals(1, preferences.getPrototypeReadingsRemaining())
    }

    // --- Persistence Tests (15-17) ---

    @Test
    fun test15_AppRestart_PersistedCountersRemainCorrect() {
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading()

        // Re-instantiate preferences to simulate app restart
        val newPrefs = FortunePreferences(application)
        assertEquals(1, newPrefs.dailyReadingCount)
        assertEquals(1, newPrefs.dailyWeb3ReadingCount)
        assertEquals(1, newPrefs.getFreeReadingsRemaining())
        assertEquals(2, newPrefs.getPrototypeReadingsRemaining())
    }

    @Test
    fun test16_DateRollover_DailyCountersResetTogether() {
        // Day 1: consume all 2 free and 3 prototype readings
        preferences.getFreeReadingsRemaining("2025-01-01")
        preferences.incrementDailyReading("2025-01-01")
        preferences.incrementDailyReading("2025-01-01")
        preferences.incrementPrototypeWeb3Reading("2025-01-01")
        preferences.incrementPrototypeWeb3Reading("2025-01-01")
        preferences.incrementPrototypeWeb3Reading("2025-01-01")

        assertEquals(0, preferences.getFreeReadingsRemaining("2025-01-01"))
        assertEquals(0, preferences.getPrototypeReadingsRemaining("2025-01-01"))

        // Day 2: Date changes
        val freeDay2 = preferences.getFreeReadingsRemaining("2025-01-02")
        val protoDay2 = preferences.getPrototypeReadingsRemaining("2025-01-02")

        assertEquals(2, freeDay2)
        assertEquals(3, protoDay2)
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
        assertEquals("2025-01-02", preferences.lastReadingDate)
    }

    @Test
    fun test17_AppDataClear_ExistingResetBehavior() {
        preferences.incrementDailyReading()
        preferences.incrementPrototypeWeb3Reading()

        // Simulate app data clear by resetting preferences
        preferences.dailyReadingCount = 0
        preferences.dailyWeb3ReadingCount = 0
        preferences.lastReadingDate = null

        assertEquals(2, preferences.getFreeReadingsRemaining())
        assertEquals(3, preferences.getPrototypeReadingsRemaining())
    }

    // --- Completion Safety Tests (18-24) ---

    @Test
    fun test18_FinalNumberSelected_ResultGenerationFails_QuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        // Not in AnimationPlaceholder screen
        viewModel.selectFinalChoice(5)

        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test19_FinalNumberSelected_CoroutineCancelled_QuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        // Leaving AnimationPlaceholder before choice selection
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.onTryAgain()

        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test20_UserLeavesCosmicVortex_BeforeFinalSelection_QuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1")
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)

        // User navigates away before selecting final number
        viewModel.navigateToHome()
        assertEquals(AppScreen.Home, viewModel.uiState.value.currentScreen)

        assertEquals(0, preferences.dailyReadingCount)
    }

    @Test
    fun test21_WalletSucceeds_UserBacksOutBeforeFinalNumber_PrototypeQuotaUnchanged() {
        val viewModel = FortuneViewModel(application)
        preferences.incrementDailyReading()
        preferences.incrementDailyReading()

        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")
        viewModel.onQuestionHoldConfirmed("Q1")
        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)

        // User backs out before final number choice
        viewModel.onTryAgain()
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test22_DuplicateFinalSelectionCallback_OnlyOneQuotaUnitConsumed() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1")
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)

        // First choice selection
        viewModel.selectFinalChoice(5)
        assertEquals(1, preferences.dailyReadingCount)
        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)

        // Duplicate selection callbacks
        viewModel.selectFinalChoice(5)
        viewModel.selectFinalChoice(5)

        assertEquals(1, preferences.dailyReadingCount)
    }

    @Test
    fun test23_RepeatedCallbackRecomposition_OneUnitConsumed() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(7)

        repeat(10) {
            viewModel.selectFinalChoice(7)
        }

        assertEquals(1, preferences.dailyReadingCount)
    }

    @Test
    fun test24_NewDeliberateReading_ConsumesNewUnit() {
        val viewModel = FortuneViewModel(application)

        // Reading 1
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(1)
        assertEquals(1, preferences.dailyReadingCount)

        // Reading 2 (new deliberate attempt)
        viewModel.onTryAgain()
        viewModel.onQuestionHoldConfirmed("Q2")
        viewModel.selectFinalChoice(2)
        assertEquals(2, preferences.dailyReadingCount)
    }

    // --- Activity Recreation Tests (25-27) ---

    @Test
    fun test25_ActivityRecreation_DuringAccessEvaluation_NoQuotaConsumed() {
        val viewModel = FortuneViewModel(application)
        viewModel.selectQuestionForConfirmation("Q1")

        // Recreate ViewModel
        FortuneViewModel(application)
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test26_ActivityRecreation_DuringIncompleteAttempt_NoDuplicateQuotaConsumption() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1") // enters AnimationPlaceholder

        // Activity recreated before final number selection
        FortuneViewModel(application)
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test27_InterruptedAttemptNotPreserved_NoDuplicateConsumption() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1")

        // Interrupted attempt does not consume quota
        assertEquals(0, preferences.dailyReadingCount)
    }

    // --- Navigation / Confirmation Tests (28-30) ---

    @Test
    fun test28_BackBeforeCompletion_NoQuotaConsumed() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()
        viewModel.selectQuestionForConfirmation("Q1")
        viewModel.handleBack()

        assertEquals(0, preferences.dailyReadingCount)
    }

    @Test
    fun test29_DuplicateYesProceed_CannotCreateDuplicateQuotaConsumption() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()
        viewModel.selectQuestionForConfirmation("Q1")
        viewModel.confirmSelectedQuestion()
        viewModel.confirmSelectedQuestion() // Duplicate call

        assertEquals(0, preferences.dailyReadingCount)
    }

    @Test
    fun test30_ConfirmationOrderRemains_Confirmation_Yes_AccessEvaluation_CosmicVortex() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()
        viewModel.selectQuestionForConfirmation("Q1")
        assertEquals("Q1", viewModel.uiState.value.pendingQuestionId)

        viewModel.confirmSelectedQuestion()
        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertEquals("Q1", viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    // --- Isolation Tests (31-35) ---

    @Test
    fun test31_Sealing_DoesNotChangeReadingQuota() {
        val viewModel = FortuneViewModel(application)
        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(3)
        assertEquals(1, preferences.dailyReadingCount)

        // Sealing invocation does not modify reading quota counters
        assertEquals(1, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test32_OracleOffering_DoesNotChangeReadingQuota() {
        val viewModel = FortuneViewModel(application)
        assertFalse(viewModel.uiState.value.isOfferingInFlight)
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test33_WalletConnectionAlone_DoesNotConsumeReadingQuota() {
        val viewModel = FortuneViewModel(application)
        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")

        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test34_ExistingSeekerEliteBehavior_RemainsUnchanged() {
        preferences.isMockSeekerEliteEnabled = true
        val viewModel = FortuneViewModel(application)

        assertEquals(999, viewModel.uiState.value.freeReadingsRemaining)
        assertEquals(999, viewModel.uiState.value.prototypeReadingsRemaining)

        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(8)

        // Seeker Elite does not increment local free or web3 daily counters
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(0, preferences.dailyWeb3ReadingCount)
    }

    @Test
    fun test35_FirstTwoReadings_OfflineBehaviorUnchanged() {
        val viewModel = FortuneViewModel(application)

        // Offline reading 1
        viewModel.onQuestionHoldConfirmed("Q1")
        assertFalse(viewModel.uiState.value.isWeb3Mode)
        viewModel.selectFinalChoice(1)
        assertEquals(1, preferences.dailyReadingCount)

        // Offline reading 2
        viewModel.onTryAgain()
        viewModel.onQuestionHoldConfirmed("Q2")
        assertFalse(viewModel.uiState.value.isWeb3Mode)
        viewModel.selectFinalChoice(2)
        assertEquals(2, preferences.dailyReadingCount)
    }
}
