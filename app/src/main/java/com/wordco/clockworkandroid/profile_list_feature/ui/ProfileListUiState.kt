package com.wordco.clockworkandroid.profile_list_feature.ui

import com.wordco.clockworkandroid.profile_list_feature.ui.model.ProfileListItem

interface ProfileListUiState {
    data object Retrieving : ProfileListUiState

    data class Retrieved(
        val profiles: List<ProfileListItem>
    ) : ProfileListUiState
}