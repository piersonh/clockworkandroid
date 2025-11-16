package com.wordco.clockworkandroid.edit_session_feature.ui.model

import java.time.LocalDate
import java.time.LocalTime

data class ReminderListItem(
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime,
)
