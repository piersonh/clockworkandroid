package com.wordco.clockworkandroid.edit_profile_feature.ui.model

sealed interface CreateProfileResult {
    data object Success : CreateProfileResult
    sealed interface Error : CreateProfileResult
    data object MissingName : Error
}