package com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.TodoSessionListItem
import java.time.Duration

fun Task.toTodoSessionListItem() : TodoSessionListItem {
    if (profileId == null) {
        error("Session must have a profileId to be a ProfileSession")
    }

    return TodoSessionListItem(
        id = taskId,
        name = name,
        dueDate = dueDate,
        difficulty = difficulty,
        color = color,
        userEstimate = userEstimate,
        appEstimate = Duration.ZERO,
    )
}