package com.wordco.clockworkandroid.edit_profile_feature.ui.model

sealed interface EditProfileResult {
    data object Success : EditProfileResult
    sealed interface Error : EditProfileResult
    data object MissingName : Error
}