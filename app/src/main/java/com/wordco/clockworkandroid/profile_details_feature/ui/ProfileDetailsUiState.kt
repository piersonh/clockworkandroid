package com.wordco.clockworkandroid.profile_details_feature.ui

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.profile_details_feature.ui.model.CompletedSessionListItem
import com.wordco.clockworkandroid.profile_details_feature.ui.model.ProfileDetailsModal
import com.wordco.clockworkandroid.profile_details_feature.ui.model.TodoSessionListItem

sealed interface ProfileDetailsUiState {
    data object Retrieving : ProfileDetailsUiState

    data class Error(
        val header: String,
        val message: String,
    ) : ProfileDetailsUiState

    data class Retrieved(
        val profileName: String,
        val profileColor: Color,
        val todoSessions: List<TodoSessionListItem>,
        val completeSessions: List<CompletedSessionListItem>,
        val isMenuOpen: Boolean,
        val currentModal: ProfileDetailsModal?
    ) : ProfileDetailsUiState

    data object Deleting : ProfileDetailsUiState
}