package com.wordco.clockworkandroid.session_editor_feature.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class DueDateTime(
    val date: LocalDate,
    val time: LocalTime,
)