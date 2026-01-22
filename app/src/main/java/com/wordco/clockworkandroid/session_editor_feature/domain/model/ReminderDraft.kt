package com.wordco.clockworkandroid.session_editor_feature.domain.model

import java.time.LocalDateTime

/**
 * Represents a single reminder for a session in the editor.
 *
 * Use a ReminderDraftFactory to create a valid instance.
 *
 * @property id
 * @property scheduledTime
 */
data class ReminderDraft(
    val id: Long,
    val scheduledTime: LocalDateTime,
)
