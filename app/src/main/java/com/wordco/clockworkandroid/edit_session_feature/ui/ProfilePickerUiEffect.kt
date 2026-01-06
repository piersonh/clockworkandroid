package com.wordco.clockworkandroid.edit_session_feature.ui


sealed interface ProfilePickerUiEffect {
    data class FinishWithResult(val profileId: Long?): ProfilePickerUiEffect
    data object NavigateBack: ProfilePickerUiEffect
    data object NavigateToCreateProfile: ProfilePickerUiEffect
    data class ShowSnackbar(val message: String): ProfilePickerUiEffect
    data class CopyToClipboard(val content: String): ProfilePickerUiEffect
}