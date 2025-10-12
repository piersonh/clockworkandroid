package com.wordco.clockworkandroid.timer_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TimerViewModel (
    private val taskId: Long,
    private val timerRepository: TimerRepository,
    private val taskRepository: TaskRepository,
    private val addMarkerUseCase: AddMarkerUseCase,
    private val completeStartedSessionUseCase: CompleteStartedSessionUseCase
    //private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.Retrieving)
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TimerUiEvent>()
    val events = _events.asSharedFlow()

    private val _loadedTask = taskRepository.getTask(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private val _timerState = timerRepository.state
        //.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),timer.timerState.value)

    init {
        viewModelScope.launch {

            combine(
                _loadedTask,
                _timerState
            ) {
                task, timerState ->

                if (task == null) {
                    return@combine TimerUiState.Retrieving
                }

                if (task is NewTask) {
                    return@combine TimerUiState.New(
                        task.name,
                        0,
                        timerState is TimerState.Preparing && timerState.taskId == taskId
                    )
                }

                if (task is CompletedTask) {
                    return@combine TimerUiState.Finished(
                        task.name,
                        0,
                        timerState is TimerState.Dormant
                    )
                }

                val task = task as StartedTask

                when (timerState) {
                    is TimerState.Empty -> TimerUiState.Suspended(
                        task.name,
                        task.workTime.seconds.toInt(),
                        timerState is TimerState.Preparing && timerState.taskId == taskId
                    )
                    is TimerState.Active if timerState.taskId != taskId -> TimerUiState.Suspended(
                        task.name,
                        task.workTime.seconds.toInt(),
                        false
                    )
                    is TimerState.Paused -> TimerUiState.Paused(
                        task.name,
                        timerState.elapsedWorkSeconds
                    )
                    is TimerState.Running -> TimerUiState.Running(
                        task.name,
                        timerState.elapsedWorkSeconds
                    )
                }
            }.collect {
                _uiState.value = it
            }
        }

    }

    fun initTimer() {
        timerRepository.start(taskId)
    }


    fun takeBreak() {
        timerRepository.pause()
    }

    fun suspendTimer() {
        timerRepository.suspend()
    }

    fun resumeTimer() {
        timerRepository.resume()
    }

    fun addMark() {
        viewModelScope.launch {
            val session = _loadedTask.value as? StartedTask
                ?: error ("addMark can only be called when timer is running")
            val markerName = addMarkerUseCase(
                sessionRepository = taskRepository,
                session = session
            )

            _events.emit(
                TimerUiEvent.ShowSnackbar("Added $markerName to Timeline")
            )
        }
    }

    fun finish() {
        if (timerRepository.state.value is TimerState.Empty) {
            viewModelScope.launch {
                completeStartedSessionUseCase(_loadedTask.value as StartedTask)
            }
        } else {
            timerRepository.finish()
        }
    }



    companion object {

        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val taskRepository = appContainer.sessionRepository
                val timer = appContainer.timerRepository
                val addMarkerUseCase = appContainer.addMarkerUseCase
                val completeStartedSessionUseCase = appContainer.completeStartedSessionUseCase
                val taskId = this[TASK_ID_KEY] as Long

                TimerViewModel(
                    taskId = taskId,
                    timerRepository = timer,
                    taskRepository = taskRepository,
                    addMarkerUseCase = addMarkerUseCase,
                    completeStartedSessionUseCase = completeStartedSessionUseCase,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}