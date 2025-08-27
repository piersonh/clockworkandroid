package com.wordco.clockworkandroid.core.data.local.entities

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
data class MarkerEntity (
    @PrimaryKey(autoGenerate = true) val markerId: Long = 0,
    val taskId: Long,
    // TODO change the type converters to a mapper where the entity classes use
    //  database native types
    var startTime: Long,
    var label: String
)