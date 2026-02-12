package com.wordco.clockworkandroid.session_list_feature.ui

sealed interface TodoListUiEvent {
    sealed interface LoadingEvent : TodoListUiEvent
    sealed interface ErrorEvent : TodoListUiEvent
    sealed interface ListEvent : TodoListUiEvent
    data object CreateSessionClicked : ListEvent
    data class SessionClicked(val id: Long) : ListEvent
    data object CopyErrorClicked : ErrorEvent
}