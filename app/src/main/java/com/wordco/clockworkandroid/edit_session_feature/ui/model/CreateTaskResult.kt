package com.wordco.clockworkandroid.edit_session_feature.ui.model

sealed interface CreateTaskResult {
    data object Success : CreateTaskResult
    sealed interface Error : CreateTaskResult
    data object MissingName : Error
}