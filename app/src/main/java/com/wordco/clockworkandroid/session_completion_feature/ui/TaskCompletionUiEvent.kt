package com.wordco.clockworkandroid.session_completion_feature.ui

sealed interface TaskCompletionUiEvent {
    data object NavigateBack : TaskCompletionUiEvent
}