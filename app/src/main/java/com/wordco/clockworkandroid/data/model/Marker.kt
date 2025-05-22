package com.wordco.clockworkandroid.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.data.local.TimestampConverter
import java.time.Instant


@Entity(
    tableName = "marker", foreignKeys = [ForeignKey(
        entity = TaskProperties::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["taskId"])]
)
data class Marker (
    @PrimaryKey(autoGenerate = true) val segmentId: Long = 0,
    val taskId: Long,
    @TypeConverters(TimestampConverter::class) var startTime: Instant,
    var label: String
)