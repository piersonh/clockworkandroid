package com.wordco.clockworkandroid.session_list_feature.ui

sealed interface TodoListUiEffect {
    data class ShowSnackbar(val message: String) : TodoListUiEffect
    data class CopyToClipboard(val content: String) : TodoListUiEffect
    data object NavigateToCreateSession : TodoListUiEffect
    data class NavigateToSessionDetails(val id: Long) : TodoListUiEffect
}