package com.wordco.clockworkandroid.edit_profile_feature.ui.util

import com.wordco.clockworkandroid.edit_profile_feature.ui.ProfileFormUiState
import kotlinx.coroutines.flow.StateFlow

inline fun <T> StateFlow<ProfileFormUiState>.withRetrieved(
    block: ProfileFormUiState.Retrieved.() -> T
) {
    val state = value
    if (state is ProfileFormUiState.Retrieved) {
        block(state)
    }
}