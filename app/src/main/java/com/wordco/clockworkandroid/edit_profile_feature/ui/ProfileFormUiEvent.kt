package com.wordco.clockworkandroid.edit_profile_feature.ui

sealed interface ProfileFormUiEvent {
    data object BackClicked: ProfileFormUiEvent
    data object DiscardConfirmed : ProfileFormUiEvent
    data object ModalDismissed: ProfileFormUiEvent
    data object SaveClicked: ProfileFormUiEvent

    data class NameChanged(val newName: String): ProfileFormUiEvent
    data class ColorSliderChanged(val newValue: Float): ProfileFormUiEvent
    data class DifficultySliderChanged(val newValue: Float): ProfileFormUiEvent
}