package com.wordco.clockworkandroid.database.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    var startTime: Long,
    var duration: Long?,
    val type: Int
)