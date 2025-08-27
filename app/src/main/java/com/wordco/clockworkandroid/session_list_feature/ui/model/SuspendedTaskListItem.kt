package com.wordco.clockworkandroid.session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Duration

data class SuspendedTaskListItem(
    val taskId: Long,
    val name: String,
    val color: Color,
    val workTime: Duration,
    val breakTime: Duration
)