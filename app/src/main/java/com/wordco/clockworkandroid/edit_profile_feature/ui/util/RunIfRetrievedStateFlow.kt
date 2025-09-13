package com.wordco.clockworkandroid.edit_profile_feature.ui.util

import com.wordco.clockworkandroid.edit_profile_feature.ui.EditProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<EditProfileUiState>.runIfRetrieved(
    function: EditProfileUiState.Retrieved.() -> T
) : T {
    value.let {
        when(it) {
            is EditProfileUiState.Retrieved -> return function(it)
            else -> error("Cannot run if not retrieved")
        }
    }
}