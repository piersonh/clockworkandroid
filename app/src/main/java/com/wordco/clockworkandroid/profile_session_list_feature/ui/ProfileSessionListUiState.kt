package com.wordco.clockworkandroid.profile_session_list_feature.ui

import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.ProfileSessionListItem

sealed interface ProfileSessionListUiState {
    data object Retrieving : ProfileSessionListUiState

    data class Retrieved(
        val profileName: String,
        val sessions: List<ProfileSessionListItem>
    ) : ProfileSessionListUiState
}