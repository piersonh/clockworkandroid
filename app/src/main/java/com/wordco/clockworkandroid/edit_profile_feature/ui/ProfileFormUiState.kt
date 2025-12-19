package com.wordco.clockworkandroid.edit_profile_feature.ui

import com.wordco.clockworkandroid.edit_profile_feature.ui.model.ProfileFormModal

sealed interface ProfileFormUiState {
    val title: String

    data class Retrieving(
        override val title: String,
    ): ProfileFormUiState

    data class Retrieved(
        override val title: String,
        val name: String,
        val colorSliderPos: Float,
        val difficulty: Float,
        val hasFormChanges: Boolean,
        val currentModal: ProfileFormModal?
    ): ProfileFormUiState
}