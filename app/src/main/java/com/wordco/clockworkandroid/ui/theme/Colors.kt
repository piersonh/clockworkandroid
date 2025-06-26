package com.wordco.clockworkandroid.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Color.kt
private val lightColorPalette = lightColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF3D3D3D),
    primaryContainer = Color(0xFFDADADA),
    onPrimaryContainer = Color(0xFF383838),
    secondary = Color(0xFF7633D9),
    onSecondary = Color(0xFFFFFFFF),
)

private val darkColorPalette = darkColorScheme(
    primary = Color(0xFF2C2C2C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF5D5B5B),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF7633D9),
    onSecondary = Color(0xFFFFFFFF),
)

@Composable
fun ClockworkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorPalette
    } else {
        lightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}