package com.wordco.clockworkandroid.edit_session_feature.ui

import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime

data class SessionFormUiState (
    val taskName: String,
    val profileName: String?,
    val colorSliderPos: Float,
    val difficulty: Float,
    val dueDate: LocalDate?,
    val dueTime: LocalTime?,
    val estimate: UserEstimate?,
)


