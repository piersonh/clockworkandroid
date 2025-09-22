package com.wordco.clockworkandroid.session_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.session_list_feature.ui.model.NewTaskListItem
import java.time.Duration

fun NewTask.toNewTaskListItem() : NewTaskListItem {
    return NewTaskListItem(
        taskId = taskId,
        name = name,
        dueDate = dueDate,
        difficulty = difficulty,
        color = color,
        userEstimate = userEstimate,
        appEstimate = Duration.ZERO
    )
}