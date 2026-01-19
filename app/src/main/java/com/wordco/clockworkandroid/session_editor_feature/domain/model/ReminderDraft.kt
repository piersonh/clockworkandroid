package com.wordco.clockworkandroid.session_editor_feature.domain.model

import java.time.LocalDateTime

data class ReminderDraft(
    val id: Long,
    val scheduledTime: LocalDateTime,
)
