package com.wordco.clockworkandroid.edit_session_feature.ui.util

import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun MutableStateFlow<SessionFormUiState>.updateIfRetrieved(
    function: (SessionFormUiState.Retrieved) -> SessionFormUiState
) {
    getIfType<SessionFormUiState.Retrieved>()?.let { currentValue ->
        update {function(currentValue)}
    } ?: error ("Can only update if retrieved")
}