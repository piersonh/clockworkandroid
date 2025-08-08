package com.wordco.clockworkandroid.ui


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import com.wordco.clockworkandroid.ui.mapper.toStartedTaskListItem
import com.wordco.clockworkandroid.ui.mapper.toUpcomingTaskListItem
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private lateinit var tasks: LiveData<List<Task>>

    // mutableStateListOf??
    lateinit var upcomingTaskList: LiveData<List<UpcomingTaskListItem>>
    //private set

    lateinit var startedTaskList: LiveData<List<StartedTaskListItem>>
    //private set

    var currentTask by mutableStateOf<Task?>(null)
        private set

    init {
        viewModelScope.launch {
            tasks = taskRepository.getTasks().asLiveData()
            setupTaskList()

//            taskRepository.insertTask(
//                Task(
//                    taskId = 0,
//                    name = "DYNAMIC TEST 4",
//                    dueDate = Instant.parse("2023-01-01T18:29:04Z"),
//                    difficulty = 1,
//                    color = Color(3, 169, 244, 255),
//                    status = ExecutionStatus.NOT_STARTED,
//                    segments = emptyList(),
//                    markers = emptyList()
//                )
//            )
//
//            for (task in DummyData.TASKS) {
//                taskRepository.insertTask(task)
//            }
        }
    }
    fun insertTask(task : Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
            //setupTaskList()
        }
    }


    private fun setupTaskList() {
        val comparator = UpcomingTaskListItemComparator()

        upcomingTaskList = tasks.map {
            it.filter { task -> task.status == ExecutionStatus.NOT_STARTED }
                .map { task -> task.toUpcomingTaskListItem() }
                .sortedWith(comparator)
        }

        // TODO: Should probably have a different section of the page for running tasks
        startedTaskList = tasks.map {
            it.filter { task ->
                task.status == ExecutionStatus.RUNNING ||
                        task.status == ExecutionStatus.SUSPENDED ||
                        task.status == ExecutionStatus.PAUSED
            }
                .map { task -> task.toStartedTaskListItem() }
        }
    }

    companion object {

//        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : ViewModel> create(
//                modelClass: Class<T>,
//                extras: CreationExtras
//            ): T {
//                // For when the data objects belong to the app instance
//                val application = checkNotNull(extras[APPLICATION_KEY])
//
//                return TaskViewModel(
//                    (application as MainApplication).taskRepository
//                    //MainApplication.taskRepository
//                ) as T
//            }
//        }

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                TaskViewModel (
                    taskRepository = taskRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}