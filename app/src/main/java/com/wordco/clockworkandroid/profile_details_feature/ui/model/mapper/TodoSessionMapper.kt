package com.wordco.clockworkandroid.profile_details_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.profile_details_feature.ui.model.TodoSessionListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toAppEstimateUiItem

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
        appEstimate = appEstimate?.toAppEstimateUiItem(),
    )
}