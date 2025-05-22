package com.wordco.clockworkandroid.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wordco.clockworkandroid.data.local.ColorConverter
import com.wordco.clockworkandroid.data.local.TimestampConverter
import java.time.Instant

@Entity(tableName = "task_properties")
data class TaskProperties(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @TypeConverters(TimestampConverter::class) val dueDate: Instant?,
    val difficulty: Int,
    @TypeConverters(ColorConverter::class) val color: Color,
    @TypeConverters(TimestampConverter::class) val status: Task.Status
)