package com.wordco.clockworkandroid.edit_profile_feature.ui


sealed interface ProfileFormUiEffect {
    data class ShowSnackbar(val message: String) : ProfileFormUiEffect
    data object NavigateBack : ProfileFormUiEffect
    data class CopyToClipboard(val content: String): ProfileFormUiEffect
}