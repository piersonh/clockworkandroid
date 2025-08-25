package com.wordco.clockworkandroid.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.domain.model.TimerState
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import com.wordco.clockworkandroid.ui.mapper.toActiveTaskItem
import com.wordco.clockworkandroid.ui.mapper.toNewTaskListItem
import com.wordco.clockworkandroid.ui.mapper.toSuspendedTaskListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface TaskListUiState {

    data object Retrieving : TaskListUiState

    sealed interface Retrieved : TaskListUiState {
        val newTasks: List<NewTaskListItem>
        val suspendedTasks: List<SuspendedTaskListItem>
    }

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



class TaskListViewModel(
    private val taskRepository: TaskRepository,
    private val timer: Timer,
) : ViewModel() {


    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Retrieving)

    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val _timerState = timer.timerState

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
                    it.status == ExecutionStatus.NOT_STARTED
                }.map { it.toNewTaskListItem() }
                        .sortedWith(NewTaskListItemComparator())


                val suspendedTasks = tasks.filter {
                    it.status == ExecutionStatus.SUSPENDED
                }.map { task -> task.toSuspendedTaskListItem() }


                when (timerState) {
                    TimerState.Closing,
                    TimerState.Dormant,
                    TimerState.Preparing -> {
                        TaskListUiState.TimerDormant(
                            newTasks = newTasks,
                            suspendedTasks = suspendedTasks,
                        )
                    }

                    is TimerState.Paused,
                    is TimerState.Running -> {
                        Log.i("ListStateFlow", "${timerState.task}")
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