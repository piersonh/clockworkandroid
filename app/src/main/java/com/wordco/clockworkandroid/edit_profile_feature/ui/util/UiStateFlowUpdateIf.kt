package com.wordco.clockworkandroid.edit_profile_feature.ui.util

import com.wordco.clockworkandroid.edit_profile_feature.ui.ProfileFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun <reified T : ProfileFormUiState> MutableStateFlow<ProfileFormUiState>.updateIf(
    function: T.() -> T
) {
    this.update { state ->
        if (state is T) {
            state.function()
        } else {
            state
        }
    }
}


private fun oo () {
    val oa = MutableStateFlow<ProfileFormUiState>(ProfileFormUiState.Retrieving("to"))

    oa.updateIf<ProfileFormUiState.Retrieving> {
        copy()
    }
}