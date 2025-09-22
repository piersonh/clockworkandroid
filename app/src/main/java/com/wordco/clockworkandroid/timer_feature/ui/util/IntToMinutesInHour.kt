package com.wordco.clockworkandroid.timer_feature.ui.util

fun Int.toMinutesInHour() : Int {
    return (this % 3600) / 60
}