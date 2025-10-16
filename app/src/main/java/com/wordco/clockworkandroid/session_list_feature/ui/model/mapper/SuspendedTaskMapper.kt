package com.wordco.clockworkandroid.session_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem

fun StartedTask.toSuspendedTaskListItem() : SuspendedTaskListItem {
    val elapsedSeconds = workTime.plus(breakTime).seconds.toInt()
    val progress = if (userEstimate != null) {
        elapsedSeconds / userEstimate.seconds.toFloat()
    } else null

    return SuspendedTaskListItem(
        taskId = taskId,
        name = name,
        color = color,
        elapsedSeconds = elapsedSeconds,
        progressToEstimate = progress,
    )
}