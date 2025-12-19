package com.wordco.clockworkandroid.edit_profile_feature.ui.util

import com.wordco.clockworkandroid.edit_profile_feature.ui.ProfileFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun MutableStateFlow<ProfileFormUiState>.updateRetrieved(
    function: ProfileFormUiState.Retrieved.() -> ProfileFormUiState.Retrieved
) {
    this.update { state ->
        if (state is ProfileFormUiState.Retrieved) {
            state.function()
        } else {
            state
        }
    }
}