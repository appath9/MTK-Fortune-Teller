package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.ui.components.FortuneSlipCard
import com.example.ui.model.FortuneSlipCardData
import com.example.ui.model.FortuneSlipStatus
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class FortuneSlipCardUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRendersLocalStatusCard() {
        composeTestRule.setContent {
            MyApplicationTheme {
                FortuneSlipCard(
                    data = FortuneSlipCardData(
                        appName = "MTK Fortune Teller",
                        question = "Will I succeed in my venture?",
                        answer = "Patience and hard work will yield positive results.",
                        oracleNumber = 3,
                        category = "Career",
                        displayDate = "Feb 10, 2025 • 12:00",
                        status = FortuneSlipStatus.LOCAL
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Will I succeed in my venture?", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Patience and hard work will yield positive results.", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("ORACLE № 3", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Career", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("LOCAL READING", substring = true).assertIsDisplayed()
    }

    @Test
    fun testRendersVerifiedStatusCard() {
        composeTestRule.setContent {
            MyApplicationTheme {
                FortuneSlipCard(
                    data = FortuneSlipCardData(
                        appName = "MTK Fortune Teller",
                        question = "Is this investment wise?",
                        answer = "A steady approach brings long-term security.",
                        oracleNumber = 6,
                        category = "Money",
                        displayDate = "Feb 10, 2025 • 14:00",
                        status = FortuneSlipStatus.VERIFIED
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("VERIFIED ON SOLANA DEVNET", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("ORACLE № 6", substring = true).assertIsDisplayed()
    }

    @Test
    fun testRendersPendingStatusCard() {
        composeTestRule.setContent {
            MyApplicationTheme {
                FortuneSlipCard(
                    data = FortuneSlipCardData(
                        appName = "MTK Fortune Teller",
                        question = "What does the future hold?",
                        answer = "Opportunities will emerge when least expected.",
                        oracleNumber = 0,
                        category = "General Fortune",
                        displayDate = "Feb 10, 2025 • 16:00",
                        status = FortuneSlipStatus.PENDING
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("ON-CHAIN VERIFICATION PENDING", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("ORACLE № 0", substring = true).assertIsDisplayed()
    }

    @Test
    fun testRendersBoundaryNumberNine() {
        composeTestRule.setContent {
            MyApplicationTheme {
                FortuneSlipCard(
                    data = FortuneSlipCardData(
                        appName = "MTK Fortune Teller",
                        question = "Will peace return?",
                        answer = "Harmony shall prevail soon.",
                        oracleNumber = 9,
                        status = FortuneSlipStatus.LOCAL
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("ORACLE № 9", substring = true).assertIsDisplayed()
    }

    @Test
    fun testRendersLongQuestionAndAnswerContent() {
        val longQuestion = "Should I consider relocating to another country for higher education and career development in the upcoming academic year?"
        val longAnswer = "An unexpected opportunity will present itself through an acquaintance from abroad. Taking this step will yield long-term spiritual and material growth, provided you stay true to your values."

        composeTestRule.setContent {
            MyApplicationTheme {
                FortuneSlipCard(
                    data = FortuneSlipCardData(
                        appName = "MTK Fortune Teller",
                        question = longQuestion,
                        answer = longAnswer,
                        oracleNumber = 7,
                        category = "Personal Growth",
                        displayDate = "Feb 10, 2025 • 20:00",
                        status = FortuneSlipStatus.VERIFIED
                    )
                )
            }
        }

        composeTestRule.onNodeWithText(longQuestion, substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText(longAnswer, substring = true).assertIsDisplayed()
    }
}
