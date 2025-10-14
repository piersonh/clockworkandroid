package com.wordco.clockworkandroid.session_list_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem

fun TimerState.Active.toActiveSessionStatus(): ActiveTaskListItem.Status {
    return when (this) {
        is TimerState.Paused -> ActiveTaskListItem.Status.PAUSED
        is TimerState.Running -> ActiveTaskListItem.Status.RUNNING
    }
}