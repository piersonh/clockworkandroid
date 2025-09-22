package com.wordco.clockworkandroid.session_list_feature.ui

import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.CompletedTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.NewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem

sealed interface TaskListUiState {

    data object Retrieving : TaskListUiState

    sealed interface Retrieved : TaskListUiState {
        val newTasks: List<NewTaskListItem>
        val suspendedTasks: List<SuspendedTaskListItem>
        val finishedTasks: List<CompletedTaskListItem>
    }

    data class TimerDormant(
        override val newTasks: List<NewTaskListItem>,
        override val suspendedTasks: List<SuspendedTaskListItem>,
        override val finishedTasks: List<CompletedTaskListItem>,
    ) : Retrieved

    data class TimerActive(
        override val newTasks: List<NewTaskListItem>,
        override val suspendedTasks: List<SuspendedTaskListItem>,
        override val finishedTasks: List<CompletedTaskListItem>,
        val activeTask: ActiveTaskListItem,
    ) : Retrieved
}