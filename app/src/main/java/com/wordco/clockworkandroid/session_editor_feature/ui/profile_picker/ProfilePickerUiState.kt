package com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker

import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.model.ProfilePickerItem


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