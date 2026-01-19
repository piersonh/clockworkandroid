package com.wordco.clockworkandroid.session_editor_feature.coordinator

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft
import com.wordco.clockworkandroid.session_editor_feature.domain.model.SessionDraft
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class ReminderDraftFactory {
    fun createNew(session: SessionDraft): ReminderDraft {
        return ReminderDraft(
            id = 0,
            scheduledTime = getDefaultScheduledTime(session.dueDateTime)
        )
    }

    fun createFromExisting(reminder: Reminder): ReminderDraft {
        return ReminderDraft(
            id = reminder.reminderId,
            scheduledTime = reminder.scheduledTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
        )
    }

    fun getDefaultScheduledTime(sessionDueDateTime: LocalDateTime?): LocalDateTime {
        return when (sessionDueDateTime) {
            null -> {
                LocalDate.now().plusDays(1).atTime(12,0)
            }
            else -> {
                sessionDueDateTime.minusMinutes(30)
            }
        }
    }
}