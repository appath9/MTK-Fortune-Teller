package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.model.AppScreen
import com.example.ui.theme.MysticBackground
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
fun SideMenuDrawer(
    isOpen: Boolean,
    currentScreen: AppScreen,
    currentLang: String,
    onClose: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onOpenHelpModal: () -> Unit,
    isMockSeekerElite: Boolean = false,
    onToggleMockSeekerElite: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBurmese = currentLang == "mm"

    // Overlay & Animated Sliding Drawer (300ms transition)
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        // 60% black background overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
                .testTag("side_menu_overlay")
        )
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(durationMillis = 300)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        // 288px wide drawer with #2B2930 dark surface
        Surface(
            modifier = modifier
                .width(288.dp)
                .fillMaxHeight()
                .testTag("side_menu_drawer"),
            color = Color(0xFF2B2930) // Dark surface #2B2930
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Header with Crystal Ball icon & close button aligned with 28.dp padding
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 28.dp, end = 28.dp, top = 28.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Subtle glowing Crystal Ball icon
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    MysticPrimaryPurple.copy(alpha = 0.35f),
                                                    Color(0xFF1F0B3D)
                                                )
                                            )
                                        )
                                        .border(1.5.dp, MysticGold.copy(alpha = 0.8f), CircleShape)
                                        .testTag("crystal_ball_header_icon"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🔮",
                                        fontSize = 20.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (isBurmese) "မရမ်းတလင်း လက်ထောက်ဗေဒင်" else "MTK Fortune Teller",
                                        color = MysticGoldBright,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MysticSurfaceVariant)
                                    .testTag("close_drawer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Menu",
                                    tint = MysticTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = Color.White.copy(alpha = 0.12f)
                        )
                    }

                    // Menu Items aligned to 28.dp inset
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                    ) {
                        // 1. Home Item
                        DrawerMenuItem(
                            icon = Icons.Default.Home,
                            title = if (isBurmese) "ပင်မစာမျက်နှာ" else "Home",
                            isActive = currentScreen == AppScreen.Home,
                            onClick = {
                                onClose()
                                onNavigateHome()
                            },
                            testTag = "drawer_item_home"
                        )

                        // 2. My Sealed Readings Item
                        DrawerMenuItem(
                            icon = Icons.Default.History,
                            title = if (isBurmese) "သိမ်းဆည်းထားသော ဟောကိန်းများ" else "My Sealed Readings",
                            isActive = currentScreen == AppScreen.History,
                            onClick = {
                                onClose()
                                onNavigateToHistory()
                            },
                            testTag = "drawer_item_history"
                        )

                        // 3. Share App Item
                        DrawerMenuItem(
                            icon = Icons.Default.Share,
                            title = if (isBurmese) "အက်ပ်အား မျှဝေမည်" else "Share App",
                            isActive = false,
                            onClick = {
                                onClose()
                                shareApp(context, isBurmese)
                            },
                            testTag = "drawer_item_share"
                        )

                        // 3. Feedback Item
                        DrawerMenuItem(
                            icon = Icons.Default.Mail,
                            title = if (isBurmese) "အကြံပြုချက် ပေးပို့မည်" else "Feedback",
                            isActive = false,
                            onClick = {
                                onClose()
                                sendFeedbackEmail(context)
                            },
                            testTag = "drawer_item_feedback"
                        )

                        // 4. Help & About Item
                        DrawerMenuItem(
                            icon = Icons.Default.Info,
                            title = if (isBurmese) "အကူအညီနှင့် မိတ်ဆက်" else "Help & About",
                            isActive = false,
                            onClick = {
                                onClose()
                                onOpenHelpModal()
                            },
                            testTag = "drawer_item_help_about"
                        )

                        // 5. Rate & Review Item
                        DrawerMenuItem(
                            icon = Icons.Default.Star,
                            title = if (isBurmese) "သုံးသပ်ချက်ပေးမည်" else "Rate & Review",
                            isActive = false,
                            onClick = {
                                onClose()
                                openPlayStore(context)
                            },
                            testTag = "drawer_item_rate"
                        )

                        if (BuildConfig.DEBUG) {
                            DrawerMenuItem(
                                icon = Icons.Default.Star,
                                title = "🧪 [DEBUG] Mock Seeker Elite",
                                subtitle = if (isMockSeekerElite) "STATUS: ACTIVE (Unlimited Readings)" else "STATUS: OFF (Tap to test A52)",
                                isActive = isMockSeekerElite,
                                onClick = {
                                    onToggleMockSeekerElite()
                                },
                                testTag = "debug_mock_seeker_toggle"
                            )
                        }
                    }
                }

                // Refined Version Footer
                Text(
                    text = if (isBurmese) "မရမ်းတလင်း လက်ထောက်ဗေဒင် v6.0.1" else "MTK Fortune Teller v6.0.1",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 0.3.sp,
                    color = Color(0xFFD0CBD7).copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val activeBackgroundColor = Color(0xFFB79AF4).copy(alpha = 0.18f)
    val activeBorderColor = Color(0xFFD2B9FF).copy(alpha = 0.72f)
    val iconColor = if (isActive) Color(0xFFD2B9FF) else Color(0xFFFFD85A)
    val titleColor = if (isActive) Color(0xFFD2B9FF) else Color(0xFFF5F2F8)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isActive) {
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(activeBackgroundColor)
                        .border(1.dp, activeBorderColor, RoundedCornerShape(16.dp))
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                } else {
                    Modifier
                        .padding(vertical = 10.dp)
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color(0xFFD2B9FF)),
                onClick = onClick
            )
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) Color(0xFFB79AF4).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.04f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFD0CBD7),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun shareApp(context: Context, isBurmese: Boolean) {
    val storeUrl = "https://play.google.com/store/apps/details?id=com.ATH.mtkweb3"
    val shareText = """
        🔮 MTK Fortune Teller

        Explore ancient wisdom with a modern Web3 experience. Ask your question, reveal your reading, and optionally seal it on Solana as an on-chain Oracle attestation.

        Try MTK Fortune Teller:
        $storeUrl
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    try {
        val shareIntent = Intent.createChooser(sendIntent, if (isBurmese) "အက်ပ်အား မျှဝေရန်" else "Share via")
        context.startActivity(shareIntent)
    } catch (_: Exception) {
        // Fallback: Copy link to clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(if (isBurmese) "မရမ်းတလင်း လက်ထောက်ဗေဒင် လင့်ခ်" else "MTK Fortune Teller Link", storeUrl)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(
            context,
            if (isBurmese) "လင့်ခ်အား ကူးယူပြီးပါပြီ" else "Link copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun sendFeedbackEmail(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:nmyint1999@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "MTK Fortune Teller App Feedback")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Fallback if no email client
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:nmyint1999@gmail.com?subject=Myanmar%20Fortune%20Teller%20Feedback"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}

private fun openPlayStore(context: Context) {
    val targetAppId = "com.ATH.mtkweb3"
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$targetAppId"))
        context.startActivity(intent)
    } catch (_: Exception) {
        // Fallback to browser URL
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$targetAppId"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
