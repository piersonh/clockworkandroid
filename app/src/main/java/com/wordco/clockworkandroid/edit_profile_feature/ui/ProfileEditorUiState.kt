package com.wordco.clockworkandroid.edit_profile_feature.ui

import com.wordco.clockworkandroid.edit_profile_feature.ui.model.ProfileEditorModal

sealed interface ProfileEditorUiState {
    val title: String

    data class Retrieving(
        override val title: String,
    ): ProfileEditorUiState

    data class Retrieved(
        override val title: String,
        val name: String,
        val colorSliderPos: Float,
        val difficulty: Float,
        val hasFormChanges: Boolean,
        val currentModal: ProfileEditorModal?
    ): ProfileEditorUiState

    data class Error(
        override val title: String,
        val header: String,
        val message: String,
    ) : ProfileEditorUiState
}