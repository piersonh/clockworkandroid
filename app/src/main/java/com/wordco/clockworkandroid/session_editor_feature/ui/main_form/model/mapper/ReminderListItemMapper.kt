package com.wordco.clockworkandroid.session_editor_feature.ui.main_form.model.mapper

import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem
import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft

fun ReminderDraft.toReminderListItem(): ReminderListItem {
    return ReminderListItem(
        scheduledDate = scheduledTime.toLocalDate(),
        scheduledTime = scheduledTime.toLocalTime(),
    )
}