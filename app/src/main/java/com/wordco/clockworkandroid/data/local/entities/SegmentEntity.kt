package com.wordco.clockworkandroid.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.data.local.DurationConverter
import com.wordco.clockworkandroid.data.local.TimestampConverter
import java.time.Duration
import java.time.Instant

@Entity(
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["taskId"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["taskId"])]
)
data class SegmentEntity (
    @PrimaryKey(autoGenerate = true) val segmentId: Long = 0,
    val taskId: Long,
    // TODO change the type converters to a mapper where the entity classes use
    //  database native types
    @TypeConverters(TimestampConverter::class) var startTime: Instant,
    @TypeConverters(DurationConverter::class) var duration: Duration?,
    val type: Int
) {

    fun setEnd(endTime: Instant) {
        duration = Duration.between(startTime, endTime)
    }
}