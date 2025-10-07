package com.wordco.clockworkandroid.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun Int.dpScaledWith(sp: TextUnit): Dp {
    return this.times(sp.getScaling()).dp
}

@Composable
fun TextUnit.getScaling(): Float {
    val density = LocalDensity.current

    val finalPixelValue = with(density) { this@getScaling.toPx() }
    val basePixelValue = with(density) { this@getScaling.value * density.density }

    return finalPixelValue / basePixelValue
}