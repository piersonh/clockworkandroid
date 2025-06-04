package com.wordco.clockworkandroid.data.local.entities

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.data.local.ColorConverter
import com.wordco.clockworkandroid.data.local.TimestampConverter
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import java.time.Instant

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    @TypeConverters(TimestampConverter::class) val dueDate: Instant?,
    val difficulty: Int,
    @TypeConverters(ColorConverter::class) val color: Color,
    @TypeConverters(TimestampConverter::class) val status: ExecutionStatus
)