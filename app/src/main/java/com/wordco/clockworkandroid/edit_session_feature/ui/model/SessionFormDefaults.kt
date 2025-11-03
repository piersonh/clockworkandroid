package com.wordco.clockworkandroid.edit_session_feature.ui.model

import java.time.LocalTime

data class SessionFormDefaults(
    val taskName: String,
    val profileName: String?,
    val colorSliderPos: Float,
    val difficulty: Float,
    val dueTime: LocalTime,
    val estimate: UserEstimate?
)