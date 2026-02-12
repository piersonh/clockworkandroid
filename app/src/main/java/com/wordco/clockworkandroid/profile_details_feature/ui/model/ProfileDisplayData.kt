package com.wordco.clockworkandroid.profile_details_feature.ui.model

import androidx.compose.ui.graphics.Color

data class ProfileDisplayData(
    val name: String,
    val color: Color,
    val todoSessions: List<TodoSessionListItem>,
    val completeSessions: List<CompletedSessionListItem>,
)
