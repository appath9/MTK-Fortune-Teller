package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortuneDataProvider
import com.example.data.FortunePreferences
import com.example.model.AppScreen
import com.example.ui.FortuneViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuestionConfirmationTest {

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
    fun testA_NormalQuestionConfirmationFlow() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)
        assertNull(viewModel.uiState.value.pendingQuestionId)

        // 1. User selects a question
        viewModel.selectQuestionForConfirmation("Q1")
        assertEquals("Q1", viewModel.uiState.value.pendingQuestionId)
        // Screen should still be SelectQuestion while confirmation UI is active
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)

        // 2. User taps YES, PROCEED
        viewModel.confirmSelectedQuestion()

        // 3. Confirmation dialog dismissed, pendingQuestionId cleared, moved to AnimationPlaceholder
        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertEquals("Q1", viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testB_ChangeQuestionFlow() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        // 1. User selects Q1
        viewModel.selectQuestionForConfirmation("Q1")
        assertEquals("Q1", viewModel.uiState.value.pendingQuestionId)

        // 2. User taps NO, CHANGE QUESTION
        viewModel.dismissQuestionConfirmation()

        // 3. Confirmation dismissed, user remains on SelectQuestion screen, reading NOT started
        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertNull(viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)

        // 4. User selects Q5
        viewModel.selectQuestionForConfirmation("Q5")
        assertEquals("Q5", viewModel.uiState.value.pendingQuestionId)

        // 5. User confirms Q5
        viewModel.confirmSelectedQuestion()

        // 6. Confirmed question Q5 is used for reading flow
        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertEquals("Q5", viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testC_AndroidBackDismissesConfirmationWithoutStartingReading() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        // User selects Q3
        viewModel.selectQuestionForConfirmation("Q3")
        assertEquals("Q3", viewModel.uiState.value.pendingQuestionId)

        // User presses Android Back
        val backHandled = viewModel.handleBack()
        assertTrue("handleBack should consume Back press to dismiss confirmation", backHandled)

        // Confirmation dismissed, user remains on SelectQuestion screen
        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertNull(viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)

        // Pressing Back again when confirmation is closed returns to Home screen
        val secondBackHandled = viewModel.handleBack()
        assertTrue(secondBackHandled)
        assertEquals(AppScreen.Home, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testD_LongQuestionTextIsRetrievedCorrectlyForConfirmationUI() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        // Select a long question (e.g. Q14)
        val longQuestionId = "Q14"
        viewModel.selectQuestionForConfirmation(longQuestionId)

        val question = FortuneDataProvider.getQuestionById(longQuestionId)
        assertNotNull(question)
        assertTrue("English question text must be non-empty", question!!.en.length > 20)
        assertTrue("Myanmar question text must be non-empty", question.mm.isNotEmpty())

        assertEquals(longQuestionId, viewModel.uiState.value.pendingQuestionId)
    }

    @Test
    fun testE_RapidTappingProtectionPreventsDuplicateCommitments() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        // Rapid multiple taps on question items
        viewModel.selectQuestionForConfirmation("Q2")
        viewModel.selectQuestionForConfirmation("Q2")
        viewModel.selectQuestionForConfirmation("Q2")

        assertEquals("Q2", viewModel.uiState.value.pendingQuestionId)

        // Rapid multiple taps on YES, PROCEED button
        viewModel.confirmSelectedQuestion()
        val screenAfterFirstConfirm = viewModel.uiState.value.currentScreen
        assertEquals(AppScreen.AnimationPlaceholder, screenAfterFirstConfirm)
        assertEquals("Q2", viewModel.uiState.value.selectedQuestionId)
        assertNull(viewModel.uiState.value.pendingQuestionId)

        // Subsequent confirm calls do nothing because pendingQuestionId is null and screen is not SelectQuestion
        viewModel.confirmSelectedQuestion()
        viewModel.confirmSelectedQuestion()

        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
        assertEquals("Q2", viewModel.uiState.value.selectedQuestionId)
    }

    @Test
    fun testF_QuotaExhaustionOccursOnlyAfterUserConfirmsQuestion() {
        val viewModel = FortuneViewModel(application)
        preferences.getDailyReadingsRemaining()
        preferences.incrementDailyReading()
        preferences.incrementDailyReading() // 0 free readings left

        viewModel.onPortalTapped()

        // Select Q10
        viewModel.selectQuestionForConfirmation("Q10")
        assertEquals("Q10", viewModel.uiState.value.pendingQuestionId)
        assertFalse("Quota dialog should NOT show before user confirms question", viewModel.uiState.value.isQuotaExhaustedDialogVisible)

        // Tap YES, PROCEED
        viewModel.confirmSelectedQuestion()

        // Confirmation is dismissed, quota dialog shows for Q10
        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertEquals("Q10", viewModel.uiState.value.selectedQuestionId)
        assertTrue("Quota exhaustion dialog should show after user confirms question", viewModel.uiState.value.isQuotaExhaustedDialogVisible)
    }
}
