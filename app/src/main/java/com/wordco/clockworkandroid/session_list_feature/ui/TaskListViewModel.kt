package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository
import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toNewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toSuspendedTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.util.NewTaskListItemComparator
import com.wordco.clockworkandroid.session_list_feature.ui.util.toActiveSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskListViewModel(
    private val taskRepository: TaskRepository,
    private val timerRepository: TimerRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Retrieving)

    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val timerState = timerRepository.state

    private val tasks = taskRepository.getTodoTasks()

    init {
        viewModelScope.launch {
            combine(
                timerState,
                tasks,
            ) { timerState, tasks ->

                val newTasks = tasks
                    .filter { it is NewTask }
                    .map { (it as NewTask).toNewTaskListItem() }
                    .sortedWith(NewTaskListItemComparator())


                val suspendedTasks = tasks
                    .filter { it is StartedTask && it.status() == StartedTask.Status.SUSPENDED }
                    .map { (it as StartedTask).toSuspendedTaskListItem() }

                when (timerState) {
                    TimerState.Closing,
                    TimerState.Dormant,
                    is TimerState.Preparing -> {
                        TaskListUiState.TimerDormant(
                            newTasks = newTasks,
                            suspendedTasks = suspendedTasks,
                        )
                    }

                    is TimerState.Active -> {
                        val activeTask = tasks.first { it.taskId == timerState.taskId }
                            .let {
                                val estimate = it.userEstimate
                                val progress = if (estimate != null) {
                                    timerState.totalElapsedSeconds / estimate.seconds.toFloat()
                                } else null
                                ActiveTaskListItem(
                                    name = it.name,
                                    taskId = timerState.taskId,
                                    status = timerState.toActiveSessionStatus(),
                                    color = it.color,
                                    elapsedSeconds = timerState.totalElapsedSeconds,
                                    currentSegmentElapsedSeconds = timerState.currentSegmentElapsedSeconds,
                                    progressToEstimate = progress,
                                )
                            }

                        TaskListUiState.TimerActive(
                            newTasks = newTasks,
                            suspendedTasks = suspendedTasks,
                            activeTask = activeTask,
                        )
                    }
                }
            }
            .collect { uiState ->
                _uiState.update { uiState }
            }
        }
    }



    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val taskRepository = appContainer.sessionRepository
                val timer = appContainer.timerRepository

                TaskListViewModel (
                    taskRepository = taskRepository,
                    timerRepository = timer,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}