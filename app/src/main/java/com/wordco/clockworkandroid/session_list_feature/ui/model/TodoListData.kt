package com.wordco.clockworkandroid.session_list_feature.ui.model

data class TodoListData(
    val activeSession: ActiveTaskListItem?,
    val newSessions: List<NewTaskListItem>,
    val suspendedSessions: List<SuspendedTaskListItem>,
)
