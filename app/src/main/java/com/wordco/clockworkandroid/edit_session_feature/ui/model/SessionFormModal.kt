package com.wordco.clockworkandroid.edit_session_feature.ui.model


sealed interface SessionFormModal {
    data object DueDate : SessionFormModal
    data object DueTime : SessionFormModal
    data object Estimate : SessionFormModal
    data object ReminderDate : SessionFormModal
    data object ReminderTime : SessionFormModal

    data object Discard : SessionFormModal
}