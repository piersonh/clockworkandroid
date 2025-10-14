package com.wordco.clockworkandroid.timer_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.Second

fun Second.toHours(): Int {
    return this / 3600
}