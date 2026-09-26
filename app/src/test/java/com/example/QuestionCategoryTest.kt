package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortuneDataProvider
import com.example.model.AppScreen
import com.example.ui.FortuneViewModel
import com.example.ui.model.QuestionCategory
import com.example.ui.model.QuestionCategoryMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuestionCategoryTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testAllCategoryReturnsEveryCurrentQuestion() {
        val allQuestions = FortuneDataProvider.fortuneData
        assertEquals("Total question count must be 64", 64, allQuestions.size)

        allQuestions.forEach { question ->
            assertTrue(
                "Question ${question.id} must match ALL category",
                QuestionCategoryMapper.matchesCategory(question.id, QuestionCategory.ALL)
            )
        }
    }

    @Test
    fun testEachCategoryReturnsExpectedIDsAndNoDuplicates() {
        val allQuestions = FortuneDataProvider.fortuneData
        val allIds = allQuestions.map { it.id }.toSet()

        val expectedLove = setOf("Q1", "Q2", "Q15", "Q16", "Q44", "Q49", "Q50")
        val expectedCareer = setOf("Q6", "Q22", "Q23", "Q41", "Q51", "Q52", "Q53", "Q54", "Q60")
        val expectedMoney = setOf("Q10", "Q12", "Q17", "Q18", "Q24", "Q25", "Q26", "Q27", "Q28", "Q29", "Q42", "Q64")
        val expectedDecision = setOf("Q3", "Q20", "Q32", "Q39", "Q40", "Q47", "Q55", "Q59", "Q62")
        val expectedPersonalGrowth = setOf("Q4", "Q5", "Q30", "Q31", "Q56", "Q57", "Q58", "Q61", "Q63")
        val expectedGeneralFortune = setOf("Q7", "Q8", "Q9", "Q11", "Q13", "Q14", "Q19", "Q21", "Q33", "Q34", "Q35", "Q36", "Q37", "Q38", "Q43", "Q45", "Q46", "Q48")

        val actualLove = allQuestions.filter { QuestionCategoryMapper.matchesCategory(it.id, QuestionCategory.LOVE) }.map { it.id }.toSet()
        val actualCareer = allQuestions.filter { QuestionCategoryMapper.matchesCategory(it.id, QuestionCategory.CAREER) }.map { it.id }.toSet()
        val actualMoney = allQuestions.filter { QuestionCategoryMapper.matchesCategory(it.id, QuestionCategory.MONEY) }.map { it.id }.toSet()
        val actualDecision = allQuestions.filter { QuestionCategoryMapper.matchesCategory(it.id, QuestionCategory.DECISION) }.map { it.id }.toSet()
        val actualPersonalGrowth = allQuestions.filter { QuestionCategoryMapper.matchesCategory(it.id, QuestionCategory.PERSONAL_GROWTH) }.map { it.id }.toSet()
        val actualGeneralFortune = allQuestions.filter { QuestionCategoryMapper.matchesCategory(it.id, QuestionCategory.GENERAL_FORTUNE) }.map { it.id }.toSet()

        assertEquals(expectedLove, actualLove)
        assertEquals(expectedCareer, actualCareer)
        assertEquals(expectedMoney, actualMoney)
        assertEquals(expectedDecision, actualDecision)
        assertEquals(expectedPersonalGrowth, actualPersonalGrowth)
        assertEquals(expectedGeneralFortune, actualGeneralFortune)

        // Ensure disjoint partition covering all 64 question IDs
        val unionSet = actualLove + actualCareer + actualMoney + actualDecision + actualPersonalGrowth + actualGeneralFortune
        assertEquals("Total mapped unique IDs must equal 64", 64, unionSet.size)
        assertEquals("All IDs Q1..Q64 must be covered", allIds, unionSet)
    }

    @Test
    fun testSearchAndCategoryFilteringCombineWithLogicalAnd() {
        val allQuestions = FortuneDataProvider.fortuneData

        // Category LOVE + search query "baby" or "2"
        val category = QuestionCategory.LOVE
        val searchQuery = "baby"
        val normalizedQuery = FortuneDataProvider.normalizeSearch(searchQuery)

        val filtered = allQuestions.filter { q ->
            val matchesCat = QuestionCategoryMapper.matchesCategory(q.id, category)
            if (!matchesCat) return@filter false
            val normEn = FortuneDataProvider.normalizeSearch(q.en)
            val normMm = FortuneDataProvider.normalizeSearch(q.mm)
            val normId = FortuneDataProvider.normalizeSearch(q.id)
            normEn.contains(normalizedQuery) || normMm.contains(normalizedQuery) || normId.contains(normalizedQuery)
        }

        // Q2 is "Will this baby be a boy or a girl?"
        assertTrue("Filtered result should contain Q2", filtered.any { it.id == "Q2" })
        assertFalse("Filtered result should NOT contain Q1", filtered.any { it.id == "Q1" })
    }

    @Test
    fun testEmptyFilteredResultHandledSafely() {
        val allQuestions = FortuneDataProvider.fortuneData
        val category = QuestionCategory.LOVE
        val searchQuery = "NON_EXISTENT_QUERY_XYZ_123"
        val normalizedQuery = FortuneDataProvider.normalizeSearch(searchQuery)

        val filtered = allQuestions.filter { q ->
            val matchesCat = QuestionCategoryMapper.matchesCategory(q.id, category)
            if (!matchesCat) return@filter false
            val normEn = FortuneDataProvider.normalizeSearch(q.en)
            normEn.contains(normalizedQuery)
        }

        assertTrue("Filtered list must be empty for non-existent search query", filtered.isEmpty())
    }

    @Test
    fun testSelectingFilteredQuestionPreservesOriginalIdAndConfirmationFlow() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        val questionId = "Q15" // Love category
        viewModel.selectQuestionForConfirmation(questionId)

        assertEquals("Q15", viewModel.uiState.value.pendingQuestionId)
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)

        viewModel.confirmSelectedQuestion()

        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertEquals("Q15", viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testDismissingConfirmationDoesNotStartReading() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        viewModel.selectQuestionForConfirmation("Q50")
        assertEquals("Q50", viewModel.uiState.value.pendingQuestionId)

        viewModel.dismissQuestionConfirmation()

        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertNull(viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun testAndroidBackDismissesConfirmationWithoutConfirmingQuestion() {
        val viewModel = FortuneViewModel(application)
        viewModel.onPortalTapped()

        viewModel.selectQuestionForConfirmation("Q22")
        assertEquals("Q22", viewModel.uiState.value.pendingQuestionId)

        val handled = viewModel.handleBack()
        assertTrue("handleBack should dismiss confirmation", handled)

        assertNull(viewModel.uiState.value.pendingQuestionId)
        assertNull(viewModel.uiState.value.selectedQuestionId)
        assertEquals(AppScreen.SelectQuestion, viewModel.uiState.value.currentScreen)
    }
}
