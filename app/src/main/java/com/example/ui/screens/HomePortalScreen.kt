package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessTier
import com.example.ui.theme.MysticBackground
import com.example.ui.theme.MysticBorderMuted
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSecondaryViolet
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary

@Composable
fun HomePortalScreen(
    currentLang: String,
    freeReadingsRemaining: Int = 2,
    accessTier: AccessTier = AccessTier.FREE,
    onPortalClick: () -> Unit,
    onOpenGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBurmese = currentLang == "mm"
    var showQuestionHint by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2500)
        showQuestionHint = false
    }
    val infiniteTransition = rememberInfiniteTransition(label = "immersive_portal_anim")

    // Outer ring pulse animation using graphicsLayer scale
    val outerRingScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outer_ring_pulse"
    )

    // Inner glow pulse animation using graphicsLayer scale
    val innerGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "inner_glow_pulse"
    )

    // Pulse for indicator dot
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_portal_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Portal background radial glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MysticPrimaryPurple.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Quota Indicator Badge on Home
            QuotaIndicatorBadge(
                currentLang = currentLang,
                freeReadingsRemaining = freeReadingsRemaining,
                accessTier = accessTier
            )

            // Entire Portal Center Circle
            Box(
                modifier = Modifier
                    .size(310.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = MysticGoldBright),
                        onClick = onPortalClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Outer Circle: GPU accelerated graphicsLayer scaling
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .graphicsLayer {
                            scaleX = outerRingScale
                            scaleY = outerRingScale
                        }
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MysticPrimaryPurple.copy(alpha = 0.25f),
                            shape = CircleShape
                        )
                )

                // Middle Circle: GPU accelerated graphicsLayer scaling
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .graphicsLayer {
                            scaleX = innerGlowScale
                            scaleY = innerGlowScale
                        }
                        .clip(CircleShape)
                        .shadow(
                            elevation = 14.dp,
                            shape = CircleShape,
                            ambientColor = MysticPrimaryPurple.copy(alpha = 0.25f),
                            spotColor = MysticPrimaryPurple.copy(alpha = 0.45f)
                        )
                        .border(
                            width = 1.dp,
                            color = MysticPrimaryPurple.copy(alpha = 0.45f),
                            shape = CircleShape
                        )
                )

                // Central Circle
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .shadow(
                            elevation = 22.dp,
                            shape = CircleShape,
                            ambientColor = MysticGold.copy(alpha = 0.25f),
                            spotColor = MysticGold.copy(alpha = 0.45f)
                        )
                        .clip(CircleShape)
                        .background(MysticSurface)
                        .border(
                            width = 2.dp,
                            color = MysticGold,
                            shape = CircleShape
                        )
                        .padding(20.dp)
                        .testTag("portal_center_circle"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Cosmic Wisdom",
                            tint = MysticGold,
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "NUMEROLOGICAL\nFORTUNE TELLER",
                            color = MysticTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(1.dp)
                                .background(MysticBorderMuted)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBurmese) "မရမ်းတလင်း လက်ထောက်ဗေဒင်" else "MTK Fortune Teller",
                            color = MysticPrimaryPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Temporary onboarding hint
            AnimatedVisibility(
                visible = showQuestionHint,
                enter = fadeIn(animationSpec = tween(180)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 56.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFFFE06A))
                        .border(1.dp, MysticGold, RoundedCornerShape(100.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = MysticSecondaryViolet),
                            onClick = onPortalClick
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .testTag("portal_status_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF29272D).copy(alpha = dotAlpha))
                        )

                        Text(
                            text = if (isBurmese) "မေးခွန်းရွေးရန် ဤနေရာကို နှိပ်ပါ" else "TAP TO SELECT QUESTION",
                            color = Color(0xFF29272D),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tap to open",
                            tint = Color(0xFF29272D),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
