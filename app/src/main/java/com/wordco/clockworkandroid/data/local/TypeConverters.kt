package com.wordco.clockworkandroid.data.local

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.SegmentType
import java.time.Duration
import java.time.Instant

class ColorConverter {
    @TypeConverter
    fun fromColor(color: Color?): Int? {
        return color?.toArgb()
    }

    @TypeConverter
    fun toColor(color: Int?): Color? {
        return color?.let { Color(it) }
    }
}

class DurationConverter {
    @TypeConverter
    fun fromDuration(duration: Duration?): Long? {
        return duration?.toMillis()
    }

    @TypeConverter
    fun toDuration(millis: Long?): Duration? {
        return millis?.let { Duration.ofMillis(it) }
    }
}

class TaskStatusConverter {
    @TypeConverter
    fun fromTaskStatus(status: ExecutionStatus?): Int? {
        return status?.ordinal
    }

    @TypeConverter
    fun toTaskStatus(status: Int?): ExecutionStatus? {
        return status?.let { ExecutionStatus.entries[it] }
    }
}

// FIXME I do not like this
class SegmentTypeConverter {
    companion object {
        fun SegmentType.fromSegmentType(): Int {
            return this.ordinal
        }

        fun Int.toSegmentType(): SegmentType {
            return SegmentType.entries[this]
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