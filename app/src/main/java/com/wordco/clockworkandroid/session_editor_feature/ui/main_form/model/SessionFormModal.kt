package com.wordco.clockworkandroid.session_editor_feature.ui.main_form.model

sealed interface SessionFormModal {
    data object DueDate : SessionFormModal
    data object DueTime : SessionFormModal
    data object Estimate : SessionFormModal
    data object ReminderDate : SessionFormModal
    data object ReminderTime : SessionFormModal

    data object Discard : SessionFormModal
}