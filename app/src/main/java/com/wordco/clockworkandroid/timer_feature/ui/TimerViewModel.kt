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
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository
import com.wordco.clockworkandroid.core.domain.use_case.DeleteSessionUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant


class TimerViewModel (
    private val taskId: Long,
    private val timerRepository: TimerRepository,
    private val getSessionUseCase: GetSessionUseCase,
    private val addMarkerUseCase: AddMarkerUseCase,
    private val completeStartedSessionUseCase: CompleteStartedSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    //private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.Retrieving)
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TimerUiEvent>()
    val events = _events.asSharedFlow()

    private val loadedTask = getSessionUseCase(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private val timerState = timerRepository.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private var lastKnownActiveSeconds: Int? = null

    init {
        viewModelScope.launch {

            combine(
                loadedTask,
                timerState
            ) {
                task, timerState ->

                if (task == null || timerState == null) {
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
                    is TimerState.Empty -> {
                        val seconds = lastKnownActiveSeconds ?: task.workTime.plus(task.breakTime).seconds.toInt()
                        TimerUiState.Suspended(
                            task.name,
                            seconds,
                            timerState is TimerState.Preparing && timerState.taskId == taskId
                        )
                    }
                    is TimerState.Active if timerState.taskId != taskId -> TimerUiState.Suspended(
                        task.name,
                        task.workTime.seconds.toInt(),
                        false
                    )
                    is TimerState.Paused -> TimerUiState.Paused(
                        task.name,
                        timerState.totalElapsedSeconds,
                        currentSegmentElapsedSeconds = timerState.currentSegmentElapsedSeconds,
                    )
                    is TimerState.Running -> TimerUiState.Running(
                        task.name,
                        timerState.totalElapsedSeconds,
                        currentSegmentElapsedSeconds = timerState.currentSegmentElapsedSeconds
                    )
                }
            }
            .collect { newState ->
                if (newState is TimerUiState.Active) {
                    lastKnownActiveSeconds = newState.totalElapsedSeconds
                }

                _uiState.update { newState }
            }
        }

    }

    fun onDeleteClick() {
        viewModelScope.launch {
            deleteSessionUseCase(taskId)
            _events.emit(TimerUiEvent.NavigateBack)
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
            val session = loadedTask.value as? StartedTask
                ?: error ("addMark can only be called when timer is running")
            val markerName = addMarkerUseCase(
                session = session,
                Instant.now()
            )

            _events.emit(
                TimerUiEvent.ShowSnackbar("Added $markerName to Timeline")
            )
        }
    }

    fun finish() {
        val currentTimerState = timerState.value

        viewModelScope.launch {
            if (currentTimerState is TimerState.Active && currentTimerState.taskId == taskId) {
                timerRepository.finish()
            } else {
                val currentTask = loadedTask.value as StartedTask

                completeStartedSessionUseCase(
                    currentTask,
                    Instant.now()
                )
            }

            _events.emit(TimerUiEvent.FinishSession)
        }
    }



    companion object {

        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val timer = appContainer.timerRepository
                val getSessionUseCase = appContainer.getSessionUseCase
                val addMarkerUseCase = appContainer.addMarkerUseCase
                val completeStartedSessionUseCase = appContainer.completeStartedSessionUseCase
                val deleteSessionUseCase = appContainer.deleteSessionUseCase
                val taskId = this[TASK_ID_KEY] as Long

                TimerViewModel(
                    taskId = taskId,
                    timerRepository = timer,
                    getSessionUseCase = getSessionUseCase,
                    addMarkerUseCase = addMarkerUseCase,
                    completeStartedSessionUseCase = completeStartedSessionUseCase,
                    deleteSessionUseCase = deleteSessionUseCase,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}