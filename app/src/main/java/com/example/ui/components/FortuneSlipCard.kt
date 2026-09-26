package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.model.FortuneSlipCardData
import com.example.ui.model.FortuneSlipStatus
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary
import com.example.ui.theme.MysticTextTertiary
import com.example.ui.theme.MyApplicationTheme

/**
 * Polished, standalone QR-free Visual Fortune Slip Card component.
 *
 * Renders an immutable [FortuneSlipCardData] presentation model with MTK mystical aesthetics.
 */
@Composable
fun FortuneSlipCard(
    data: FortuneSlipCardData,
    modifier: Modifier = Modifier
) {
    val isBurmese = data.appName.contains("မင်းတုန်းမင်း") || data.question.any { it.code in 0x1000..0x109F }

    Box(
        modifier = modifier
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
            .padding(22.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header & MTK Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MysticGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = data.appName.uppercase(),
                    color = MysticGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            // Title Sub-header
            Text(
                text = if (isBurmese) "✦ ကံဇာတာ ဟောကိန်း လက်မှတ် ✦" else "✦ FORTUNE SLIP ✦",
                color = Color(0xFFD2B9FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.0.sp
            )

            // Category Pill (if present)
            if (!data.category.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(MysticPrimaryPurple.copy(alpha = 0.12f))
                        .border(1.dp, MysticPrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = data.category,
                        color = MysticGoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 2. Question Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1B1A20).copy(alpha = 0.64f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = if (isBurmese) "မေးမြန်းခဲ့သော မေးခွန်း" else "YOUR QUESTION",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD85A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.question,
                        fontSize = 17.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.Normal,
                        color = MysticTextPrimary
                    )
                }
            }

            // 3. Oracle Interpretation Section (Emotional Focal Point)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B1A20).copy(alpha = 0.82f))
                    .border(1.dp, MysticGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = if (isBurmese) "ဗေဒင် ဟောကိန်းအဖြေ" else "ORACLE INTERPRETATION",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD85A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.answer,
                        fontSize = 19.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = MysticTextPrimary
                    )
                }
            }

            // 4. Oracle Choice Number & Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (data.oracleNumber != null) {
                    val burmeseDigits = listOf("၀", "၁", "၂", "၃", "၄", "၅", "၆", "၇", "၈", "၉")
                    val digitDisplay = if (isBurmese) {
                        burmeseDigits.getOrElse(data.oracleNumber) { data.oracleNumber.toString() }
                    } else {
                        data.oracleNumber.toString()
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MysticPrimaryPurple.copy(alpha = 0.15f))
                            .border(1.dp, MysticPrimaryPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBurmese) "အမှတ်စဉ် № $digitDisplay 🔮" else "ORACLE № $digitDisplay 🔮",
                            color = Color(0xFFD2B9FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!data.displayDate.isNullOrBlank()) {
                    Text(
                        text = data.displayDate,
                        color = MysticTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // 5. Presentation Status Badge
            val (statusText, statusColor, statusBg) = when (data.status) {
                FortuneSlipStatus.LOCAL -> Triple(
                    if (isBurmese) "✦ ပြည်တွင်း ဟောကိန်း" else "✦ LOCAL READING",
                    MysticGold,
                    MysticGold.copy(alpha = 0.12f)
                )
                FortuneSlipStatus.VERIFIED -> Triple(
                    if (isBurmese) "🛡️ SOLANA DEVNET အတည်ပြုပြီး" else "🛡️ VERIFIED ON SOLANA DEVNET",
                    Color(0xFF14F195),
                    Color(0xFF14F195).copy(alpha = 0.12f)
                )
                FortuneSlipStatus.PENDING -> Triple(
                    if (isBurmese) "◌ SOLANA အတည်ပြုချက် စောင့်ဆိုင်းဆဲ" else "◌ ON-CHAIN VERIFICATION PENDING",
                    Color(0xFFFFB300),
                    Color(0xFFFFB300).copy(alpha = 0.12f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusBg)
                    .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            // 6. Contextual Footer
            Text(
                text = if (isBurmese) "ဖျော်ဖြေရေးနှင့် ကိုယ်ပိုင်စဉ်းစားဆင်ခြင်ရန်အတွက် ဖြစ်ပါသည်" else "For entertainment & personal reflection",
                color = MysticTextTertiary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ============================================================================
// PREVIEWS
// ============================================================================

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun FortuneSlipCardLocalPreview() {
    MyApplicationTheme {
        FortuneSlipCard(
            data = FortuneSlipCardData(
                appName = "MTK Fortune Teller",
                question = "Will this couple have children?",
                answer = "They will have a gifted son who becomes an outstanding person.",
                oracleNumber = 3,
                category = "Love",
                displayDate = "Nov 15, 2024 • 14:30",
                status = FortuneSlipStatus.LOCAL
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun FortuneSlipCardVerifiedPreview() {
    MyApplicationTheme {
        FortuneSlipCard(
            data = FortuneSlipCardData(
                appName = "MTK Fortune Teller",
                question = "Will I find success in my new career transition?",
                answer = "Great progress will come through patience and dedication over time.",
                oracleNumber = 6,
                category = "Career",
                displayDate = "Jan 20, 2025 • 09:15",
                status = FortuneSlipStatus.VERIFIED
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun FortuneSlipCardPendingPreview() {
    MyApplicationTheme {
        FortuneSlipCard(
            data = FortuneSlipCardData(
                appName = "MTK Fortune Teller",
                question = "Is this the right timing for financial investments?",
                answer = "Exercise prudence and review all aspects before proceeding.",
                oracleNumber = 0,
                category = "Money",
                displayDate = "Feb 01, 2025 • 18:45",
                status = FortuneSlipStatus.PENDING
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun FortuneSlipCardLongContentPreview() {
    MyApplicationTheme {
        FortuneSlipCard(
            data = FortuneSlipCardData(
                appName = "MTK Fortune Teller",
                question = "Should I consider relocating to another country for higher education and career development in the upcoming academic year?",
                answer = "An unexpected opportunity will present itself through an acquaintance from abroad. Taking this step will yield long-term spiritual and material growth, provided you stay true to your values.",
                oracleNumber = 7,
                category = "Personal Growth",
                displayDate = "Feb 10, 2025 • 21:00",
                status = FortuneSlipStatus.VERIFIED
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun FortuneSlipCardBoundaryNinePreview() {
    MyApplicationTheme {
        FortuneSlipCard(
            data = FortuneSlipCardData(
                appName = "MTK Fortune Teller",
                question = "Will this relationship blossom into marriage?",
                answer = "They will have children who bring immense peace and benefits to their family.",
                oracleNumber = 9,
                category = "Love",
                displayDate = "Mar 05, 2025 • 11:20",
                status = FortuneSlipStatus.LOCAL
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun FortuneSlipCardBurmesePreview() {
    MyApplicationTheme {
        FortuneSlipCard(
            data = FortuneSlipCardData(
                appName = "မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်",
                question = "ဇနီးမောင်နှံ၌ သားသမီးရ ကိန်းနှင့်ပတ်သက်သောအဟော။",
                answer = "ဤလောကကိုအကျိုးပြုမည့် သားကောင်းရတနာတစ်ဦး ရရှိပေလိမ့်မည်",
                oracleNumber = 0,
                category = "အချစ်ရေး",
                displayDate = "မတ် ၀၅၊ ၂၀၂၅ • ၁၁:၂၀",
                status = FortuneSlipStatus.VERIFIED
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
