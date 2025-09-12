package com.wordco.clockworkandroid.session_completion_feature.ui

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate


sealed interface TaskCompletionUiState {
    data object Retrieving : TaskCompletionUiState

    //FIXME
    data class Retrieved (
        val name: String,
        val dueDate: LocalDate?,
        val difficulty: Float,
        val color: Color,
        val estimate: UserEstimate?,
        val segments: List<Segment>,
        val markers: List<Marker>,
    ) : TaskCompletionUiState
}