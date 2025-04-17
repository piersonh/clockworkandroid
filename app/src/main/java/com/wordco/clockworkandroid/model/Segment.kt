package com.wordco.clockworkandroid.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.model.database.DurationConverter
import com.wordco.clockworkandroid.model.database.TimestampConverter
import java.time.Duration
import java.time.Instant


@Entity(
    tableName = "segment", foreignKeys = [ForeignKey(
        entity = TaskProperties::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Segment(
    @PrimaryKey(autoGenerate = true) val segmentId: Long = 0,
    val taskId: Long,
    @TypeConverters(TimestampConverter::class) var startTime: Instant,
    @TypeConverters(DurationConverter::class) var duration: Duration?
) {

    fun setEnd(endTime: Instant) {
        duration = Duration.between(startTime, endTime)
    }
}