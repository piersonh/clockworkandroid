package com.wordco.clockworkandroid.session_editor_feature.coordinator

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft
import com.wordco.clockworkandroid.session_editor_feature.domain.model.SessionDraft

sealed interface SessionEditorState {
    data class Error(
        val alert: String,
        val error: Exception,
    ) : SessionEditorState

    data object Retrieving : SessionEditorState

    data class Retrieved(
        val draft: SessionDraft,
        val activeProfile: Profile?,
        val reminders: List<ReminderDraft>,
        val isEstimateEditable: Boolean,
        val hasUnsavedChanges: Boolean,
    ) : SessionEditorState
}