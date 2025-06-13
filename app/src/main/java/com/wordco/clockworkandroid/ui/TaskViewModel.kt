package com.wordco.clockworkandroid.ui


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.wordco.clockworkandroid.ClockWorkApp
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import com.wordco.clockworkandroid.ui.mapper.toStartedTaskListItem
import com.wordco.clockworkandroid.ui.mapper.toUpcomingTaskListItem
import com.wordco.clockworkandroid.util.DummyData
import kotlinx.coroutines.launch
import java.time.Instant

class TaskViewModel (
    private val taskRepository: TaskRepository
) : ViewModel() {

    private lateinit var tasks : List<Task>

    // mutableStateListOf??
    var upcomingTaskList by mutableStateOf<List<UpcomingTaskListItem>>(emptyList())
        private set

    var startedTaskList by mutableStateOf<List<StartedTaskListItem>>(emptyList())
        private set

    init {
        viewModelScope.launch {
//            for (task in DummyData.TASKS) {
//                taskRepository.insertTask(task)
//            }

            tasks = taskRepository.getTasks()
            setupTaskList()

//            taskRepository.insertTask(
//                Task(
//                    taskId = 0,
//                    name = "DYNAMIC TEST 3",
//                    dueDate = Instant.parse("2023-01-01T18:29:04Z"),
//                    difficulty = 1,
//                    color = Color(3, 169, 244, 255),
//                    status = ExecutionStatus.NOT_STARTED,
//                    segments = emptyList(),
//                    markers = emptyList()
//                )
//            )
        }
    }

    private fun setupTaskList() {
        val comparator = UpcomingTaskListItemComparator()

        upcomingTaskList = tasks
            .filter { task -> task.status == ExecutionStatus.NOT_STARTED }
            .map{task -> task.toUpcomingTaskListItem()}
            .sortedWith(comparator)

        // TODO: Should probably have a different section of the page for running tasks
        startedTaskList = tasks
            .filter { task -> task.status == ExecutionStatus.RUNNING ||
                    task.status == ExecutionStatus.SUSPENDED ||
                    task.status == ExecutionStatus.PAUSED }
            .map { task -> task.toStartedTaskListItem() }
    }

    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                return TaskViewModel(
                    (application as ClockWorkApp).taskRepository
                ) as T
            }
        }
    }
}