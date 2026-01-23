package com.wordco.clockworkandroid.edit_profile_feature.ui

sealed interface ProfileEditorUiEvent {
    sealed interface LoadingEvent: ProfileEditorUiEvent
    sealed interface ErrorEvent: ProfileEditorUiEvent
    sealed interface FormEvent: ProfileEditorUiEvent


    data object BackClicked: ErrorEvent, LoadingEvent, FormEvent
    data object DiscardConfirmed : FormEvent
    data object ModalDismissed: FormEvent
    data object SaveClicked: FormEvent

    data object CopyErrorClicked: ErrorEvent

    data class NameChanged(val newName: String): FormEvent
    data class ColorSliderChanged(val newPos: Float): FormEvent
    data class DifficultySliderChanged(val newPos: Float): FormEvent
}