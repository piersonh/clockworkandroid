package com.wordco.clockworkandroid.session_editor_feature.ui.main_form

sealed interface SessionFormUiEffect {
    data class ShowSnackbar(val message: String) : SessionFormUiEffect
    data object NavigateBack : SessionFormUiEffect
    data object NavigateToProfilePicker : SessionFormUiEffect
    data class CopyToClipboard(val content: String) : SessionFormUiEffect
}