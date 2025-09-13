package com.wordco.clockworkandroid.edit_profile_feature.ui.util

import com.wordco.clockworkandroid.edit_profile_feature.ui.EditProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate

fun MutableStateFlow<EditProfileUiState>.updateRetrieved(
    function: (EditProfileUiState.Retrieved) -> EditProfileUiState.Retrieved
) {
    getAndUpdate {
        when (it) {
            is EditProfileUiState.Retrieved -> function(it)
            else -> error("Cannot update state flow when not retrieved")
        }
    }
}