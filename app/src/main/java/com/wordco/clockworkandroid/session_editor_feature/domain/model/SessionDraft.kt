package com.wordco.clockworkandroid.session_editor_feature.domain.model

import java.time.LocalDateTime

/**
 * Represents the interim values of a Session in the editor before the it is committed to storage
 *
 * Use a SessionDraftFactory to create a valid instance
 *
 * @property sessionId
 * @property sessionName
 * @property profileId
 * @property colorHue Between 0 and 1
 * @property difficulty Between0 and 4
 * @property dueDateTime
 * @property estimate
 */
data class SessionDraft(
    val sessionId: Long,
    val sessionName: String,
    val profileId: Long?,
    val colorHue: Float,
    val difficulty: Int,
    val dueDateTime: LocalDateTime?,
    val estimate: UserEstimate?,
)