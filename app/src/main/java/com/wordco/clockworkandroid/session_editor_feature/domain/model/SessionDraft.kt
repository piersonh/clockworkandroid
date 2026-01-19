package com.wordco.clockworkandroid.session_editor_feature.domain.model

import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDateTime

data class SessionDraft(
    val sessionId: Long,
    val sessionName: String,
    val profileId: Long?,
    val colorHue: Float, // Between 0 and 360 //TODO: make between 0 and 1
    val difficulty: Int,
    val dueDateTime: LocalDateTime?,
    val estimate: UserEstimate?,
)