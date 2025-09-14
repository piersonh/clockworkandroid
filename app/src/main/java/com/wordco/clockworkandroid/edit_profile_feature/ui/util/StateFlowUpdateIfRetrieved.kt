package com.wordco.clockworkandroid.edit_profile_feature.ui.util

import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.edit_profile_feature.ui.EditProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableStateFlow<EditProfileUiState>.updateIfRetrieved(
    function: (EditProfileUiState.Retrieved) -> EditProfileUiState.Retrieved
) {
    getIfType<EditProfileUiState.Retrieved>()?.let { currentValue ->
        update {function(currentValue)}
    } ?: error ("Can only update if retrieved")
}