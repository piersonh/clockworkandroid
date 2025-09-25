package com.wordco.clockworkandroid.user_stats_feature.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class CompletedSessionListItem(
    val taskId: Long,
    val name: String,
    val color: Color,
    val workTime: Duration,
    val breakTime: Duration,
    val completedAt: Instant,
)