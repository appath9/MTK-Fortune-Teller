package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.room.FortuneRecordEntity
import com.example.model.AccessTier
import com.example.ui.components.FortuneProofCard
import com.example.ui.components.captureAndShareFortuneCard
import com.example.ui.theme.*
import com.example.util.SolanaWalletGatekeeper
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SealedHistoryScreen(
    currentLang: String,
    sealedRecords: List<FortuneRecordEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val isBurmese = currentLang == "mm"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("sealed_history_screen")
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MysticSurface)
                    .border(1.dp, MysticBorderMuted, CircleShape)
                    .testTag("history_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MysticPrimaryPurple,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBurmese) "သိမ်းဆည်းထားသော ဟောကိန်းများ" else "My Sealed Readings",
                    color = MysticTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBurmese) "Solana Devnet မှတ်တမ်း တိုက်ခန်း" else "On-Chain Attestation Vault",
                    color = MysticTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (sealedRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .testTag("history_empty_state"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MysticSurface)
                            .border(1.dp, MysticBorderMuted, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔮", fontSize = 28.sp)
                    }
                    Text(
                        text = if (isBurmese) "သိမ်းဆည်းထားသော ဟောကိန်း မရှိသေးပါ" else "No sealed fortunes yet",
                        color = MysticTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isBurmese) "Solana Devnet တွင် ဟောကိန်းကို သိမ်းဆည်း၍ ကံကြမ္မာကို အခိုင်အမာ တည်ဆောက်ပါ"
                               else "Seal a reading on Solana Devnet to anchor your destiny on-chain",
                        color = MysticTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("history_list"),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sealedRecords, key = { it.attestationId }) { record ->
                    SealedRecordCard(
                        record = record,
                        isBurmese = isBurmese,
                        coroutineScope = coroutineScope,
                        onViewExplorer = {
                            val explorerUrl = SolanaWalletGatekeeper.buildExplorerUrl(record.txSignature, isTx = true)
                            try {
                                uriHandler.openUri(explorerUrl)
                            } catch (_: Exception) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(explorerUrl))
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SealedRecordCard(
    record: FortuneRecordEntity,
    isBurmese: Boolean,
    coroutineScope: CoroutineScope,
    onViewExplorer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    val formattedDate = remember(record.timestamp) { dateFormatter.format(Date(record.timestamp)) }
    val truncatedSig = remember(record.txSignature) {
        val clean = record.txSignature.trim()
        if (clean.length >= 12) "${clean.take(6)}...${clean.takeLast(6)}" else clean
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Off-screen FortuneProofCard with proper width (380.dp) for graphicsLayer capture,
        // using zero-size layout bounds so it does not inflate item height in LazyColumn.
        Box(
            modifier = Modifier
                .width(380.dp)
                .wrapContentHeight()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(0, 0) {
                        placeable.place(-2000, 0)
                    }
                }
        ) {
            FortuneProofCard(
                isBurmese = isBurmese,
                accessTier = AccessTier.PAID_ACCESS,
                isWeb3Mode = true,
                freeReadingsRemaining = 0,
                displayResultDigit = record.chosenNumber.toString(),
                questionText = record.questionText,
                localizedAnswer = record.answerText,
                txSignature = record.txSignature,
                graphicsLayer = graphicsLayer
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MysticSurface)
                .border(1.dp, MysticGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(12.dp)
                .testTag("history_card_${record.attestationId}")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedDate,
                        color = MysticTextSecondary,
                        fontSize = 11.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MysticPrimaryPurple.copy(alpha = 0.2f))
                            .border(1.dp, MysticPrimaryPurple.copy(alpha = 0.6f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "№ ${record.chosenNumber}",
                            color = MysticGoldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = record.questionText,
                    color = MysticTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = record.answerText,
                    color = MysticTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MysticBorderMuted.copy(alpha = 0.5f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "ID: ${record.attestationId}",
                        color = MysticTextSecondary.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Sig: $truncatedSig • Solana Devnet ⚡",
                        color = Color(0xFF14F195).copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewExplorer,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("history_explorer_button"),
                        shape = RoundedCornerShape(19.dp),
                        border = BorderStroke(1.dp, MysticGold.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MysticGoldBright
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Launch,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBurmese) "Explorer တွင်ကြည့်မည်" else "View on Explorer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val shareTitle = if (isBurmese) "ဗေဒင် ဟောကိန်း သက်သေခံချက်" else "Sealed Fortune Proof"
                            val explorerUrl = if (record.txSignature.isNotBlank()) {
                                SolanaWalletGatekeeper.buildExplorerUrl(record.txSignature, isTx = true)
                            } else {
                                "https://explorer.solana.com/?cluster=devnet"
                            }
                            val shareBody = buildString {
                                appendLine("🔮 The Solana Oracle has spoken! 🌌")
                                appendLine()
                                appendLine("❓ I asked: ${record.questionText}")
                                appendLine("🎯 Oracle Reveal: ${record.chosenNumber}")
                                appendLine("📜 My Fate (Prediction): ${record.answerText}")
                                appendLine()
                                appendLine("⛓️ My destiny is forever sealed on the Solana Blockchain. 🛡️")
                                appendLine("🔗 $explorerUrl")
                                appendLine()
                                append("✨ Try your luck here: https://play.google.com/store/apps/details?id=${context.packageName}")
                            }
                            captureAndShareFortuneCard(context, coroutineScope, graphicsLayer, shareTitle, shareBody)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("history_share_button"),
                        shape = RoundedCornerShape(19.dp),
                        border = BorderStroke(1.dp, MysticPrimaryPurple.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MysticTextPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBurmese) "သက်သေခံ မျှဝေမည်" else "Share Proof",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
