package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

interface SessionFormUiState {
    val title: String

    data class Retrieving(
        override val title: String,
    ) : SessionFormUiState

    data class Retrieved (
        override val title: String,
        val initialPage: Int,
        val profiles: List<ProfilePickerItem>,
        val taskName: String,
        val profileName: String?,
        val colorSliderPos: Float,
        val difficulty: Float,
        val dueDate: LocalDate?,
        val dueTime: LocalTime?,
        val estimate: UserEstimate?,
        val isEstimateEditable: Boolean,
        val hasFieldChanges: Boolean,
        val averageSessionDuration: Duration?,
        val averageEstimateError: Double?,
        val reminder: ReminderListItem?,
    ) : SessionFormUiState
}