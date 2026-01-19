package com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker


sealed interface ProfilePickerUiEffect {
    data class FinishWithResult(val profileId: Long?): ProfilePickerUiEffect
    data object NavigateBack: ProfilePickerUiEffect
    data object NavigateToCreateProfile: ProfilePickerUiEffect
    data class ShowSnackbar(val message: String): ProfilePickerUiEffect
    data class CopyToClipboard(val content: String): ProfilePickerUiEffect
}