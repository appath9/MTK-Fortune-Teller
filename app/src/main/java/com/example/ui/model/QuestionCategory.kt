package com.example.ui.model

enum class QuestionCategory {
    ALL,
    LOVE,
    CAREER,
    MONEY,
    DECISION,
    PERSONAL_GROWTH,
    GENERAL_FORTUNE;

    fun getLabel(isBurmese: Boolean): String {
        return if (isBurmese) {
            when (this) {
                ALL -> "အားလုံး"
                LOVE -> "အချစ်ရေး"
                CAREER -> "အလုပ်အကိုင်"
                MONEY -> "စီးပွား/ငွေကြေး"
                DECISION -> "ဆုံးဖြတ်ချက်"
                PERSONAL_GROWTH -> "ကိုယ်ပိုင်တိုးတက်မှု"
                GENERAL_FORTUNE -> "အထွေထွေကံဇာတာ"
            }
        } else {
            when (this) {
                ALL -> "All"
                LOVE -> "Love"
                CAREER -> "Career"
                MONEY -> "Money"
                DECISION -> "Decision"
                PERSONAL_GROWTH -> "Personal Growth"
                GENERAL_FORTUNE -> "General Fortune"
            }
        }
    }
}

object QuestionCategoryMapper {
    private val loveIds = setOf("Q1", "Q2", "Q15", "Q16", "Q44", "Q49", "Q50")
    private val careerIds = setOf("Q6", "Q22", "Q23", "Q41", "Q51", "Q52", "Q53", "Q54", "Q60")
    private val moneyIds = setOf("Q10", "Q12", "Q17", "Q18", "Q24", "Q25", "Q26", "Q27", "Q28", "Q29", "Q42", "Q64")
    private val decisionIds = setOf("Q3", "Q20", "Q32", "Q39", "Q40", "Q47", "Q55", "Q59", "Q62")
    private val personalGrowthIds = setOf("Q4", "Q5", "Q30", "Q31", "Q56", "Q57", "Q58", "Q61", "Q63")
    private val generalFortuneIds = setOf("Q7", "Q8", "Q9", "Q11", "Q13", "Q14", "Q19", "Q21", "Q33", "Q34", "Q35", "Q36", "Q37", "Q38", "Q43", "Q45", "Q46", "Q48")

    fun matchesCategory(questionId: String, category: QuestionCategory): Boolean {
        return when (category) {
            QuestionCategory.ALL -> true
            QuestionCategory.LOVE -> questionId in loveIds
            QuestionCategory.CAREER -> questionId in careerIds
            QuestionCategory.MONEY -> questionId in moneyIds
            QuestionCategory.DECISION -> questionId in decisionIds
            QuestionCategory.PERSONAL_GROWTH -> questionId in personalGrowthIds
            QuestionCategory.GENERAL_FORTUNE -> questionId in generalFortuneIds
        }
    }

    fun getCategoryForQuestion(questionId: String): QuestionCategory {
        return when {
            questionId in loveIds -> QuestionCategory.LOVE
            questionId in careerIds -> QuestionCategory.CAREER
            questionId in moneyIds -> QuestionCategory.MONEY
            questionId in decisionIds -> QuestionCategory.DECISION
            questionId in personalGrowthIds -> QuestionCategory.PERSONAL_GROWTH
            questionId in generalFortuneIds -> QuestionCategory.GENERAL_FORTUNE
            else -> QuestionCategory.ALL
        }
    }
}
