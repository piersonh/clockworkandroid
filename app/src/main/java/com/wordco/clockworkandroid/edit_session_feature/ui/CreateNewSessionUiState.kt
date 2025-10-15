package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime

sealed interface CreateNewSessionUiState {
    data object Retrieving : CreateNewSessionUiState

    data class Retrieved (
        val taskName: String,
        val profileName: String?,
        val colorSliderPos: Float,
        val difficulty: Float,
        val dueDate: LocalDate?,
        val dueTime: LocalTime?,
        val estimate: UserEstimate?,
        val profiles: List<ProfilePickerItem>,
        val hasFieldChanges: Boolean,
    ) : CreateNewSessionUiState {
        fun toFormUiState() : SessionFormUiState {
            return SessionFormUiState(
                taskName = taskName,
                profileName = profileName,
                colorSliderPos = colorSliderPos,
                difficulty = difficulty,
                dueDate = dueDate,
                dueTime = dueTime,
                estimate = estimate
            )
        }
    }
}