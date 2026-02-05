package com.wordco.clockworkandroid.profile_details_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.profile_details_feature.ui.model.CompletedSessionListItem

fun CompletedTask.toCompletedSessionListItem() : CompletedSessionListItem {
    return CompletedSessionListItem(
        id = taskId,
        name = name,
        color = color,
        workTime = workTime,
        breakTime = breakTime
    )
}