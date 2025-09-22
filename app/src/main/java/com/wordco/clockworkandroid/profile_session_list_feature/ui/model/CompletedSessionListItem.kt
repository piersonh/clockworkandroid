package com.wordco.clockworkandroid.profile_session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Duration

data class CompletedSessionListItem(
    val id: Long,
    val name: String,
    val color: Color,
    val workTime: Duration,
    val breakTime: Duration
)