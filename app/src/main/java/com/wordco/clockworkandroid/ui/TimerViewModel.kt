package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.NewTask
import com.wordco.clockworkandroid.domain.model.StartedTask
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.domain.model.TimerState
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TimerUiState {
    data object Retrieving : TimerUiState

    sealed interface Retrieved : TimerUiState {
        val taskName: String
        val elapsedSeconds: Int
    }

    data class New (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Shelved

    sealed interface Active : Retrieved

    sealed interface Shelved : Retrieved

    data class Running (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Active

    data class Paused (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Active

    data class Suspended (
        override val taskName: String,
        override val elapsedSeconds: Int
    ) : Shelved
}


class TimerViewModel (
    private val taskId: Long,
    private val timer: Timer,
    private val taskRepository: TaskRepository,
    //private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimerUiState>(TimerUiState.Retrieving)

    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _loadedTask = taskRepository.getTask(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private val _timerState = timer.timerState
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
                        0
                    )
                }

                val task = task as StartedTask

                when (timerState) {
                    is TimerState.Empty -> TimerUiState.Suspended(
                        task.name,
                        task.workTime.seconds.toInt()
                    )
                    is TimerState.HasTask if timerState.task.taskId != taskId -> TimerUiState.Suspended(
                        task.name,
                        task.workTime.seconds.toInt()
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
        timer.start(taskId)
    }


    fun takeBreak() {
        timer.pause()
    }

    fun suspendTimer() {
        timer.suspend()
    }

    fun resumeTimer() {
        timer.resume()
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