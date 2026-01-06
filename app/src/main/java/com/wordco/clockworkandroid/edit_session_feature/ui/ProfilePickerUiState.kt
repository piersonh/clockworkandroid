package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem

sealed interface ProfilePickerUiState {
    data object Retrieving: ProfilePickerUiState

    data class Retrieved(
        val profiles: List<ProfilePickerItem>,
        val selectedProfileId: Long?,
    ): ProfilePickerUiState

    data class Error(
        val header: String,
        val message: String,
    ) : ProfilePickerUiState
}