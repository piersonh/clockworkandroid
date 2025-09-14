package com.wordco.clockworkandroid.edit_session_feature.ui.model

sealed interface EditTaskResult {
    data object Success : EditTaskResult
    sealed interface Error : EditTaskResult
    data object MissingName : Error
}