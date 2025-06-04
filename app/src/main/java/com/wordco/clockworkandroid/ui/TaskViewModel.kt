package com.wordco.clockworkandroid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import com.wordco.clockworkandroid.ui.mapper.toStartedTaskListItem
import com.wordco.clockworkandroid.ui.mapper.toUpcomingTaskListItem
import kotlinx.coroutines.launch

class TaskViewModel (
    private val taskRepository: TaskRepository
) : ViewModel() {

    private lateinit var tasks : List<Task>

    var upcomingTaskList by mutableStateOf<List<UpcomingTaskListItem>>(emptyList())
        private set

    var startedTaskList by mutableStateOf<List<StartedTaskListItem>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            tasks = taskRepository.getTasks()
            setupTaskList()
        }
    }

    private fun setupTaskList() {
        val comparator = UpcomingTaskListItemComparator()

        upcomingTaskList = tasks
            .filter { task -> task.status == ExecutionStatus.NOT_STARTED }
            .map{task -> task.toUpcomingTaskListItem()}
            .sortedWith(comparator)

        startedTaskList = tasks
            .filter { task -> task.status == ExecutionStatus.RUNNING ||
                    task.status == ExecutionStatus.SUSPENDED ||
                    task.status == ExecutionStatus.PAUSED }
            .map { task -> task.toStartedTaskListItem() }
    }
}