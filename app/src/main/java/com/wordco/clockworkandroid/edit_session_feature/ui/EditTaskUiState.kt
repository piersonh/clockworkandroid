package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime

sealed interface EditTaskUiState {
    data object Retrieving : EditTaskUiState

    data class Retrieved (
        override val taskName: String,
        override val colorSliderPos: Float,
        override val difficulty: Float,
        override val dueDate: LocalDate?,
        override val dueTime: LocalTime?,
        override val currentModal: PickerModal?,
        override val estimate: UserEstimate?,
    ) : EditTaskUiState, EditTaskFormUiState
}