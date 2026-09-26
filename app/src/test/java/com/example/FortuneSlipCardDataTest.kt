package com.example

import com.example.data.FortuneDataProvider
import com.example.data.room.FortuneRecordEntity
import com.example.model.FortuneState
import com.example.model.SealingState
import com.example.ui.model.FortuneSlipCardData
import com.example.ui.model.FortuneSlipCardMapper
import com.example.ui.model.FortuneSlipStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FortuneSlipCardDataTest {

    @Test
    fun test1_correctQuestionMapping() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 0,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        val expectedQuestion = FortuneDataProvider.getQuestionById("Q1")?.en
        assertEquals(expectedQuestion, cardData?.question)
    }

    @Test
    fun test2_correctFortuneAnswerMapping() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 2,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        val expectedAnswer = FortuneDataProvider.getQuestionById("Q1")?.answers?.get("2")?.en
        assertEquals(expectedAnswer, cardData?.answer)
    }

    @Test
    fun test3_oracleNumberZero() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 0,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertEquals(0, cardData?.oracleNumber)
    }

    @Test
    fun test4_oracleNumberNine() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 9,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertEquals(9, cardData?.oracleNumber)
    }

    @Test
    fun test5_optionalCategoryWhenPresent() {
        // Q1 is mapped to Love category
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 1,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertEquals("Love", cardData?.category)
    }

    @Test
    fun test6_optionalCategoryWhenAbsent() {
        val cardData = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Sample Question?",
            answer = "Sample Answer.",
            oracleNumber = 5,
            category = null,
            displayDate = "Jan 01, 2025 • 12:00",
            status = FortuneSlipStatus.LOCAL
        )
        assertNull(cardData.category)
    }

    @Test
    fun test7_optionalDateWhenPresent() {
        val timestamp = 1700000000000L
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 3,
            txTimestamp = timestamp,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertNotNull(cardData?.displayDate)
    }

    @Test
    fun test8_optionalDateWhenAbsent() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 3,
            txTimestamp = null,
            currentLang = "en"
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertNull(cardData?.displayDate)
    }

    @Test
    fun test9_localStatusMapping() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 4,
            sealingState = SealingState.IDLE
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertEquals(FortuneSlipStatus.LOCAL, cardData?.status)
    }

    @Test
    fun test10_confirmedVerifiedStatusMappingOnlyWhenProvenReconciled() {
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 5,
            txSignature = "5K...SigConfirmed",
            sealingState = SealingState.CONFIRMED
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertEquals(FortuneSlipStatus.VERIFIED, cardData?.status)

        // Room Entity represents confirmed reconciled record
        val entity = FortuneRecordEntity(
            timestamp = 1700000000000L,
            questionId = "Q1",
            questionText = "Will this couple have children?",
            chosenNumber = 5,
            answerText = "They will definitely have children.",
            txSignature = "5K...SigConfirmed",
            attestationId = "ORCL_123"
        )
        val entityData = FortuneSlipCardMapper.fromEntity(entity)
        assertEquals(FortuneSlipStatus.VERIFIED, entityData.status)
    }

    @Test
    fun test11_pendingUnknownStateMustNotMapToVerified() {
        // Even if txSignature is non-null, in-flight or pending states MUST NOT map to VERIFIED
        val statesToTest = listOf(
            SealingState.WALLET_CONNECTING,
            SealingState.SIGNING_DEVNET_MEMO,
            SealingState.RECONCILING
        )

        for (sealingState in statesToTest) {
            val state = FortuneState(
                selectedQuestionId = "Q1",
                finalChoice = 6,
                txSignature = "UnconfirmedInFlightTxSignature123",
                sealingState = sealingState
            )
            val cardData = FortuneSlipCardMapper.fromFortuneState(state)
            assertNotNull(cardData)
            assertNotEquals("Pending state must not map to VERIFIED", FortuneSlipStatus.VERIFIED, cardData?.status)
            assertEquals(FortuneSlipStatus.PENDING, cardData?.status)
        }
    }

    @Test
    fun test12_cancelledRejectedFailedStatesMustNotMapToVerified() {
        // A non-null txSignature in ERROR state must NOT map to VERIFIED or PENDING
        val state = FortuneState(
            selectedQuestionId = "Q1",
            finalChoice = 7,
            txSignature = "FailedOrCancelledTxSignature456",
            txError = "Transaction failed on-chain.",
            sealingState = SealingState.ERROR
        )
        val cardData = FortuneSlipCardMapper.fromFortuneState(state)
        assertNotNull(cardData)
        assertNotEquals("Failed/cancelled state must not map to VERIFIED", FortuneSlipStatus.VERIFIED, cardData?.status)
        assertNotEquals("Failed/cancelled state must not map to PENDING", FortuneSlipStatus.PENDING, cardData?.status)
        assertEquals(FortuneSlipStatus.LOCAL, cardData?.status)
    }
}
