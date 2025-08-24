package com.wordco.clockworkandroid.ui

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.domain.model.ExecutionStatus

data class ActiveTaskListItem(
    val taskId: Long,
    val name: String,
    val status: ExecutionStatus,
    val color: Color,
    val elapsedWorkSeconds: Int,
    val elapsedBreakMinutes: Int,
)
