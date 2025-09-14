package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.TodoSessionListItem

sealed interface ProfileSessionListUiState {
    data object Retrieving : ProfileSessionListUiState

    data class Retrieved(
        val profileName: String,
        val profileColor: Color,
        val todoSessions: List<TodoSessionListItem>,
        val completeSessions: List<TodoSessionListItem>,
    ) : ProfileSessionListUiState
}