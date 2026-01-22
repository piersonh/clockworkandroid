package com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.model.mapper

import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft
import com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.model.ReminderListItem

fun ReminderDraft.toReminderListItem(): ReminderListItem {
    return ReminderListItem(
        scheduledDate = scheduledTime.toLocalDate(),
        scheduledTime = scheduledTime.toLocalTime(),
    )
}