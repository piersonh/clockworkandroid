package com.wordco.clockworkandroid.profile_list_feature.ui


sealed interface ProfileListUiEvent {
    sealed interface LoadingEvent : ProfileListUiEvent
    sealed interface ErrorEvent : ProfileListUiEvent
    sealed interface ListEvent : ProfileListUiEvent
    data object CreateProfileClicked : ListEvent
    data class ProfileClicked(val id: Long) : ListEvent
    data object CopyErrorClicked : ErrorEvent
}