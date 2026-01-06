package com.wordco.clockworkandroid.edit_session_feature.ui

sealed interface ProfilePickerUiEvent {
    sealed interface LoadingEvent: ProfilePickerUiEvent
    sealed interface ErrorEvent: ProfilePickerUiEvent
    sealed interface PickerEvent: ProfilePickerUiEvent

    data object BackClicked : PickerEvent, LoadingEvent, ErrorEvent
    data object CopyErrorClicked: ErrorEvent
    data class ProfileClicked(val id: Long?): PickerEvent
    data object CreateProfileClicked: PickerEvent
}