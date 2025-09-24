package com.wordco.clockworkandroid.user_stats_feature.ui.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("LL/dd/yyyy hh:mm a")

fun Instant.asDateTime() : String {
    return atZone(ZoneId.systemDefault())
        .format(DATE_TIME_FORMATTER)
}