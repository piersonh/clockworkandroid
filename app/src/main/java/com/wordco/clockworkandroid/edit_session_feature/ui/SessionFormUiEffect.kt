package com.wordco.clockworkandroid.edit_session_feature.ui

sealed interface SessionFormUiEffect {
    data class ShowSnackbar(val message: String) : SessionFormUiEffect
    data object NavigateBack : SessionFormUiEffect
    data class NavigateToProfilePicker(val currentProfileId: Long?) : SessionFormUiEffect
    data class CopyToClipboard(val content: String) : SessionFormUiEffect
}