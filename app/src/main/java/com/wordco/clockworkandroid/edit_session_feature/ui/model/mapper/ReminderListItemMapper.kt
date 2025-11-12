package com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem
import java.time.ZoneId

fun Reminder.toReminderListItem(): ReminderListItem {
    return scheduledTime.atZone(ZoneId.systemDefault()).run {
        ReminderListItem(
            scheduledDate = toLocalDate(),
            scheduledTime = toLocalTime(),
        )
    }
}