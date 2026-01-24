package com.wordco.clockworkandroid.profile_list_feature.ui

sealed interface ProfileListUiEffect {
    data class CopyToClipboard(val content: String) : ProfileListUiEffect
    data class ShowSnackbar(val message: String) : ProfileListUiEffect
    data class NavigateToProfile(val id: Long) : ProfileListUiEffect
    data object NavigateToCreateProfile : ProfileListUiEffect
}