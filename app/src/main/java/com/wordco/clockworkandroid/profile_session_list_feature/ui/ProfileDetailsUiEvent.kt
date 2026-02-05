package com.wordco.clockworkandroid.profile_session_list_feature.ui

sealed interface ProfileDetailsUiEvent {
    sealed interface LoadingEvent : ProfileDetailsUiEvent
    sealed interface ErrorEvent : ProfileDetailsUiEvent
    sealed interface DetailsEvent : ProfileDetailsUiEvent
    sealed interface DeletingEvent : ProfileDetailsUiEvent

    data object BackClicked : LoadingEvent, ErrorEvent, DetailsEvent
    data object CopyErrorClicked : ErrorEvent
    data object EditClicked : DetailsEvent
    data object DeleteClicked : DetailsEvent
    data object DeleteConfirmed : DetailsEvent
    data object ModalDismissed : DetailsEvent
    data object CreateSessionClicked : DetailsEvent
    data class TodoSessionClicked(val id: Long) : DetailsEvent
    data class CompletedSessionClicked(val id: Long) : DetailsEvent
    data object MenuOpened : DetailsEvent
    data object MenuClosed : DetailsEvent
}