package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = MintActivePill,
    onPrimaryContainer = ForestGreen,
    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = MintActivePill,
    onSecondaryContainer = ForestGreen,
    background = MintBackground,
    onBackground = Color(0xFF132B23),
    surface = MintCardSurface,
    onSurface = Color(0xFF132B23),
    surfaceVariant = Color(0xFFE4EFE9),
    onSurfaceVariant = Color(0xFF4C6A60),
    outline = MintCardOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = MintBadgeGreen,
    onPrimary = ForestGreenDark,
    primaryContainer = DarkMintActivePill,
    onPrimaryContainer = Color(0xFFD4EFE4),
    secondary = MintBadgeGreen,
    onSecondary = ForestGreenDark,
    secondaryContainer = DarkMintActivePill,
    onSecondaryContainer = Color(0xFFD4EFE4),
    background = DarkForestBackground,
    onBackground = DarkForestText,
    surface = DarkForestCard,
    onSurface = DarkForestText,
    surfaceVariant = DarkForestSurface,
    onSurfaceVariant = Color(0xFFA1BDB3),
    outline = Color(0xFF28443B)
)

@Composable
fun IronWillTheme(
    themeMode: String = "LIGHT",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemInDark
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ExpressiveShapes,
        content = content
    )
}
