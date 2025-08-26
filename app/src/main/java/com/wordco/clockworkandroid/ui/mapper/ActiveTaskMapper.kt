package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.StartedTask
import com.wordco.clockworkandroid.ui.ActiveTaskListItem

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