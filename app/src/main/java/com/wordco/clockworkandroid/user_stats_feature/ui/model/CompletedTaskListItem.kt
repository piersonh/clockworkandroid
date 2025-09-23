package com.wordco.clockworkandroid.user_stats_feature.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Duration

data class CompletedTaskListItem(
    val taskId: Long,
    val name: String,
    val color: Color,
    val workTime: Duration,
    val breakTime: Duration
)