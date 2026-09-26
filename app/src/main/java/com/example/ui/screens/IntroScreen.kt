package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.INTRO_STEPS
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSecondaryViolet
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticSurfaceBorder
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary

@Composable
fun IntroScreen(
    currentStep: Int,
    currentLang: String,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onStepSelect: (Int) -> Unit,
    onAccept: () -> Unit,
    onClose: () -> Unit = onAccept,
    modifier: Modifier = Modifier
) {
    val totalSteps = INTRO_STEPS.size
    val isLastStep = currentStep >= totalSteps - 1
    val safeStep = currentStep.coerceIn(0, totalSteps - 1)
    val isBurmese = currentLang == "mm"

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("intro_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Centered Glass-morphism Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MysticGold.copy(alpha = 0.7f),
                            MysticPrimaryPurple.copy(alpha = 0.5f),
                            MysticSecondaryViolet.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MysticSurface.copy(alpha = 0.94f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with badge and close/skip button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MysticSecondaryViolet,
                                        Color(0xFF4A2A84)
                                    )
                                )
                            )
                            .border(1.dp, MysticGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isBurmese) "ဗေဒင်မေးရာတွင် လိုက်နာရန် စည်းကမ်းများ" else "Sacred Principles",
                            color = MysticGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MysticSurfaceBorder)
                            .testTag("intro_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Guide",
                            tint = MysticTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content area for step body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated step content keyed on (safeStep, isBurmese)
                    AnimatedContent(
                        targetState = Pair(safeStep, isBurmese),
                        transitionSpec = {
                            fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(140))
                        },
                        label = "step_content_anim"
                    ) { (stepIdx, burmeseMode) ->
                        val currentStepData = INTRO_STEPS[stepIdx]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Mystic Icon Circle
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                MysticPrimaryPurple.copy(alpha = 0.35f),
                                                MysticSecondaryViolet,
                                                Color(0xFF1B0B33)
                                            )
                                        )
                                    )
                                    .border(2.dp, MysticGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentStepData.iconEmoji,
                                    fontSize = 36.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Step Title
                            Text(
                                text = if (burmeseMode) currentStepData.titleMm else currentStepData.titleEn,
                                color = MysticGoldBright,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Step Message Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1E1C24))
                                    .border(1.dp, MysticSurfaceBorder, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (burmeseMode) currentStepData.messageMm else currentStepData.messageEn,
                                    color = MysticTextPrimary,
                                    fontSize = 13.5.sp,
                                    lineHeight = 21.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stepper Dots Indicator (1 to 5) - Clickable to jump to any step
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalSteps) { idx ->
                        val isSelected = idx == safeStep
                        val isPassed = idx < safeStep

                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 26.dp else 12.dp, 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        isSelected -> MysticGold
                                        isPassed -> MysticPrimaryPurple
                                        else -> MysticSurfaceBorder
                                    }
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, color = MysticGold),
                                    onClick = { onStepSelect(idx) }
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fixed navigation Controls at bottom of card
                if (isLastStep) {
                    // Step 5: OK (Accepted) Button
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .testTag("ok_accepted_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MysticGold,
                            contentColor = Color(0xFF261900)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF261900),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBurmese) "သဘောတူလက်ခံပါသည် (OK)" else "OK (Enter Portal)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous button
                        if (safeStep > 0) {
                            OutlinedButton(
                                onClick = onPrevStep,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("intro_prev_button"),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MysticSurfaceBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MysticTextSecondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBurmese) "ရှေ့သို့" else "Back",
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Next button
                        Button(
                            onClick = onNextStep,
                            modifier = Modifier
                                .weight(if (safeStep > 0) 1.2f else 1f)
                                .height(46.dp)
                                .testTag("intro_next_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MysticSecondaryViolet,
                                contentColor = MysticGold
                            )
                        ) {
                            Text(
                                text = if (isBurmese) "နောက်တစ်ခု" else "Next Step",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = MysticGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
