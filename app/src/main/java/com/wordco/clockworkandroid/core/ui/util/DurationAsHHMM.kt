package com.wordco.clockworkandroid.core.ui.util

import java.time.Duration
import java.util.Locale


fun Duration.asHHMM(): String {
    val totalMinutes = this.toMinutes()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes)
}

