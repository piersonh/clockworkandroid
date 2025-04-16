package com.wordco.clockworkandroid.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant
import java.time.Duration


@Entity(
    tableName = "segment",
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )]
)
abstract class Segment(startTime: Instant, duration: Duration?) {
    @PrimaryKey(autoGenerate = true) val segmentId: Long = 0
        private set
    val taskId: Long,
    var startTime: Instant = startTime
        private set
    @TypeConverters(DurationConverter::class)
    var duration: Duration? = duration
        private set

    fun setEnd(endTime: Instant) {
        duration = Duration.between(startTime, endTime)
    }
}


interface DefaultInstance<out T> {
    fun new(): T
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