package com.wordco.clockworkandroid.edit_profile_feature.ui


sealed interface EditProfileUiState {
    data object Retrieving : EditProfileUiState

    data class Retrieved(
        override val name: String,
        override val colorSliderPos: Float,
        override val difficulty: Float,
        val hasFieldChanges: Boolean,
    ) : EditProfileUiState, ProfileFormUiState
}