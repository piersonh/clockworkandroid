package com.wordco.clockworkandroid.edit_profile_feature.ui


sealed interface ProfileEditorUiEffect {
    data class ShowSnackbar(val message: String) : ProfileEditorUiEffect
    data object NavigateBack : ProfileEditorUiEffect
    data class CopyToClipboard(val content: String) : ProfileEditorUiEffect
}