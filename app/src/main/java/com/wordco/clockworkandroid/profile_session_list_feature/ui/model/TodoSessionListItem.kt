package com.wordco.clockworkandroid.profile_session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class TodoSessionListItem(
    val id: Long,
    val name: String,
    val dueDate: Instant?,
    val difficulty: Int,
    val color: Color,
    val userEstimate: Duration?,
    val appEstimate: Duration
)