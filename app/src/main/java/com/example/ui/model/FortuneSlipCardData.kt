package com.example.ui.model

import com.example.data.FortuneDataProvider
import com.example.data.room.FortuneRecordEntity
import com.example.model.FortuneState
import com.example.model.SealingState
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Presentation-only status for the QR-free Visual Fortune Slip Card.
 *
 * This status belongs strictly to the presentation/sharing layer.
 * It must NEVER infer [VERIFIED] merely from a non-null transaction signature.
 */
enum class FortuneSlipStatus {
    LOCAL,
    VERIFIED,
    PENDING
}

/**
 * Presentation-only data model for rendering a Fortune Slip card.
 *
 * Does not own or interpret blockchain proof.
 */
data class FortuneSlipCardData(
    val appName: String,
    val question: String,
    val answer: String,
    val oracleNumber: Int?,
    val category: String? = null,
    val displayDate: String? = null,
    val status: FortuneSlipStatus = FortuneSlipStatus.LOCAL
)

/**
 * Helper mapper to convert domain and state models into [FortuneSlipCardData].
 */
object FortuneSlipCardMapper {

    /**
     * Maps current [FortuneState] into presentation-only [FortuneSlipCardData].
     *
     * Strict proof-state rule: Only [SealingState.CONFIRMED] maps to [FortuneSlipStatus.VERIFIED].
     * In-flight sealing states ([SealingState.WALLET_CONNECTING], [SealingState.SIGNING_DEVNET_MEMO],
     * [SealingState.RECONCILING]) map to [FortuneSlipStatus.PENDING].
     * Unsealed, failed, or cancelled states map to [FortuneSlipStatus.LOCAL].
     */
    fun fromFortuneState(
        state: FortuneState,
        isBurmese: Boolean = state.currentLang == "mm",
        appName: String = if (isBurmese) "မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်" else "MTK Fortune Teller",
        formatter: DateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    ): FortuneSlipCardData? {
        val questionId = state.selectedQuestionId ?: return null
        val choice = state.finalChoice ?: return null

        val questionObj = FortuneDataProvider.getQuestionById(questionId)
        val questionText = if (isBurmese) questionObj?.mm ?: "" else questionObj?.en ?: ""
        val answerObj = questionObj?.answers?.get(choice.toString())
        val answerText = if (isBurmese) answerObj?.mm ?: "" else answerObj?.en ?: ""

        val categoryEnum = QuestionCategoryMapper.getCategoryForQuestion(questionId)
        val categoryText = categoryEnum.getLabel(isBurmese)

        val dateStr = state.txTimestamp?.let { formatter.format(Date(it)) }

        val status = when (state.sealingState) {
            SealingState.CONFIRMED -> FortuneSlipStatus.VERIFIED
            SealingState.WALLET_CONNECTING,
            SealingState.SIGNING_DEVNET_MEMO,
            SealingState.RECONCILING -> FortuneSlipStatus.PENDING
            SealingState.IDLE,
            SealingState.ERROR -> FortuneSlipStatus.LOCAL
        }

        return FortuneSlipCardData(
            appName = appName,
            question = questionText,
            answer = answerText,
            oracleNumber = choice,
            category = categoryText,
            displayDate = dateStr,
            status = status
        )
    }

    /**
     * Maps explicit details directly into [FortuneSlipCardData].
     */
    fun fromDetails(
        questionText: String,
        answerText: String,
        oracleNumber: Int?,
        category: String? = null,
        displayDate: String? = null,
        sealingState: SealingState = SealingState.IDLE,
        appName: String = "MTK Fortune Teller"
    ): FortuneSlipCardData {
        val status = when (sealingState) {
            SealingState.CONFIRMED -> FortuneSlipStatus.VERIFIED
            SealingState.WALLET_CONNECTING,
            SealingState.SIGNING_DEVNET_MEMO,
            SealingState.RECONCILING -> FortuneSlipStatus.PENDING
            SealingState.IDLE,
            SealingState.ERROR -> FortuneSlipStatus.LOCAL
        }

        return FortuneSlipCardData(
            appName = appName,
            question = questionText,
            answer = answerText,
            oracleNumber = oracleNumber,
            category = category,
            displayDate = displayDate,
            status = status
        )
    }

    /**
     * Maps a Room [FortuneRecordEntity] into [FortuneSlipCardData].
     *
     * Room entities are strictly persisted upon confirmed reconciliation,
     * so their status maps to [FortuneSlipStatus.VERIFIED].
     */
    fun fromEntity(
        entity: FortuneRecordEntity,
        isBurmese: Boolean = false,
        appName: String = if (isBurmese) "မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်" else "MTK Fortune Teller",
        formatter: DateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    ): FortuneSlipCardData {
        val categoryText = QuestionCategoryMapper.getCategoryForQuestion(entity.questionId).getLabel(isBurmese)
        val dateStr = formatter.format(Date(entity.timestamp))

        return FortuneSlipCardData(
            appName = appName,
            question = entity.questionText,
            answer = entity.answerText,
            oracleNumber = entity.chosenNumber,
            category = categoryText,
            displayDate = dateStr,
            status = FortuneSlipStatus.VERIFIED
        )
    }
}
