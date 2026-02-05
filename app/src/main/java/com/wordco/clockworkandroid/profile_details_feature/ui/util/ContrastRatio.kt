package com.wordco.clockworkandroid.profile_details_feature.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

fun Color.contrastRatioWith(other: Color) : Float {
    val l1 = max(other.luminance(), luminance())
    val l2 = min(other.luminance(), luminance())

    return ((l1 + 0.05f) / (l2 + 0.05f))
}