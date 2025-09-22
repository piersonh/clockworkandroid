package com.wordco.clockworkandroid.database.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileEntity(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val name: String,
    val difficulty: Int,
    val color: Int,
)