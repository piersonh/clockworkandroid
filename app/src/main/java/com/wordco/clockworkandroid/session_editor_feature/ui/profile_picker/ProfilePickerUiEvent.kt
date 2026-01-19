package com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker

sealed interface ProfilePickerUiEvent {
    sealed interface LoadingEvent: ProfilePickerUiEvent
    sealed interface ErrorEvent: ProfilePickerUiEvent
    sealed interface PickerEvent: ProfilePickerUiEvent

    data object BackClicked : PickerEvent, LoadingEvent, ErrorEvent
    data object CopyErrorClicked: ErrorEvent
    data class ProfileClicked(val id: Long?): PickerEvent
    data object CreateProfileClicked: PickerEvent
}