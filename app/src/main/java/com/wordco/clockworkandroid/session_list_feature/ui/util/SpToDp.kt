package com.wordco.clockworkandroid.session_list_feature.ui.util

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

fun TextUnit.toDp(density: Density) : Dp {
    return with(density) {
        this@toDp.toDp()
    }
}