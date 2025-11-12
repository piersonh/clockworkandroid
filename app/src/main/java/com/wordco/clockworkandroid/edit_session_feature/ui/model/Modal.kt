package com.wordco.clockworkandroid.edit_session_feature.ui.model


sealed interface Modal {
    data object DueDate : Modal
    data object DueTime : Modal
    data object Estimate : Modal
    data object ReminderDate : Modal
    data object ReminderTime : Modal

    data object Discard : Modal
}