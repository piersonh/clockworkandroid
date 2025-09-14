package com.wordco.clockworkandroid.database.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ProfileEntity::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["profileId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    val profileId: Long?,
    val dueDate: Long?,
    val difficulty: Int,
    val color: Int,
    val status: Int,
    val userEstimate: Long?
)