package com.wordco.clockworkandroid.profile_details_feature.ui

sealed interface ProfileDetailsUiEffect {
    data object NavigateBack : ProfileDetailsUiEffect
    data class ShowSnackbar(val message: String) : ProfileDetailsUiEffect
    data class CopyToClipboard(val content: String) : ProfileDetailsUiEffect
    data object NavigateToCreateSession : ProfileDetailsUiEffect
    data object NavigateToProfileEditor : ProfileDetailsUiEffect
    data class NavigateToTodoSession(val id: Long) : ProfileDetailsUiEffect
    data class NavigateToCompletedSession(val id: Long) : ProfileDetailsUiEffect
}