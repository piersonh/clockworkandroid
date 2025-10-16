package com.wordco.clockworkandroid.session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.Second

data class ActiveTaskListItem(
    val taskId: Long,
    val name: String,
    val status: Status,
    val color: Color,
    val elapsedSeconds: Second,
    val currentSegmentElapsedSeconds: Second,
    val progressToEstimate: Float?,
) {
    enum class Status {
        RUNNING, PAUSED
    }
}