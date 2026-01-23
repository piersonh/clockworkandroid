package com.wordco.clockworkandroid.session_completion_feature.ui

sealed interface TaskCompletionUiEffect {
    data object NavigateBack : TaskCompletionUiEffect
    data object NavigateToEditSession : TaskCompletionUiEffect
    data object NavigateToContinue : TaskCompletionUiEffect
    data class CopyToClipboard(val content: String): TaskCompletionUiEffect
    data class ShowSnackbar(val message: String) : TaskCompletionUiEffect
}