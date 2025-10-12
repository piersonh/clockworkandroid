package com.wordco.clockworkandroid.session_list_feature.ui

import android.util.Log
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
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toActiveTaskItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toNewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toSuspendedTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.util.NewTaskListItemComparator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskListViewModel(
    private val taskRepository: TaskRepository,
    private val timerRepository: TimerRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Retrieving)

    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val _timerState = timerRepository.state

    private val _tasks = taskRepository.getTodoTasks()
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

                    is TimerState.Paused,
                    is TimerState.Running -> {
                        val activeTask = tasks.first { it.taskId == timerState.taskId }
                            .let { it as? StartedTask }
                            ?: run {
                                Log.w("TodoListVM", "todo list session list flow is behind")
                                return@combine null
                            }


                        // The session list flow might not update with the activated session
                        //  before the timerstate flow does.  If, so skip the emission
                        if (activeTask.status() == StartedTask.Status.SUSPENDED) {
                            Log.w("TodoListVM", "todo list session list flow is behind")
                            return@combine null
                        }

                        TaskListUiState.TimerActive(
                            newTasks = newTasks,
                            suspendedTasks = suspendedTasks,
                            activeTask = activeTask.toActiveTaskItem(
                                elapsedWorkSeconds = timerState.elapsedWorkSeconds,
                                elapsedBreakMinutes = timerState.elapsedBreakMinutes,
                            ),
                        )
                    }
                }
            }
            .filterNotNull()
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