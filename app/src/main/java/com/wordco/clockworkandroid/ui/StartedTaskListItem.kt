package com.wordco.clockworkandroid.ui

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import java.time.Duration

class StartedTaskListItem(
    val taskId: Long,
    val name: String,
    val status: ExecutionStatus,
    val color: Color,
    val workTime: Duration,
    val breakTime: Duration
) {
}