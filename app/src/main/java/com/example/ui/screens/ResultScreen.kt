package com.example.ui.screens

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.components.FortuneProofCard
import com.example.ui.components.captureAndShareFortuneCard
import com.example.data.FortuneDataProvider
import com.example.ui.components.ConfettiCelebrationCanvas
import com.example.ui.theme.MysticBackground
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticTextSecondary
import com.example.util.HapticHelper
import com.example.util.SolanaWalletGatekeeper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.activity.compose.BackHandler
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AccessTier
import com.example.model.SealingState
import com.example.ui.components.FortuneProofCard
import com.example.ui.components.FortuneSlipCard
import com.example.ui.components.sharePredictionTextOnly
import com.example.ui.model.FortuneSlipCardData
import com.example.ui.model.FortuneSlipCardMapper
import com.example.ui.model.FortuneSlipStatus
import com.example.ui.model.QuestionCategoryMapper
import com.example.util.FortuneSlipShareHelper

@Composable
fun ResultScreen(
    currentLang: String,
    selectedQuestionId: String?,
    finalChoice: Int?,
    onTryAgain: () -> Unit,
    connectedWallet: String? = null,
    txSignature: String? = null,
    txTimestamp: Long? = null,
    isSubmittingTransaction: Boolean = false,
    attestationStatusText: String? = null,
    isOfferingInFlight: Boolean = false,
    offeringStatusText: String? = null,
    offeringTxSignature: String? = null,
    offeringConfirmed: Boolean = false,
    offeringUnknownConfirmation: Boolean = false,
    onMakeOffering: () -> Unit = {},
    onSealOnSolana: () -> Unit = {},
    isWeb3Mode: Boolean = false,
    sealingState: SealingState = SealingState.IDLE,
    freeReadingsRemaining: Int = 2,
    accessTier: AccessTier = AccessTier.FREE,
    isResultRevealed: Boolean = false,
    onResultRevealed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    val isBurmese = currentLang == "mm"
    val question = remember(selectedQuestionId) {
        selectedQuestionId?.let { FortuneDataProvider.getQuestionById(it) }
    }

    val burmeseDigits = listOf("၀", "၁", "၂", "၃", "၄", "၅", "၆", "၇", "၈", "၉")
    val resultNumber = finalChoice ?: 0
    val displayResultDigit = if (isBurmese) {
        burmeseDigits.getOrElse(resultNumber) { resultNumber.toString() }
    } else {
        resultNumber.toString()
    }

    val localizedAnswer = remember(question, resultNumber, isBurmese) {
        val answerObj = question?.answers?.get(resultNumber.toString())
        if (isBurmese) answerObj?.mm ?: "ကံကြမ္မာ အဖြေ ရှာမတွေ့ပါ။"
        else answerObj?.en ?: "Divine answer not found."
    }

    val formattedTimestamp = remember(txTimestamp) {
        val timeMs = txTimestamp ?: System.currentTimeMillis()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formatter.format(Date(timeMs))
    }

    val openExplorer: () -> Unit = {
        if (!txSignature.isNullOrBlank()) {
            val explorerUrl = SolanaWalletGatekeeper.buildExplorerUrl(txSignature, isTx = true)
            try {
                uriHandler.openUri(explorerUrl)
            } catch (_: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(explorerUrl))
                context.startActivity(intent)
            }
        }
    }

    var isFortuneSlipPreviewVisible by remember { mutableStateOf(false) }

    // 1. The Suspense Countdown: 9 down to 0 at 150ms intervals
    var countdownValue by remember { mutableIntStateOf(9) }
    var isCountingDown by remember { mutableStateOf(!isResultRevealed) }

    LaunchedEffect(Unit) {
        if (!isResultRevealed) {
            for (i in 9 downTo 0) {
                countdownValue = i
                delay(150)
            }
            isCountingDown = false
            HapticHelper.playMysticRumble(context)
            onResultRevealed()
        }
    }

    // Countdown glowing pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "countdown_pulse")
    val countdownGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdown_glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("result_screen"),
        contentAlignment = Alignment.Center
    ) {
        if (isCountingDown) {
            // 1. Full-screen Suspense Countdown View
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("countdown_view")
            ) {
                Text(
                    text = if (isBurmese) "ကံကြမ္မာ အဖြေ တွက်ချက်နေပါသည်..." else "Unveiling Divine Prediction...",
                    color = MysticGoldBright,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Countdown Circle with glowing primary purple text
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = CircleShape,
                            ambientColor = MysticPrimaryPurple.copy(alpha = countdownGlowAlpha),
                            spotColor = MysticPrimaryPurple.copy(alpha = countdownGlowAlpha)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MysticSurface,
                                    MysticBackground
                                )
                            )
                        )
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MysticPrimaryPurple,
                                    MysticGold.copy(alpha = 0.7f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBurmese) burmeseDigits.getOrElse(countdownValue) { countdownValue.toString() } else countdownValue.toString(),
                        color = MysticPrimaryPurple,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.shadow(
                            elevation = 16.dp,
                            ambientColor = MysticPrimaryPurple
                        )
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = if (isBurmese) "စိတ်ကို တည်ငြိမ်စွာ ထားပါ" else "Focus your energy",
                    color = MysticTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        } else {
            // 2. The Final Reveal: Centered glass-morphism card with 2px purple border (#D0BCFF)
            AnimatedVisibility(
                visible = !isCountingDown,
                enter = fadeIn(tween(400)) + scaleIn(tween(400, easing = FastOutSlowInEasing))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FortuneProofCard(
                        isBurmese = isBurmese,
                        accessTier = accessTier,
                        isWeb3Mode = isWeb3Mode,
                        freeReadingsRemaining = freeReadingsRemaining,
                        displayResultDigit = displayResultDigit,
                        questionText = if (isBurmese) question?.mm ?: "" else question?.en ?: "",
                        localizedAnswer = localizedAnswer,
                        txSignature = txSignature,
                        graphicsLayer = graphicsLayer,
                        modifier = Modifier.testTag("result_card")
                    )

                    // On-Chain Attestation Details Card (Only shown in Web3 mode when confirmed and valid txSignature exists)
                    if (isWeb3Mode && sealingState == SealingState.CONFIRMED && !txSignature.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color(0xFF14F195).copy(alpha = 0.15f),
                                    spotColor = Color(0xFF14F195).copy(alpha = 0.25f)
                                )
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1E1B2E))
                                .border(
                                    1.dp,
                                    Color(0xFF14F195).copy(alpha = 0.5f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, color = Color(0xFF14F195)),
                                    onClick = openExplorer
                                )
                                .padding(18.dp)
                                .testTag("on_chain_attestation_card")
                        ) {
                            Column {
                                // Header Row: Status Indicator Dot + Title
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF14F195))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBurmese) "🟢 Solana Devnet တွင် မှတ်တမ်းတင်ပြီး" else "🟢 Recorded on Solana Devnet",
                                        color = Color(0xFF14F195),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Metadata 2-Column Key/Value Layout
                                val questionIdDisplay = "Q#${selectedQuestionId?.removePrefix("Q") ?: "-"}"
                                val walletDisplay = shortenWallet(connectedWallet)
                                val txDisplay = shortenTxSignature(txSignature)

                                // Row 1: Question ID & Choice Number
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBurmese) "မေးခွန်း အိုင်ဒီ" else "Question ID",
                                            color = Color(0xFFA09CAB),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = questionIdDisplay,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBurmese) "ရွေးချယ်မှု အမှတ်" else "Choice Number",
                                            color = Color(0xFFA09CAB),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = displayResultDigit,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Row 2: Timestamp & Payer Wallet
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBurmese) "အချိန်" else "Timestamp",
                                            color = Color(0xFFA09CAB),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = formattedTimestamp,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBurmese) "ပေးချေသည့် ဝေါလက်" else "Payer Wallet",
                                            color = Color(0xFFA09CAB),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = walletDisplay,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Row 3: Transaction Signature with Explorer Hint
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBurmese) "လုပ်ဆောင်မှု သက်သေ" else "Transaction",
                                            color = Color(0xFFA09CAB),
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = txDisplay,
                                            color = Color(0xFF14F195),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = if (isBurmese) "Explorer တွင် ကြည့်မည် ↗" else "View on Explorer ↗",
                                        color = Color(0xFF14F195).copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons Column
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Share Prediction Button
                        Button(
                            onClick = {
                                isFortuneSlipPreviewVisible = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("share_prediction_button"),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD2B9FF),
                                contentColor = Color(0xFF30205E)
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isBurmese) "ဟောကိန်း မျှဝေမည်" else "Share Prediction",
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Subtle CTA or Submitting Indicator for Sealing Reading on Solana
                        if (txSignature.isNullOrBlank()) {
                            if (isSubmittingTransaction || attestationStatusText != null) {
                                val statusDisplay = attestationStatusText ?: (
                                    if (isBurmese) "Solana Devnet သို့ ပေးပို့နေပါသည်... 🔮" else "Sealing fate on Solana Devnet... 🔮"
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF231E2E))
                                        .border(1.dp, Color(0xFF14F195), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                        .testTag("solana_submitting_indicator"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color(0xFF14F195),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = statusDisplay,
                                            color = Color(0xFF14F195),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF1E1B2E))
                                        .border(1.dp, MysticGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(bounded = true, color = MysticGoldBright),
                                            onClick = onSealOnSolana
                                        )
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                        .testTag("seal_on_solana_cta"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isBurmese) "Solana ပေါ်တွင် ဟောကိန်း သိမ်းဆည်းလိုပါသလား။ ဝေါလက် ချိတ်ဆက်ပါ။"
                                               else "Want to seal this reading on Solana? Connect Wallet.",
                                        color = MysticGoldBright,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Phase 7A: Optional Oracle Offering UI (Web3 Mode Only)
                        if (isWeb3Mode) {
                            when {
                                offeringConfirmed -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 8.dp,
                                                shape = RoundedCornerShape(20.dp),
                                                ambientColor = Color(0xFF14F195).copy(alpha = 0.15f),
                                                spotColor = Color(0xFF14F195).copy(alpha = 0.25f)
                                            )
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFF1B382B))
                                            .border(1.dp, Color(0xFF14F195), RoundedCornerShape(20.dp))
                                            .padding(16.dp)
                                            .testTag("oracle_offering_confirmed_card")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = Color(0xFF14F195),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (isBurmese) "✨ Oracle လှူဒါန်းမှု အတည်ပြုပြီးပါပြီ" else "✨ Oracle Offering Confirmed",
                                                    color = Color(0xFF14F195),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            if (!offeringTxSignature.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                OutlinedButton(
                                                    onClick = {
                                                        val explorerUrl = SolanaWalletGatekeeper.buildExplorerUrl(offeringTxSignature, isTx = true)
                                                        try {
                                                            uriHandler.openUri(explorerUrl)
                                                        } catch (_: Exception) {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(explorerUrl))
                                                            context.startActivity(intent)
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(44.dp)
                                                        .testTag("view_offering_explorer_button"),
                                                    shape = RoundedCornerShape(22.dp),
                                                    border = BorderStroke(1.dp, Color(0xFF14F195)),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = Color(0xFF14F195)
                                                    )
                                                ) {
                                                    Text(
                                                        text = if (isBurmese) "Solana Devnet တွင် လှူဒါန်းမှု ကြည့်ရှုမည် 🔗" else "View Offering on Solana Devnet 🔗",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                offeringUnknownConfirmation -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFF332B1E))
                                            .border(1.dp, Color(0xFFFFD85A), RoundedCornerShape(20.dp))
                                            .padding(16.dp)
                                            .testTag("oracle_offering_unknown_card")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = if (isBurmese) "လှူဒါန်းမှု ပေးပို့ပြီးပါပြီ။ Devnet အတည်ပြုချက် ကြန့်ကြာနေပါသည်။"
                                                       else "Offering submitted, but Devnet confirmation is taking longer than expected.",
                                                color = Color(0xFFFFD85A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                            if (!offeringTxSignature.isNullOrBlank()) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                OutlinedButton(
                                                    onClick = {
                                                        val explorerUrl = SolanaWalletGatekeeper.buildExplorerUrl(offeringTxSignature, isTx = true)
                                                        try {
                                                            uriHandler.openUri(explorerUrl)
                                                        } catch (_: Exception) {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(explorerUrl))
                                                            context.startActivity(intent)
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(44.dp)
                                                        .testTag("view_offering_explorer_button"),
                                                    shape = RoundedCornerShape(22.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFFFD85A)),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = Color(0xFFFFD85A)
                                                    )
                                                ) {
                                                    Text(
                                                        text = if (isBurmese) "Solana Devnet တွင် လှူဒါန်းမှု ကြည့်ရှုမည် 🔗" else "View Offering on Solana Devnet 🔗",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                isOfferingInFlight -> {
                                    OutlinedButton(
                                        onClick = { },
                                        enabled = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("oracle_offering_button_submitting"),
                                        shape = RoundedCornerShape(27.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFD85A).copy(alpha = 0.5f)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            disabledContainerColor = Color(0xFF2A263D),
                                            disabledContentColor = Color(0xFFFFD85A)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 24.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = Color(0xFFFFD85A),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = offeringStatusText ?: if (isBurmese) "လုပ်ဆောင်နေပါသည်..." else "Processing Offering...",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    OutlinedButton(
                                        onClick = onMakeOffering,
                                        enabled = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(54.dp)
                                            .testTag("oracle_offering_button"),
                                        shape = RoundedCornerShape(27.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFD85A)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color(0xFF2A263D),
                                            contentColor = Color(0xFFFFD85A)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 24.dp)
                                    ) {
                                        Text(
                                            text = if (isBurmese) "🪙 Devnet SOL 0.001 ကို Oracle ထံ လှူဒါန်းမည်" else "🪙 Offer 0.001 Devnet SOL to the Oracle",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // View On-Chain Proof (Solana Devnet Explorer) Button (Only shown in Web3 mode when confirmed and valid txSignature exists)
                        if (isWeb3Mode && sealingState == SealingState.CONFIRMED && !txSignature.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = openExplorer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("solana_explorer_button"),
                                shape = RoundedCornerShape(27.dp),
                                border = BorderStroke(
                                    1.5.dp,
                                    Color(0xFF14F195).copy(alpha = 0.9f)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF14F195)
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF14F195),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isBurmese) "Solana Devnet သက်သေကြည့်မည် 🔗" else "View Solana Devnet Proof 🔗",
                                        fontSize = 16.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Ask Another Button (Clears selection & returns to Select Question)
                        OutlinedButton(
                            onClick = onTryAgain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("ask_another_button"),
                            shape = RoundedCornerShape(27.dp),
                            border = BorderStroke(
                                1.5.dp,
                                Color(0xFFFFD85A).copy(alpha = 0.9f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFFD85A)
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD85A),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isBurmese) "နောက်ထပ် မေးမြန်းမည်" else "Ask Another",
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 3. Celebratory Canvas-based Confetti Effect
            ConfettiCelebrationCanvas()
        }

        // 4. Fortune Slip Card Preview Overlay Dialog
        if (isFortuneSlipPreviewVisible) {
            val slipGraphicsLayer = rememberGraphicsLayer()
            val fortuneSlipData = remember(selectedQuestionId, finalChoice, sealingState, txTimestamp, isBurmese) {
                val questionText = if (isBurmese) question?.mm ?: "" else question?.en ?: ""
                val categoryEnum = QuestionCategoryMapper.getCategoryForQuestion(selectedQuestionId ?: "")
                val categoryText = categoryEnum.getLabel(isBurmese)
                val timeMs = txTimestamp ?: System.currentTimeMillis()
                val formatter = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                val displayDate = formatter.format(Date(timeMs))

                FortuneSlipCardMapper.fromDetails(
                    questionText = questionText,
                    answerText = localizedAnswer,
                    oracleNumber = finalChoice,
                    category = categoryText,
                    displayDate = displayDate,
                    sealingState = sealingState,
                    appName = if (isBurmese) "မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်" else "MTK Fortune Teller"
                )
            }

            Dialog(
                onDismissRequest = { isFortuneSlipPreviewVisible = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                BackHandler {
                    isFortuneSlipPreviewVisible = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 440.dp)
                            .verticalScroll(rememberScrollState())
                            .testTag("fortune_slip_preview_dialog"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawWithContent {
                                    slipGraphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(slipGraphicsLayer)
                                }
                                .testTag("fortune_slip_card")
                        ) {
                            FortuneSlipCard(data = fortuneSlipData)
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            val imageBitmap = slipGraphicsLayer.toImageBitmap()
                                            val androidBitmap = imageBitmap.asAndroidBitmap()
                                            val explorerUrl = if (fortuneSlipData.status == FortuneSlipStatus.VERIFIED && !txSignature.isNullOrBlank()) {
                                                SolanaWalletGatekeeper.buildExplorerUrl(txSignature, isTx = true)
                                            } else null

                                            FortuneSlipShareHelper.shareFortuneSlip(
                                                context = context,
                                                data = fortuneSlipData,
                                                bitmap = androidBitmap,
                                                explorerUrl = explorerUrl
                                            )
                                        } catch (e: Exception) {
                                            Log.e("ResultScreen", "Failed to capture FortuneSlipCard bitmap, using text fallback", e)
                                            val explorerUrl = if (fortuneSlipData.status == FortuneSlipStatus.VERIFIED && !txSignature.isNullOrBlank()) {
                                                SolanaWalletGatekeeper.buildExplorerUrl(txSignature, isTx = true)
                                            } else null
                                            val shareText = FortuneSlipShareHelper.buildShareText(fortuneSlipData, explorerUrl)
                                            val title = FortuneSlipShareHelper.buildShareTitle(fortuneSlipData)
                                            sharePredictionTextOnly(context, title, shareText)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("share_fortune_slip_button"),
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD2B9FF),
                                    contentColor = Color(0xFF30205E)
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isBurmese) "လက်မှတ် မျှဝေမည်" else "Share Fortune Slip",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { isFortuneSlipPreviewVisible = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("close_fortune_slip_preview_button"),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = if (isBurmese) "ပိတ်မည်" else "Close",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun shortenWallet(wallet: String?): String {
    if (wallet.isNullOrBlank()) return "-"
    val clean = wallet.trim()
    return if (clean.length >= 8) {
        "${clean.take(4)}...${clean.takeLast(4)}"
    } else {
        clean
    }
}

private fun shortenTxSignature(txSig: String?): String {
    if (txSig.isNullOrBlank()) return "-"
    val clean = txSig.trim()
    return if (clean.length >= 12) {
        "${clean.take(6)}...${clean.takeLast(6)}"
    } else {
        clean
    }
}


