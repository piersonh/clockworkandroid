package com.wordco.clockworkandroid.session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.ExecutionStatus

data class ActiveTaskListItem(
    val taskId: Long,
    val name: String,
    val status: ExecutionStatus,
    val color: Color,
    val elapsedWorkSeconds: Int,
    val elapsedBreakMinutes: Int,
)