package com.wordco.clockworkandroid.timer_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.Second

fun Second.toMinutesInHour() : Int {
    return (this % 3600) / 60
}