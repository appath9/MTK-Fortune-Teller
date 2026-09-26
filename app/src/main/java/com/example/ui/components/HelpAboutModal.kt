package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.ui.theme.MysticBackground
import com.example.ui.theme.MysticBorderMuted
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSecondaryViolet
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticSurfaceBorder
import com.example.ui.theme.MysticSurfaceVariant
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary
import com.example.ui.theme.MysticTextTertiary

@Composable
fun HelpAboutModal(
    currentLang: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBurmese = currentLang == "mm"
    val privacyPolicyUrl = "https://sites.google.com/view/all-time-high-ft/mtk_web3app_privacy_policy"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(MysticSurface)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(MysticGoldBright, MysticPrimaryPurple, MysticSecondaryViolet)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
                .testTag("help_about_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MysticDeepVioletBadge)
                                .border(1.dp, MysticGold.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔮", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBurmese) "အကူအညီနှင့် မိတ်ဆက်" else "Help & About",
                                color = MysticGoldBright,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "App Version ${BuildConfig.VERSION_NAME}",
                                color = MysticTextTertiary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MysticSurfaceVariant)
                            .testTag("close_help_modal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MysticTextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MysticSurfaceBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // About Numerology Method Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MysticBackground),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(MysticSurfaceBorder, MysticSurfaceBorder)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MysticPrimaryPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBurmese) "မြန်မာ့ဂဏန်းဗေဒင် ဟောကိန်းနည်းလမ်း" else "Myanmar Numerology Method",
                                color = MysticPrimaryPurple,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBurmese) {
                                "၇ ရက် သားသမီးများ မိတ်ဆွေသူငယ်ချင်းများ အတွက် ဆရာကြီး မင်းသိင်္ခ၏ မရမ်းတလင်း လက်ထောက်ဗေဒင်ကျမ်းကြီးကို Application ပြုလုပ်ထားခြင်းဖြစ်ပါသည် (‌‌‌‌လောကီ အကျိုးအမြတ်အတွက် မရည်ရွယ်ပါ)။\nအိမ်ထောင်ရေး၊ စီးပွားရေး၊ ကျန်းမာရေး၊ လူမှုရေး၊ ပညာရေး ဆိုင်ရာ မေးခွန်းများအတွက် ဟောကိန်း၊ အဖြေများနှင့်အတူ ဆောင်ရန်၊ ရှောင်ရန်များ နှင့် ပြုလုပ်ရန် ယတြာများ ပါဝင်ပါသည်။"
                            } else {
                                "MTK Fortune Teller is inspired by the traditional and ancient numerology and fortune-telling traditions passed down through the teachings of mystic masters to their disciples.\n\nIt offers traditional-style readings based on the questions you are interested in or seeking guidance on, using the Myanmar numerology method and presented through an interactive digital Oracle experience.\n\nThe readings are provided for entertainment and cultural exploration only. They are not professional, medical, financial, legal, or other expert advice."
                            },
                            color = MysticTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Information details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MysticBackground)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBurmese) "ဗားရှင်း" else "Version",
                            color = MysticTextTertiary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${BuildConfig.VERSION_NAME} (Production)",
                            color = MysticTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBurmese) "ရေးသားသူ" else "Developer",
                            color = MysticTextTertiary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "All_Time_High",
                            color = MysticTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Privacy Policy Link (CRITICAL Requirement)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MysticPrimaryPurple.copy(alpha = 0.1f))
                        .clickable {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("privacy_policy_link"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Policy,
                            contentDescription = null,
                            tint = MysticPrimaryPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBurmese) "ကိုယ်ရေးအချက်အလက် မူဝါဒ (Privacy Policy)" else "Privacy Policy",
                            color = MysticPrimaryPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open",
                        tint = MysticPrimaryPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // OK / Got It button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("dismiss_help_modal_button"),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MysticPrimaryPurple,
                        contentColor = Color(0xFF1C1B1F)
                    )
                ) {
                    Text(
                        text = if (isBurmese) "နားလည်ပါပြီ" else "Got It",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private val MysticDeepVioletBadge = Color(0xFF1F0B3D)
