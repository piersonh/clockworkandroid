package com.wordco.clockworkandroid.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.Segment
import com.wordco.clockworkandroid.domain.model.SegmentType
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

// maybe represent as sealed interface loading & execution states?
data class TimerUiState (
    val isLoading: Boolean = true,
    val loadedTask: Task? = null,
    val executionState: TimerState = TimerState.WAITING,
    val timerSeconds: Int = 0,
)

class TimerViewModel (
    private val taskId: Long,
    private val timer: Timer,
    private val taskRepository: TaskRepository,
    //private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(TimerUiState())

    val state: StateFlow<TimerUiState>
        get() = _state

    private val _loadedTask = taskRepository.getTask(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private val _executionState = MutableStateFlow(TimerState.WAITING)

    private val _timerSeconds = timer.secondsElapsed

    init {
        viewModelScope.launch {

            combine(
                _loadedTask,
                _executionState,
                _timerSeconds
            ) {
                task,
                executionState,
                timerSeconds,
                ->

                TimerUiState(
                    isLoading = false,
                    loadedTask = task,
                    executionState = executionState,
                    timerSeconds = timerSeconds,
                )
            }.collect {
                _state.value = it
            }
        }

    }

    fun startTimer() {
        timer.startTimer()
        _executionState.update { TimerState.RUNNING }

        viewModelScope.launch {
            taskRepository.insertSegment(
                Segment(
                    segmentId = 0,
                    taskId = taskId,
                    startTime = Instant.now(),
                    duration = null,
                    type = SegmentType.WORK
                )
            )
        }
    }

    fun takeBreak() {
        timer.stopTimer()
        _executionState.update { TimerState.BREAK }

        viewModelScope.launch {
            // TODO: Test this when .value is null
            _loadedTask.value?.run {
                segments.last().run {
                    val duration = Duration.between(startTime, Instant.now())
                    taskRepository.insertSegment(copy(duration=duration))
                }
            }.also {
                taskRepository.insertSegment(
                    Segment(
                        segmentId = 0,
                        taskId = taskId,
                        startTime = Instant.now(),
                        duration = null,
                        type = SegmentType.BREAK
                    )
                )
            } ?: {
                Log.println(Log.ERROR, "TimerSaveProgress",
                    "Failed to save progress for taskId=${taskId} because failed to load."
                )

            }

            null // this is here because println returns and int
        }
    }

    fun suspendTimer() {
        timer.stopTimer()
        _executionState.update { TimerState.SUSPENDED }

        viewModelScope.launch {
            _loadedTask.value?.run {
                segments.last().run {
                    val duration = Duration.between(startTime, Instant.now())
                    taskRepository.insertSegment(copy(duration=duration))
                }
            } ?: {
                Log.println(Log.ERROR, "TimerSaveProgress",
                    "Failed to save progress for taskId=${taskId} because failed to load."
                )
            }

            null
        }
    }

    fun addMark() {

    }

    fun finish() {

    }



    companion object {

        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val timer = (this[APPLICATION_KEY] as MainApplication).timer
                val taskId = this[TASK_ID_KEY] as Long

                TimerViewModel (
                    taskId = taskId,
                    timer = timer,
                    taskRepository = taskRepository,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}