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

private val DarkColorScheme = darkColorScheme(
    primary = ModernIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = ModernIndigoDark,
    onPrimaryContainer = Color.White,
    secondary = ModernVioletAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF312E81),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = ModernCyanAccent,
    background = ModernBgDark,
    surface = ModernSurfaceDark,
    surfaceVariant = ModernCardDark,
    onBackground = ModernTextPrimaryDark,
    onSurface = ModernTextPrimaryDark,
    onSurfaceVariant = ModernTextSecondaryDark,
    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = ModernIndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = ModernIndigoDark,
    secondary = ModernVioletAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF5B21B6),
    tertiary = ModernCyanAccent,
    background = ModernBgLight,
    surface = ModernSurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = ModernTextPrimaryLight,
    onSurface = ModernTextPrimaryLight,
    onSurfaceVariant = ModernTextSecondaryLight,
    outline = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
