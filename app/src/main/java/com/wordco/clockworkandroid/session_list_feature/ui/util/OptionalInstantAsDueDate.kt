package com.wordco.clockworkandroid.session_list_feature.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val TIME_FORMATER: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
private val DATE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("LL/dd/yyyy hh:mm a")

fun Instant?.asTaskDueFormat(): String {
    if (this == null) {
        return "Not Scheduled"
    }

    val todayStart =
        ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneId.systemDefault())
            .toInstant()
    val tomorrowStart = todayStart.plus(1, ChronoUnit.DAYS)
    val overmorrowStart = todayStart.plus(2, ChronoUnit.DAYS)
    val yesterdayStart = todayStart.minus(1, ChronoUnit.DAYS)


    val zonedDueDate = this.atZone(ZoneId.systemDefault())//ZonedDateTime.ofInstant(this, ZoneId.systemDefault())
    val formattedDueTime = zonedDueDate.format(TIME_FORMATER)

    return when (this) {
        in yesterdayStart..<todayStart -> "Yesterday $formattedDueTime"
        in todayStart..<tomorrowStart -> "Today $formattedDueTime"
        in tomorrowStart..<overmorrowStart -> "Tomorrow $formattedDueTime"
        else -> {
            zonedDueDate.format(DATE_TIME_FORMATTER)
        }
    }
}

