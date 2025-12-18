package com.wordco.clockworkandroid.core.ui.theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Color.kt
internal val lightColorPalette = lightColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF3D3D3D),
    primaryContainer = Color(0xFFDADADA),
    onPrimaryContainer = Color(0xFF383838),
    secondary = Color(0xFF7633D9),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC0A9F5),
    onSecondaryContainer = Color(0xFFf6f5ff),
    tertiary = Color(0xFF10B981),
    tertiaryContainer = Color(0xFFD1FAE5),
    surfaceVariant = Color(0xFFf3edf7),
    background = Color(0xFFfef7ff)
)

internal val darkColorPalette = darkColorScheme(
    primary = Color(0xFF2C2C2C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF5D5B5B),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF7633D9),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC0A9F5),
    onSecondaryContainer = Color(0xFFf6f5ff),
    tertiary = Color(0xFF10B981),
    tertiaryContainer = Color(0xFFD1FAE5),
    surfaceVariant = Color(0xFF211f26),
    background = Color(0xFF141218)
)

