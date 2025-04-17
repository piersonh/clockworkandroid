package com.wordco.clockworkandroid.model

import androidx.compose.animation.VectorConverter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Duration
import java.time.Instant

@Database(entities = [TaskProperties::class, Segment::class], version = 1)
@TypeConverters(TimestampConverter::class, DurationConverter::class, TaskStatusConverter::class,
    ColorConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun taskDao(): SegmentDao
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
    fun fromTaskStatus(status: Status?): Int? {
        return status?.ordinal
    }

    @TypeConverter
    fun toTaskStatus(status: Int?): Status? {
        return status?.let { Status.entries[it] }
    }
}

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