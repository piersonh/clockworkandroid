package com.wordco.clockworkandroid.session_report_feature.ui.util

import java.time.Duration

fun Duration.toHourMinuteString(): String {
    val hours = this.toHours()
    val minutes = this.toMinutes() % 60
    return "${hours}h ${minutes}m"
}