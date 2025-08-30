package com.wordco.clockworkandroid.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    val dueDate: Long?,
    val difficulty: Int,
    val color: Int,
    val status: Int
)