package com.wordco.clockworkandroid.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

enum class Status(i: Int) {
    RUNNING(0),
    SUSPENDED(1),
    SCHEDULED(2)
}

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dueDate: Instant?,
    val difficulty: Int,
    val color: Color,
    val status: Status
) {
    companion object {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm aa")
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("LL/dd/yyyy hh:mm aa")
    }
}


fun Task.timeAsHHMM (time: Int) : String {
    val hours = time / 3600
    val minutes = (time % 3600) / 60
    return String.format(Locale.getDefault(), "%02d:%02d", hours , minutes)
}

/*
fun Task.returnDueDate() : String {
    if (due == 0.toLong()) {
        return "Not Scheduled"
    }

    val now = System.currentTimeMillis()
    val timezone = TimeZone.getDefault()
    val daylight = if (timezone.useDaylightTime() && timezone.inDaylightTime(Date(now))) {
        3600000
    } else {
        0
    }
    val dayStart = now - (now % 86400000) - timezone.rawOffset - daylight

    return when (due - dayStart) {
        in -86400000..0 -> "Yesterday " + SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(due)
        in 0..86400000 -> "Today " + SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(due)
        in 86400001..172800000 -> "Tomorrow " + SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(due)
        else -> SimpleDateFormat("LL/dd/yyyy hh:mm aa", Locale.getDefault()).format(due)
    }
}
*/

fun Task.printDue() : String {
    if (dueDate == null) {
        return "Not Scheduled"
    }

    val todayStart = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneId.systemDefault()).toInstant()
    val tomorrowStart = todayStart.plus(1, ChronoUnit.DAYS)
    val overmorrowStart = todayStart.plus(2, ChronoUnit.DAYS)
    val yesterdayStart = todayStart.minus(1, ChronoUnit.DAYS)

    val zonedDueDate = ZonedDateTime.ofInstant(dueDate, ZoneId.systemDefault())
    val formattedDueTime = zonedDueDate.format(Task.timeFormatter)

    return when (dueDate) {
        in yesterdayStart..todayStart -> "Yesterday $formattedDueTime"
        in todayStart..tomorrowStart -> "Today $formattedDueTime"
        in tomorrowStart..overmorrowStart -> "Tomorrow $formattedDueTime"
        else -> {
            zonedDueDate.format(Task.dateTimeFormatter)
        }
    }
}


class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Instant?): Long? {
        return date?.toEpochMilli()
    }
}