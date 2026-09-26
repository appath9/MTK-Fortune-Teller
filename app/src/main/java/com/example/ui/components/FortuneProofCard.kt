package com.example.ui.components

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.model.AccessTier
import com.example.ui.theme.*
import com.example.util.SolanaWalletGatekeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun FortuneProofCard(
    isBurmese: Boolean,
    accessTier: AccessTier,
    isWeb3Mode: Boolean,
    freeReadingsRemaining: Int,
    displayResultDigit: String,
    questionText: String,
    localizedAnswer: String,
    txSignature: String?,
    graphicsLayer: GraphicsLayer,
    modifier: Modifier = Modifier
) {
    val burmeseDigits = listOf("၀", "၁", "၂", "၃", "၄", "၅", "၆", "၇", "၈", "၉")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = MysticPrimaryPurple.copy(alpha = 0.25f),
                    spotColor = MysticPrimaryPurple.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF25232A))
                .border(
                    2.dp,
                    Color(0xFFD2B9FF).copy(alpha = 0.92f),
                    RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Badge Header
                if (accessTier == AccessTier.SEEKER_ELITE) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFF14F195).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF14F195).copy(alpha = 0.8f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBurmese) "🛡️ Seeker Elite အထူးအဖွဲ့ဝင်" else "🛡️ Seeker Elite",
                                color = Color(0xFF14F195),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                } else if (!isWeb3Mode) {
                    val freeRemainingDisplay = if (isBurmese) burmeseDigits.getOrElse(freeReadingsRemaining) { freeReadingsRemaining.toString() } else freeReadingsRemaining.toString()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MysticPrimaryPurple.copy(alpha = 0.15f))
                            .border(1.dp, MysticPrimaryPurple.copy(alpha = 0.6f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBurmese) "🔮 အခမဲ့ ဟောကိန်း • $freeRemainingDisplay/၂ ယနေ့ ကျန်ရှိသည်" else "🔮 Free Reading • $freeReadingsRemaining/2 remaining today",
                                color = MysticGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(MysticPrimaryPurple.copy(alpha = 0.15f))
                            .border(1.dp, MysticPrimaryPurple.copy(alpha = 0.6f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MysticGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBurmese) "ဗေဒင် ဟောကိန်း အဖြေ" else "DIVINE PREDICTION",
                                color = MysticGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selected Number Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isBurmese) "ရလဒ် အမှတ်စဉ် ($displayResultDigit) 🔮" else "Result No. $displayResultDigit 🔮",
                        color = MysticPrimaryPurple,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1A20).copy(alpha = 0.64f))
                        .padding(22.dp)
                ) {
                    Column {
                        Text(
                            text = if (isBurmese) "မေးမြန်းခဲ့သော မေးခွန်း" else "QUESTION",
                            fontSize = 16.sp,
                            lineHeight = 21.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFD85A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = questionText,
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.Normal,
                            color = MysticTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Answer Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1B1A20).copy(alpha = 0.64f))
                        .padding(22.dp)
                ) {
                    Column {
                        Text(
                            text = if (isBurmese) "ဗေဒင် ဟောကိန်းအဖြေ" else "ORACLE INTERPRETATION",
                            fontSize = 16.sp,
                            lineHeight = 21.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFFD85A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = localizedAnswer,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Normal,
                            color = MysticTextPrimary
                        )
                    }
                }

                // Optional On-Chain Devnet Proof inside Card
                if (!txSignature.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF14F195).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF14F195).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isBurmese) "⚡ Solana Devnet အတည်ပြုချက် သက်သေ" else "⚡ Solana Devnet On-Chain Proof",
                                color = Color(0xFF14F195),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = SolanaWalletGatekeeper.buildExplorerUrl(txSignature, isTx = true),
                                color = Color(0xFF14F195).copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

fun captureAndShareFortuneCard(
    context: Context,
    coroutineScope: CoroutineScope,
    graphicsLayer: GraphicsLayer,
    shareTitle: String,
    shareBody: String
) {
    coroutineScope.launch {
        try {
            val imageBitmap = graphicsLayer.toImageBitmap()
            val contentUri = withContext(Dispatchers.IO) {
                val androidBitmap = imageBitmap.asAndroidBitmap()
                val cacheFile = File(context.cacheDir, "oracle_proof.png")
                FileOutputStream(cacheFile).use { out ->
                    androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    cacheFile
                )
            }
            sharePredictionWithImage(context, shareTitle, shareBody, contentUri)
        } catch (e: Exception) {
            Log.e("FortuneProofCard", "Failed to capture visual share card, falling back to text share", e)
            sharePredictionTextOnly(context, shareTitle, shareBody)
        }
    }
}

fun sharePredictionWithImage(
    context: Context,
    title: String,
    content: String,
    imageUri: Uri
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "image/png"
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
        putExtra(Intent.EXTRA_STREAM, imageUri)
        clipData = ClipData.newRawUri("oracle_proof", imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val shareIntent = Intent.createChooser(sendIntent, title).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(shareIntent)
}

fun sharePredictionTextOnly(context: Context, title: String, content: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, title)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    context.startActivity(shareIntent)
}
