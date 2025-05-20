package com.wordco.clockworkandroid.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.model.database.TimestampConverter
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
    @PrimaryKey(autoGenerate = true) val segmentId: Int = 0,
    val taskId: Int,
    @TypeConverters(TimestampConverter::class) var startTime: Instant,
    var label: String
)