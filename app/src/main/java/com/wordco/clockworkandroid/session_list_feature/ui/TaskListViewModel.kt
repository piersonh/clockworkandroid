package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.data.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.timer.TimerManager
import com.wordco.clockworkandroid.core.timer.TimerState
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toActiveTaskItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toNewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toSuspendedTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.util.NewTaskListItemComparator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskListViewModel(
    private val taskRepository: TaskRepository,
    private val timer: TimerManager,
) : ViewModel() {


    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Retrieving)

    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val _timerState = timer.state

    private val _tasks = taskRepository.getTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    init {
        viewModelScope.launch {
            combine(
                _timerState,
                _tasks,
            ) { timerState, tasks ->

                if (tasks == null) {
                    return@combine TaskListUiState.Retrieving
                }

                val newTasks = tasks.filter {
                    it is NewTask
                }.map { (it as NewTask).toNewTaskListItem() }
                        .sortedWith(NewTaskListItemComparator())


                val suspendedTasks = tasks.filter {
                    it is StartedTask && it.status() == StartedTask.Status.SUSPENDED
                }.map { task -> (task as StartedTask).toSuspendedTaskListItem() }


                when (timerState) {
                    TimerState.Closing,
                    TimerState.Dormant,
                    is TimerState.Preparing -> {
                        TaskListUiState.TimerDormant(
                            newTasks = newTasks,
                            suspendedTasks = suspendedTasks,
                        )
                    }

                    is TimerState.Paused,
                    is TimerState.Running -> {
                        TaskListUiState.TimerActive(
                            newTasks = newTasks,
                            suspendedTasks = suspendedTasks,
                            activeTask = timerState.task.toActiveTaskItem(
                                elapsedWorkSeconds = timerState.elapsedWorkSeconds,
                                elapsedBreakMinutes = timerState.elapsedBreakMinutes,
                            )
                        )
                    }
                }
            }.collect { uiState ->
                _uiState.update { uiState }
            }
        }
    }



    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val timer = (this[APPLICATION_KEY] as MainApplication).timer

                TaskListViewModel (
                    taskRepository = taskRepository,
                    timer = timer,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}