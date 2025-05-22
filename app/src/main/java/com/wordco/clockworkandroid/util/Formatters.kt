package com.wordco.clockworkandroid.util

import com.wordco.clockworkandroid.data.model.Task
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val TIME_FORMATER: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
private val DATE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("LL/dd/yyyy hh:mm a")

fun Duration.asHHMM(): String {
    val totalMinutes = this.toMinutes()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes)
}

fun Task.formatDue(): String {
    if (dueDate == null) {
        return "Not Scheduled"
    }

    val todayStart =
        ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneId.systemDefault())
            .toInstant()
    val tomorrowStart = todayStart.plus(1, ChronoUnit.DAYS)
    val overmorrowStart = todayStart.plus(2, ChronoUnit.DAYS)
    val yesterdayStart = todayStart.minus(1, ChronoUnit.DAYS)

    val zonedDueDate = ZonedDateTime.ofInstant(dueDate, ZoneId.systemDefault())
    val formattedDueTime = zonedDueDate.format(TIME_FORMATER)

    return when (dueDate) {
        in yesterdayStart..todayStart -> "Yesterday $formattedDueTime"
        in todayStart..tomorrowStart -> "Today $formattedDueTime"
        in tomorrowStart..overmorrowStart -> "Tomorrow $formattedDueTime"
        else -> {
            zonedDueDate.format(DATE_TIME_FORMATTER)
        }
    }
}

