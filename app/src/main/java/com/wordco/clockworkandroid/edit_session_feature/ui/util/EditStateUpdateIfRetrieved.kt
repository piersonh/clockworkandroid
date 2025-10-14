package com.wordco.clockworkandroid.edit_session_feature.ui.util

import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.edit_session_feature.ui.EditTaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableStateFlow<EditTaskUiState>.updateIfRetrieved(
    function: (EditTaskUiState.Retrieved) -> EditTaskUiState
) {
    getIfType<EditTaskUiState.Retrieved>()?.let { currentValue ->
        update {function(currentValue)}
    } ?: error ("Can only update if retrieved")
}


