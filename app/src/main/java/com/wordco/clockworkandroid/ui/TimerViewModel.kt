package com.wordco.clockworkandroid.ui

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel (
    private val timer: Timer,
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val taskId : Long = savedStateHandle.toRoute<PageRoutes.Timer>().id

    val loadedTask = taskRepository.getTask(taskId).asLiveData()

    var isRunning = timer.isRunning.asLiveData()

    var secondsElapsed = timer.secondsElapsed.asLiveData()

    private val _state = MutableLiveData(TimerState.WAITING)
    val state : LiveData<TimerState>
        get() = _state

    init {
        viewModelScope.launch {
            loadedTask.asFlow().first().let {
                task ->
                timer.setTimer(
                    task.workTime.toMillis().toInt()
                )
            }
        }
    }

    fun startTimer() {
        timer.startTimer()
        _state.value = TimerState.RUNNING
    }

    fun takeBreak() {
        timer.stopTimer()
        _state.value = TimerState.BREAK
    }

    fun suspendTimer() {
        timer.stopTimer()
        _state.value = TimerState.SUSPENDED
    }

    fun addMark() {

    }

    fun finish() {

    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val timer = (this[APPLICATION_KEY] as MainApplication).timer
                TimerViewModel (
                    timer = timer,
                    taskRepository = taskRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}