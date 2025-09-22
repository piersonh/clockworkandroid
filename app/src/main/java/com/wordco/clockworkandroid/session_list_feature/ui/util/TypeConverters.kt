package com.wordco.clockworkandroid.session_list_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem

fun StartedTask.Status.toActiveTaskStatus(): ActiveTaskListItem.Status {
    return when (this) {
        StartedTask.Status.RUNNING -> ActiveTaskListItem.Status.RUNNING
        StartedTask.Status.PAUSED -> ActiveTaskListItem.Status.PAUSED
        StartedTask.Status.SUSPENDED -> error("Active Tasks cannot be suspended")
    }
}