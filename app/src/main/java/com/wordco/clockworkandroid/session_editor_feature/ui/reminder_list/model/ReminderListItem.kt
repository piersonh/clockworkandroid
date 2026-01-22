package com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.model

import java.time.LocalDate
import java.time.LocalTime

data class ReminderListItem(
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime,
)