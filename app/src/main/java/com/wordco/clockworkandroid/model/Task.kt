package com.wordco.clockworkandroid.model

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class Status(val status: Int) {
    RUNNING(0),
    SUSPENDED(1),
    SCHEDULED(2)
}

data class Task(
    val name: String,
    val workTime: Int,
    val breakTime: Int,
    val due: Long,
    val difficulty: Int,
    val color: Color,
    val status: Status
)


fun Task.timeAsHHMM (time: Int) : String {
    val hours = time / 3600
    val minutes = (time % 3600) / 60
    return String.format(Locale.getDefault(), "%02d:%02d", hours , minutes)
}

fun Task.returnDueDate() : String {
    if (due == 0.toLong()) {
        return "Not Scheduled"
    }

    val now = System.currentTimeMillis()
    var daylight = 0
    val timezone = TimeZone.getDefault()
    if (timezone.useDaylightTime() && timezone.inDaylightTime(Date(now))) {
        daylight = 3600000
    }
    val dayStart = now - (now % 86400000) - timezone.rawOffset - daylight

    if (due - dayStart in -86400000..0) {
        return "Yesterday " + SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(due)
    }else if (due - dayStart in 0..86400000) {
        return "Today " + SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(due)
    } else if (due - dayStart in 86400001..172800000) {
        return "Tomorrow " + SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(due)
    }
    return SimpleDateFormat("LL/dd/yyyy hh:mm aa", Locale.getDefault()).format(due)
}