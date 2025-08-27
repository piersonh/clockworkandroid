package com.wordco.clockworkandroid.session_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem

fun StartedTask.toActiveTaskItem(
    elapsedWorkSeconds: Int,
    elapsedBreakMinutes: Int
): ActiveTaskListItem {
    return ActiveTaskListItem(
        name = name,
        elapsedWorkSeconds = elapsedWorkSeconds,
        elapsedBreakMinutes = elapsedBreakMinutes,
        taskId = taskId,
        status = status(),
        color = color,
    )
}