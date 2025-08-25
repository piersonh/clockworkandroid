package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.ActiveTaskListItem

fun Task.toActiveTaskItem(
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