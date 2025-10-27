package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.database.data.local.entities.ReminderEntity
import com.wordco.clockworkandroid.database.data.util.fromInstant
import com.wordco.clockworkandroid.database.data.util.fromReminderStatus
import com.wordco.clockworkandroid.database.data.util.toInstant
import com.wordco.clockworkandroid.database.data.util.toReminderStatus

fun ReminderEntity.toReminder() : Reminder {
    return Reminder(
        reminderId = reminderId,
        sessionId = sessionId,
        workRequestId = workRequestId,
        scheduledTime = toInstant(scheduledTime),
        status = toReminderStatus(status)
    )
}

fun Reminder.toReminderEntity() : ReminderEntity {
    return ReminderEntity(
        reminderId = reminderId,
        sessionId = sessionId,
        workRequestId = workRequestId,
        scheduledTime = fromInstant(scheduledTime),
        status = fromReminderStatus(status)
    )
}