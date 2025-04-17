package com.wordco.clockworkandroid.model

import androidx.compose.ui.graphics.Color
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.model.database.ColorConverter
import com.wordco.clockworkandroid.model.database.TimestampConverter
import java.time.Duration
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
    NOT_STARTED(2),
    COMPLETED(3)
}

data class Task(
    @Embedded val taskProperties: TaskProperties,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val segments: List<Segment>
) {
    val name: String
        get() = taskProperties.name
    val dueDate: Instant?
        get() = taskProperties.dueDate
    val difficulty: Int
        get() = taskProperties.difficulty
    val color: Color
        get() = taskProperties.color
    val status: Status
        get() = taskProperties.status

    val workTime: Duration by lazy { Duration.ofMillis(0) }
    val breakTime: Duration by lazy { Duration.ofMillis(0) }

    constructor(name: String, dueDate: Instant?, difficulty: Int, color: Color) : this(
        TaskProperties(0, name, dueDate, difficulty, color, Status.NOT_STARTED),
        mutableListOf()
    )

    companion object {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("LL/dd/yyyy hh:mm a")

        fun timeAsHHMM(time: Int): String {
            val hours = time / 3600
            val minutes = (time % 3600) / 60
            return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
        }

        fun timeAsHHMM(duration: Duration): String {
            val totalMinutes = duration.toMinutes()
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return String.format("%02d:%02d", hours, minutes)
        }
    }

    fun formatDue(): String {
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
        val formattedDueTime = zonedDueDate.format(timeFormatter)

        return when (dueDate) {
            in yesterdayStart..todayStart -> "Yesterday $formattedDueTime"
            in todayStart..tomorrowStart -> "Today $formattedDueTime"
            in tomorrowStart..overmorrowStart -> "Tomorrow $formattedDueTime"
            else -> {
                zonedDueDate.format(dateTimeFormatter)
            }
        }
    }
}


@Entity(tableName = "task_properties")
data class TaskProperties(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @TypeConverters(TimestampConverter::class) val dueDate: Instant?,
    val difficulty: Int,
    @TypeConverters(ColorConverter::class) val color: Color,
    @TypeConverters(TimestampConverter::class) val status: Status
)

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




