package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MysticPrimaryPurple,
    onPrimary = MysticDeepViolet,
    primaryContainer = MysticSecondaryViolet,
    onPrimaryContainer = MysticTertiaryLilac,
    secondary = MysticGold,
    onSecondary = Color(0xFF3E2D00),
    secondaryContainer = Color(0xFF5B4300),
    onSecondaryContainer = MysticGoldBright,
    tertiary = MysticTertiaryLilac,
    background = MysticBackground,
    onBackground = MysticTextPrimary,
    surface = MysticSurface,
    onSurface = MysticTextPrimary,
    surfaceVariant = MysticSurfaceVariant,
    onSurfaceVariant = MysticTextSecondary,
    outline = MysticSurfaceBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We enforce the Dark Mystical Theme specified by the project rules
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
