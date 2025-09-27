package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.EditPageModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.toOptionalSessionFormModal
import java.time.LocalDate
import java.time.LocalTime

sealed interface EditTaskUiState {
    data object Retrieving : EditTaskUiState

    data class Retrieved (
        val taskName: String,
        val profileName: String?,
        val colorSliderPos: Float,
        val difficulty: Float,
        val dueDate: LocalDate?,
        val dueTime: LocalTime?,
        val currentModal: EditPageModal?,
        val estimate: UserEstimate?,
        val profiles: List<ProfilePickerItem>
    ) : EditTaskUiState {
        fun toFormUiState() : SessionFormUiState {
            return SessionFormUiState(
                taskName = taskName,
                profileName = profileName,
                colorSliderPos = colorSliderPos,
                difficulty = difficulty,
                dueDate = dueDate,
                dueTime = dueTime,
                currentModal = currentModal?.toOptionalSessionFormModal(),
                estimate = estimate
            )
        }
    }
}