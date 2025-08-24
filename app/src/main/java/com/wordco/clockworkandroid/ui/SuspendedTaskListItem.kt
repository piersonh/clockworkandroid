package com.wordco.clockworkandroid.ui

import androidx.compose.ui.graphics.Color
import java.time.Duration

data class SuspendedTaskListItem(
    val taskId: Long,
    val name: String,
    val color: Color,
    val workTime: Duration,
    val breakTime: Duration
)