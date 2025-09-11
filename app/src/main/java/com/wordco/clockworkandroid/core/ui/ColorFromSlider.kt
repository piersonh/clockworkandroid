package com.wordco.clockworkandroid.core.ui

import androidx.compose.ui.graphics.Color

fun Color.Companion.fromSlider(sliderPos: Float) : Color {
    return Color.hsv(
        sliderPos * 360,
        1f,
        1f
    )
}