package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSecondaryViolet
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary

@Composable
fun LoadingScreen(
    currentLang: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")
    
    // Orb pulse
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    // Outer glow pulse
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    // Rotation for stars
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star_rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                // Expanding outer glowing aura
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MysticPrimaryPurple.copy(alpha = 0.25f),
                                    MysticSecondaryViolet.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rotating constellation / rune ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .border(
                            width = 1.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    MysticGold.copy(alpha = 0.6f),
                                    Color.Transparent,
                                    MysticPrimaryPurple.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Central Crystal Ball
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFEADDFF),
                                    MysticPrimaryPurple,
                                    MysticSecondaryViolet,
                                    Color(0xFF1E0A3C)
                                )
                            )
                        )
                        .border(1.5.dp, MysticGoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔮",
                        fontSize = 42.sp,
                        modifier = Modifier.testTag("crystal_ball_icon")
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = if (currentLang == "mm") "ဂဏန်းဗေဒင် လမ်းညွှန်ချက်များ ရှာဖွေနေပါသည်..." else "Connecting to Numerological Cosmos...",
                color = MysticGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (currentLang == "mm") "စိတ်ကြည်လင်ငြိမ်းချမ်းစွာ စောင့်ဆိုင်းပါ" else "Please center your mind in quiet contemplation",
                color = MysticTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
