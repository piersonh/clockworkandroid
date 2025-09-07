package com.wordco.clockworkandroid.session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class NewTaskListItem(
    val taskId: Long,
    val name: String,
    val dueDate: Instant?,
    val difficulty: Int,
    val color: Color,
    val userEstimate: Duration?,
    val appEstimate: Duration
)