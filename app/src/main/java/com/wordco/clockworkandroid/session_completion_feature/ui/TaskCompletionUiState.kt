package com.wordco.clockworkandroid.session_completion_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.Duration


sealed interface TaskCompletionUiState {
    data object Retrieving : TaskCompletionUiState

    data class Retrieved (
        val name: String,
        val estimate: UserEstimate?,
        val workTime: Duration,
        val breakTime: Duration,
        val totalTime: Duration
    ) : TaskCompletionUiState
}