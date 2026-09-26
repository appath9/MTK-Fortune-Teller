package com.example

// Fortune Teller - Myanmar Numerology Application
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.model.AppScreen
import com.example.ui.FortuneViewModel
import com.example.ui.components.GlobalShell
import com.example.ui.screens.AnimationPlaceholderScreen
import com.example.ui.screens.HomePortalScreen
import com.example.ui.screens.IntroScreen
import com.example.ui.screens.LoadingScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SealedHistoryScreen
import com.example.ui.screens.SelectQuestionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.MysticBackground
import com.example.util.HapticHelper
import com.example.util.SolanaWalletGatekeeper
import com.example.util.SolanaWalletManager
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: FortuneViewModel by viewModels()
    private val sender = ActivityResultSender(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FortuneTeller", "MainActivity initialized at ${System.currentTimeMillis()}")
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MysticBackground
                ) {
                    FortuneTellerApp(
                        viewModel = viewModel,
                        sender = sender
                    )
                }
            }
        }
    }
}

@Composable
fun FortuneTellerApp(
    viewModel: FortuneViewModel,
    sender: ActivityResultSender
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val walletManager = remember { SolanaWalletManager() }
    val coroutineScope = rememberCoroutineScope()
    val connectedWallet by viewModel.connectedWallet.collectAsStateWithLifecycle()

    // Android Back Button Logic:
    // If Side Menu is open -> Close Menu
    // Else if Help Modal is open -> Close Modal
    // Else if on Result/Animation -> Navigate back to Question Selection
    // Else if on Question Selection -> Navigate back to Home
    // Else -> Exit app
    BackHandler(enabled = true) {
        val handled = viewModel.handleBack()
        if (!handled) {
            (context as? Activity)?.finish()
        }
    }

    GlobalShell(
        currentLang = state.currentLang,
        currentScreen = state.currentScreen,
        isDrawerOpen = state.isDrawerOpen,
        isHelpModalOpen = state.isHelpModalOpen,
        onOpenDrawer = { viewModel.openDrawer() },
        onCloseDrawer = { viewModel.closeDrawer() },
        onOpenHelpModal = { viewModel.openHelpModal() },
        onCloseHelpModal = { viewModel.closeHelpModal() },
        onNavigateHome = { viewModel.resetAllState() },
        onNavigateToHistory = { viewModel.navigateToHistory() },
        onToggleLang = { viewModel.toggleLanguage() },
        onSetLang = { viewModel.setLanguage(it) },
        onOpenGuide = { viewModel.openGuide() },
        isMockSeekerElite = state.isMockSeekerElite,
        onToggleMockSeekerElite = { viewModel.toggleMockSeekerElite() }
    ) {
        AnimatedContent(
            targetState = state.currentScreen,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.Loading -> {
                    LoadingScreen(
                        currentLang = state.currentLang
                    )
                }
                AppScreen.Intro -> {
                    IntroScreen(
                        currentStep = state.introStep,
                        currentLang = state.currentLang,
                        onNextStep = { viewModel.nextIntroStep() },
                        onPrevStep = { viewModel.prevIntroStep() },
                        onStepSelect = { viewModel.setIntroStep(it) },
                        onAccept = { viewModel.acceptIntro() },
                        onClose = { viewModel.navigateToHome() }
                    )
                }
                AppScreen.Home -> {
                    HomePortalScreen(
                        currentLang = state.currentLang,
                        freeReadingsRemaining = state.freeReadingsRemaining,
                        accessTier = state.accessTier,
                        onPortalClick = { viewModel.onPortalTapped() },
                        onOpenGuide = { viewModel.openGuide() }
                    )
                }
                AppScreen.SelectQuestion -> {
                    SelectQuestionScreen(
                        currentLang = state.currentLang,
                        searchQuery = state.searchQuery,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        freeReadingsRemaining = state.freeReadingsRemaining,
                        prototypeReadingsRemaining = state.prototypeReadingsRemaining,
                        accessTier = state.accessTier,
                        pendingQuestionId = state.pendingQuestionId,
                        onSelectQuestionForConfirmation = { questionId ->
                            viewModel.selectQuestionForConfirmation(questionId)
                        },
                        onDismissQuestionConfirmation = {
                            viewModel.dismissQuestionConfirmation()
                        },
                        onConfirmQuestion = {
                            viewModel.confirmSelectedQuestion()
                        },
                        isQuotaExhaustedDialogVisible = state.isQuotaExhaustedDialogVisible,
                        onDismissQuotaDialog = { viewModel.dismissQuotaExhaustedDialog() },
                        onConnectWalletForWeb3 = {
                            coroutineScope.launch {
                                walletManager.connectAndAuthorize(
                                    sender = sender,
                                    onSuccess = { pubkeyBytes, _ ->
                                        val base58 = SolanaWalletGatekeeper.encodeToBase58(pubkeyBytes)
                                        viewModel.onWalletConnected(base58)
                                        viewModel.proceedToWeb3ModeFromQuotaDialog()
                                    },
                                    onError = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        },
                        connectedWallet = connectedWallet,
                        onDisconnectWallet = { viewModel.disconnectWallet() },
                        onQuestionHoldConfirmed = { questionId ->
                            viewModel.onQuestionHoldConfirmed(questionId)
                        },
                        onBackToHome = { viewModel.navigateToHome() }
                    )
                }
                AppScreen.AnimationPlaceholder -> {
                    AnimationPlaceholderScreen(
                        currentLang = state.currentLang,
                        selectedQuestionId = state.selectedQuestionId,
                        isSubmittingTransaction = state.isSubmittingTransaction,
                        attestationStatusText = state.attestationStatusText,
                        onBallSelected = { choice ->
                            HapticHelper.playMysticRumble(context)
                            viewModel.onOracleNumberSelected(
                                sender = sender,
                                walletManager = walletManager,
                                choice = choice,
                                onError = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        onBack = { viewModel.onTryAgain() }
                    )
                }
                AppScreen.Result -> {
                    ResultScreen(
                        currentLang = state.currentLang,
                        selectedQuestionId = state.selectedQuestionId,
                        finalChoice = state.finalChoice,
                        onTryAgain = { viewModel.onTryAgain() },
                        connectedWallet = connectedWallet,
                        txSignature = state.txSignature,
                        txTimestamp = state.txTimestamp,
                        isSubmittingTransaction = state.isSubmittingTransaction,
                        attestationStatusText = state.attestationStatusText,
                        isOfferingInFlight = state.isOfferingInFlight,
                        offeringStatusText = state.offeringStatusText,
                        offeringTxSignature = state.offeringTxSignature,
                        offeringConfirmed = state.offeringConfirmed,
                        offeringUnknownConfirmation = state.offeringUnknownConfirmation,
                        onMakeOffering = {
                            viewModel.onMakeOracleOffering(
                                sender = sender,
                                walletManager = walletManager,
                                onStatusMessage = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onSealOnSolana = {
                            viewModel.sealCurrentReadingOnChain(
                                sender = sender,
                                walletManager = walletManager,
                                onError = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        isWeb3Mode = state.isWeb3Mode,
                        sealingState = state.sealingState,
                        freeReadingsRemaining = state.freeReadingsRemaining,
                        accessTier = state.accessTier,
                        isResultRevealed = state.isResultRevealed,
                        onResultRevealed = { viewModel.markResultRevealed() }
                    )
                }
                AppScreen.History -> {
                    val sealedRecords by viewModel.sealedHistoryRecords.collectAsStateWithLifecycle()
                    SealedHistoryScreen(
                        currentLang = state.currentLang,
                        sealedRecords = sealedRecords,
                        onBack = { viewModel.navigateToHome() }
                    )
                }
            }
        }
    }
}
