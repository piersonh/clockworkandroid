package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.CompletedSessionListItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.ProfileDetailsModal
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.TodoSessionListItem

sealed interface ProfileSessionListUiState {
    data object Retrieving : ProfileSessionListUiState

    data class Error(
        val header: String,
        val message: String,
    ) : ProfileSessionListUiState

    data class Retrieved(
        val profileName: String,
        val profileColor: Color,
        val todoSessions: List<TodoSessionListItem>,
        val completeSessions: List<CompletedSessionListItem>,
        val isMenuOpen: Boolean,
        val currentModal: ProfileDetailsModal?
    ) : ProfileSessionListUiState

    data object Deleting : ProfileSessionListUiState
}