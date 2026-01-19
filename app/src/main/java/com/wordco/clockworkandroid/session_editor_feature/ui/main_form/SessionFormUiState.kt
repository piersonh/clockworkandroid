package com.wordco.clockworkandroid.session_editor_feature.ui.main_form

import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SessionFormModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


sealed interface SessionFormUiState {
    val title: String

    data class Retrieving(
        override val title: String,
    ) : SessionFormUiState

    data class Retrieved (
        override val title: String,

        val isEstimateEditable: Boolean,
        val averageSessionDuration: Duration?,
        val averageEstimateError: Double?,

        val taskName: String,
        val profileName: String?,
        val colorSliderPos: Float,
        val difficulty: Float,
        val dueDate: LocalDate?,
        val dueTime: LocalTime?,
        val estimate: UserEstimate?,
        val reminder: ReminderListItem?,

        val currentModal: SessionFormModal? = null
    ) : SessionFormUiState

    data class Error(
        override val title: String,
        val header: String,
        val message: String,
    ) : SessionFormUiState
}