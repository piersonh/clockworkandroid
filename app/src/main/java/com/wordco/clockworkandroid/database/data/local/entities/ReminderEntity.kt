package com.wordco.clockworkandroid.database.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["taskId"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["sessionId"])]
)
data class ReminderEntity (
    @PrimaryKey(autoGenerate = true) val reminderId: Long = 0,
    val sessionId: Long,
    val workRequestId: String,
    val scheduledTime: Long,
    val status: Int // "PENDING", "COMPLETED", "EXPIRED"
)