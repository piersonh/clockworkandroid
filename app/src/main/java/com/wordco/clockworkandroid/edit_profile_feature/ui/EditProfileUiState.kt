package com.wordco.clockworkandroid.edit_profile_feature.ui

import com.wordco.clockworkandroid.edit_profile_feature.ui.model.EditPageModal

sealed interface EditProfileUiState {
    data object Retrieving : EditProfileUiState

    data class Retrieved(
        override val name: String,
        override val colorSliderPos: Float,
        override val difficulty: Float,
        val currentModal: EditPageModal?,
    ) : EditProfileUiState, ProfileFormUiState
}