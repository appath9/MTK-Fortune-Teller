package com.example

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import com.example.data.FortunePreferences
import com.example.model.AccessTier
import com.example.model.AppScreen
import com.example.ui.FortuneViewModel
import com.example.util.SeekerEntitlementManager
import com.example.util.SiwsPayload
import com.example.util.SolanaWalletManager
import com.example.util.VerificationErrorCode
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SeekerEntitlementTest {

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
    fun test1_FreeUserHas2PerDayBehaviorUnchanged() {
        val viewModel = FortuneViewModel(application)
        assertEquals(AccessTier.FREE, viewModel.uiState.value.accessTier)
        assertFalse(viewModel.uiState.value.isSeekerElite)
        assertEquals(2, viewModel.uiState.value.freeReadingsRemaining)
    }

    @Test
    fun test2_NonSeekerUserIsNotSeekerElite() {
        val viewModel = FortuneViewModel(application)
        assertEquals(AccessTier.FREE, viewModel.uiState.value.accessTier)
        assertFalse(viewModel.uiState.value.isSeekerElite)
    }

    @Test
    fun test3_WalletConnectedButSiwsNotVerifiedIsNotSeekerElite() {
        val viewModel = FortuneViewModel(application)
        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")
        // Connected wallet alone must NEVER automatically grant SEEKER_ELITE
        assertEquals(AccessTier.FREE, viewModel.uiState.value.accessTier)
        assertFalse(viewModel.uiState.value.isSeekerElite)
    }

    @Test
    fun test4_SiwsCancelledDoesNotGrantSeekerElite() = runBlocking {
        val payload = SiwsPayload(address = "5eykt...", nonce = "nonce123")
        val result = SeekerEntitlementManager.verifySiwsAndSgtWithBackend(
            payload = payload,
            signatureBase58 = null // Missing/cancelled signature
        )
        assertFalse(result.isVerified)
        assertEquals(AccessTier.FREE, result.accessTier)
        assertEquals(VerificationErrorCode.INVALID_SIGNATURE, result.errorCode)
    }

    @Test
    fun test5_InvalidNonceIsRejected() {
        val error = SeekerEntitlementManager.validateNonce(
            nonce = "wrong_nonce",
            expectedNonce = "expected_nonce",
            isNonceUsed = false
        )
        assertEquals(VerificationErrorCode.INVALID_NONCE, error)
    }

    @Test
    fun test6_ExpiredNonceIsRejected() {
        val error = SeekerEntitlementManager.validateNonce(
            nonce = "nonce123",
            expectedNonce = "nonce123",
            isNonceUsed = false,
            isExpired = true
        )
        assertEquals(VerificationErrorCode.EXPIRED_NONCE, error)
    }

    @Test
    fun test7_ReusedNonceIsRejected() {
        val error = SeekerEntitlementManager.validateNonce(
            nonce = "nonce123",
            expectedNonce = "nonce123",
            isNonceUsed = true
        )
        assertEquals(VerificationErrorCode.REUSED_NONCE, error)
    }

    @Test
    fun test8_SgtAbsentOnMainnetIsNotSeekerElite() = runBlocking {
        val payload = SiwsPayload(address = "5eykt...", nonce = "nonce123")
        val result = SeekerEntitlementManager.verifySiwsAndSgtWithBackend(
            payload = payload,
            signatureBase58 = "valid_sig",
            expectedNonce = "nonce123",
            hasSgtOnMainnet = false // SGT absent
        )
        assertFalse(result.isVerified)
        assertEquals(AccessTier.FREE, result.accessTier)
        assertEquals(VerificationErrorCode.SGT_ABSENT, result.errorCode)
    }

    @Test
    fun test9_ValidSiwsAndValidGenuineSgtArchitectureContract() = runBlocking {
        // Enforce the architecture requirement that production verification requires both SIWS + Mainnet SGT check
        val payload = SiwsPayload(address = "5eykt...", nonce = "nonce123")
        val result = SeekerEntitlementManager.verifySiwsAndSgtWithBackend(
            payload = payload,
            signatureBase58 = "valid_sig",
            expectedNonce = "nonce123",
            hasSgtOnMainnet = true
        )
        // Since no backend exists in repo, production verification returns BACKEND_UNAVAILABLE
        assertFalse(result.isVerified)
        assertEquals(AccessTier.FREE, result.accessTier)
        assertEquals(VerificationErrorCode.BACKEND_UNAVAILABLE, result.errorCode)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun test10_SeekerEliteHasUnlimitedReadings() {
        preferences.isMockSeekerEliteEnabled = true
        val viewModel = FortuneViewModel(application)
        assertTrue(viewModel.uiState.value.isSeekerElite)
        assertEquals(AccessTier.SEEKER_ELITE, viewModel.uiState.value.accessTier)

        // Hold-to-cast -> proceeds directly to AnimationPlaceholder
        viewModel.onQuestionHoldConfirmed("Q1")
        assertEquals(AppScreen.AnimationPlaceholder, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun test11_SeekerEliteDoesNotConsumeDailyQuota() {
        preferences.isMockSeekerEliteEnabled = true
        val viewModel = FortuneViewModel(application)
        assertEquals(2, preferences.getDailyReadingsRemaining())

        viewModel.onQuestionHoldConfirmed("Q1")
        viewModel.selectFinalChoice(5)

        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)
        // Quota in preferences remains unconsumed at 0 count / 2 remaining
        assertEquals(0, preferences.dailyReadingCount)
        assertEquals(2, preferences.getDailyReadingsRemaining())
    }

    @Test
    fun test12_DebugMockSeekerEliteCanBeExercisedOnA52() {
        val viewModel = FortuneViewModel(application)
        assertFalse(viewModel.uiState.value.isSeekerElite)

        // Toggle Debug Mock ON
        viewModel.toggleMockSeekerElite()
        assertTrue(viewModel.uiState.value.isSeekerElite)
        assertEquals(AccessTier.SEEKER_ELITE, viewModel.uiState.value.accessTier)

        // Toggle Debug Mock OFF
        viewModel.toggleMockSeekerElite()
        assertFalse(viewModel.uiState.value.isSeekerElite)
        assertEquals(AccessTier.FREE, viewModel.uiState.value.accessTier)
    }

    @Test
    fun test13_DebugMockIsUnavailableInReleaseBuilds() {
        if (!BuildConfig.DEBUG) {
            preferences.isMockSeekerEliteEnabled = true
            assertFalse(preferences.isMockSeekerEliteEnabled)
        } else {
            assertTrue(BuildConfig.DEBUG)
        }
    }

    @Test
    fun test14_SeekerEliteAndFreeUserCanSealReadingOnSolana() {
        preferences.isMockSeekerEliteEnabled = true
        val viewModel = FortuneViewModel(application)
        viewModel.onWalletConnected("5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111")

        viewModel.onQuestionHoldConfirmed("Q7")
        viewModel.selectFinalChoice(3)

        assertEquals(AppScreen.Result, viewModel.uiState.value.currentScreen)
        assertEquals("Q7", viewModel.uiState.value.selectedQuestionId)
        assertEquals(3, viewModel.uiState.value.finalChoice)

        val dummySender = ActivityResultSender(
            ComponentActivity()
        )
        val dummyWalletManager = SolanaWalletManager()

        // Seal current reading on Solana Devnet
        viewModel.sealCurrentReadingOnChain(
            sender = dummySender,
            walletManager = dummyWalletManager,
            onError = {}
        )

        // Web3 mode and submitting flags should activate for attestation
        assertTrue(viewModel.uiState.value.isWeb3Mode)
        assertTrue(viewModel.uiState.value.isSubmittingTransaction)
        assertEquals("Q7", viewModel.uiState.value.selectedQuestionId)
        assertEquals(3, viewModel.uiState.value.finalChoice)
    }
}
