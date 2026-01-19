package com.wordco.clockworkandroid.session_editor_feature.domain.model

sealed interface DraftValidationError {
    data object EmptyName : DraftValidationError
    data object EditorNotLoaded : DraftValidationError
}