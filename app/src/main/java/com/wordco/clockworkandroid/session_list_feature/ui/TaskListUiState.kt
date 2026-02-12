package com.wordco.clockworkandroid.session_list_feature.ui

import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.NewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem

sealed interface TaskListUiState {
    data class Error(
        val header: String,
        val message: String,
    ) : TaskListUiState

    data object Retrieving : TaskListUiState

    sealed interface Retrieved : TaskListUiState {
        val newTasks: List<NewTaskListItem>
        val suspendedTasks: List<SuspendedTaskListItem>
    }

    // TODO: get rid of timer states and use nullable ActiveTaskListItem in Retrieved
    data class TimerDormant(
        override val newTasks: List<NewTaskListItem>,
        override val suspendedTasks: List<SuspendedTaskListItem>,
    ) : Retrieved

    data class TimerActive(
        override val newTasks: List<NewTaskListItem>,
        override val suspendedTasks: List<SuspendedTaskListItem>,
        val activeTask: ActiveTaskListItem,
    ) : Retrieved
}