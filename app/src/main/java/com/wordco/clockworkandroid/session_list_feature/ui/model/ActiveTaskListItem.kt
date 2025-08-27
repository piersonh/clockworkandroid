package com.wordco.clockworkandroid.session_list_feature.ui.model

import androidx.compose.ui.graphics.Color

data class ActiveTaskListItem(
    val taskId: Long,
    val name: String,
    val status: Status,
    val color: Color,
    val elapsedWorkSeconds: Int,
    val elapsedBreakMinutes: Int,
) {
    enum class Status {
        RUNNING, PAUSED
    }
}