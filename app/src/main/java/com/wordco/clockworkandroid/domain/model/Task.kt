package com.wordco.clockworkandroid.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class Task(
    val taskId: Long,
    val name: String,
    val dueDate: Instant?,
    val difficulty: Int,
    val color: Color,
    val status: ExecutionStatus,
    val segments: List<Segment>,
    val markers: List<Marker>
) {

    val workTime: Duration by lazy { Duration.ofMillis(0) }
    val breakTime: Duration by lazy { Duration.ofMillis(0) }
}
