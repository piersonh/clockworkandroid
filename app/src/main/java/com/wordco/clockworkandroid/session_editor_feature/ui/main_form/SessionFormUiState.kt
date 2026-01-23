package com.wordco.clockworkandroid.session_editor_feature.ui.main_form

import com.wordco.clockworkandroid.session_editor_feature.domain.model.DueDateTime
import com.wordco.clockworkandroid.session_editor_feature.domain.model.UserEstimate
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.model.SessionFormModal
import com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.model.ReminderListItem
import java.time.Duration


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
        val dueDateTime: DueDateTime?,
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