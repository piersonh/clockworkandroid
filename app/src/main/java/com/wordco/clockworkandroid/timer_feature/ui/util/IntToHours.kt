package com.wordco.clockworkandroid.timer_feature.ui.util

import com.wordco.clockworkandroid.core.ui.timer.Second

fun Second.toHours(): Int {
    return this / 3600
}