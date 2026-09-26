package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.theme.MysticBackground
import com.example.ui.theme.MysticBorderMuted
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSecondaryViolet
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary
import kotlin.math.roundToInt

@Composable
fun GlobalShell(
    currentLang: String,
    currentScreen: AppScreen,
    isDrawerOpen: Boolean,
    isHelpModalOpen: Boolean,
    onOpenDrawer: () -> Unit,
    onCloseDrawer: () -> Unit,
    onOpenHelpModal: () -> Unit,
    onCloseHelpModal: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onToggleLang: () -> Unit,
    onSetLang: (String) -> Unit,
    onOpenGuide: () -> Unit,
    isMockSeekerElite: Boolean = false,
    onToggleMockSeekerElite: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val showHamburger = currentScreen != AppScreen.Loading && currentScreen != AppScreen.Intro

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MysticBackground),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Radial Portal Glow in the background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MysticPrimaryPurple.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Container: max-width 480dp centered on screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp)
        ) {
            // Top App Bar with hamburger menu, guide & language toggle
            TopNavigationBar(
                currentLang = currentLang,
                showHamburger = showHamburger,
                onOpenDrawer = onOpenDrawer,
                onSetLang = onSetLang,
                onOpenGuide = onOpenGuide
            )

            // Main screen content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content()
            }

            // Fixed 24px bottom animated marquee ('သဗ္ဗေသတ္တာ ကမ္မသကာ' in Gold #FFE082, 22s loop)
            BottomMottoMarquee()
        }

        // Side Menu Drawer
        SideMenuDrawer(
            isOpen = isDrawerOpen,
            currentScreen = currentScreen,
            currentLang = currentLang,
            onClose = onCloseDrawer,
            onNavigateHome = onNavigateHome,
            onNavigateToHistory = onNavigateToHistory,
            onOpenHelpModal = onOpenHelpModal,
            isMockSeekerElite = isMockSeekerElite,
            onToggleMockSeekerElite = onToggleMockSeekerElite
        )

        // Help & About Modal with Privacy Policy
        if (isHelpModalOpen) {
            HelpAboutModal(
                currentLang = currentLang,
                onDismiss = onCloseHelpModal
            )
        }
    }
}

@Composable
fun TopNavigationBar(
    currentLang: String,
    showHamburger: Boolean,
    onOpenDrawer: () -> Unit,
    onSetLang: (String) -> Unit,
    onOpenGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left section: Hamburger Icon (☰) & App Identity
        Row(
            modifier = Modifier
                .weight(1f)
                .widthIn(min = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showHamburger) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                        .testTag("hamburger_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Navigation Menu",
                        tint = MysticTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 0.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenGuide() }
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔮",
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = if (currentLang == "mm") "မရမ်းတလင်း လက်ထောက်ဗေဒင်" else "MTK Fortune Teller",
                        color = MysticTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        maxLines = if (currentLang == "mm") 2 else 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }

        // Right section Action controls: Guide button & Language toggle pill
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Guide button
            IconButton(
                onClick = onOpenGuide,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                    .testTag("guide_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = "Guide",
                    tint = MysticTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // LanguageTogglePill hidden for English-only mode
        }
    }
}

@Composable
fun LanguageTogglePill(
    currentLang: String,
    onSelectEn: () -> Unit,
    onSelectMm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBurmese = currentLang == "mm"
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(MysticSurface)
            .border(1.5.dp, MysticGold.copy(alpha = 0.7f), RoundedCornerShape(100.dp))
            .padding(2.dp)
            .testTag("language_toggle_pill"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // English Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (!isBurmese) MysticGold else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = MysticGoldBright),
                        onClick = onSelectEn
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("lang_en_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isBurmese) Color(0xFF1E1528) else MysticTextSecondary
                )
            }

            // Burmese Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (isBurmese) MysticGold else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = MysticGoldBright),
                        onClick = onSelectMm
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("lang_mm_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBurmese) Color(0xFF1E1528) else MysticTextSecondary
                )
            }
        }
    }
}

@Composable
fun BottomMottoMarquee(
    modifier: Modifier = Modifier
) {
    // Fixed 24px (24.dp) bar with one complete Burmese+English segment per loop.
    // The segment width is measured so the animation resets only at an identical
    // segment boundary, preventing cut-off phrases at the repeat point.
    val segmentWidthPx = remember { mutableIntStateOf(1) }
    val transition = rememberInfiniteTransition(label = "bottom_ticker_anim")
    val xOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -segmentWidthPx.intValue.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ticker_offset"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color(0xFF1F1D24)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clipToBounds()
                .border(
                    width = 0.5.dp,
                    color = MysticBorderMuted.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(0.dp)
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .testTag("footer_marquee"),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth(unbounded = true)
                    .offset { IntOffset(xOffset.roundToInt(), 0) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Each segment is complete and ordered Burmese -> separator -> English -> separator.
                // Repeating whole segments makes the reset land on the same content boundary.
                repeat(4) { index ->
                    Row(
                        modifier = if (index == 0) {
                            Modifier
                                .wrapContentWidth(unbounded = true)
                                .onGloballyPositioned { coordinates ->
                                segmentWidthPx.intValue = coordinates.size.width.coerceAtLeast(1)
                            }
                        } else {
                            Modifier
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "သဗ္ဗေ သတ္တာ ကမ္မသကာ",
                            color = Color(0xFFFFE082), // Gold #FFE082
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.0.sp
                        )
                        Text(
                            text = "✦",
                            color = MysticPrimaryPurple.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                        Text(
                            text = "All living beings are the owners of their own karma.",
                            color = Color(0xFFFFE082),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.2.sp
                        )
                        Text(
                            text = "•",
                            color = Color(0xFFFFE082).copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
        }
    }
}

