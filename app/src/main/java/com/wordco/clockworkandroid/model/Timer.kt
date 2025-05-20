package com.wordco.clockworkandroid.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UnloadedTimerException: Exception("The timer does not have a task loaded")


class Timer private constructor(task: Task) {

    enum class State {
        IDLE,       // When the timer is loaded with a new task
        RUNNING,    // When the timer is executing a loaded task
        PAUSED,     // When the timer has paused a loaded task
        SUSPENDED   // When the timer is loaded with or suspends a loaded task
    }

    // On instantiation, the task can either be new or suspended
    // PAUSED tasks cannot be unloaded (therefor then cannot be loaded)
    // COMPLETED tasks cannot be loaded into the timer
    private val _state = MutableStateFlow(
        if (task.segments.isEmpty()) {
            State.IDLE
        } else {
            State.SUSPENDED
        }
    )

    val state: StateFlow<State> = _state


    // Singleton pattern (kinda)
    // Current working model: there should be a single timer e.g. stopwatch in the entire app.
    // Tasks (more specifically execution history) are loaded into the timer
    // At the time of writing, the timer page is responsible for displaying task properties.
    // This should change so that a new task properties page forwards the user to the timer page
    // when they press 'start' (or 'resume' for suspended tasks), so that users can view the
    // properties of a task while the timer is executing a different task.  This would come with
    // the deprecation of the INIT state (and the FINISHED state is probably not meaningful either)
    companion object {
        private var instance: Timer? = null

        fun loadTask(task: Task) {

            // CLOSE OLD TASK
            instance?.suspendTimer()


            // OPEN NEW TASK
            // check if task is competed?
            instance = Timer(task)
        }

        fun startTimer() {
            if (instance != null) {
                instance!!.startTimer()
            } else {
                throw UnloadedTimerException()
            }
        }

        fun pauseTimer() {
            if (instance != null) {
                instance!!.pauseTimer()
            } else {
                throw UnloadedTimerException()
            }
        }

        fun suspendTimer() {
            if (instance != null) {
                instance!!.suspendTimer()
            } else {
                throw UnloadedTimerException()
            }
        }
    }

    private val _secondsElapsed = MutableStateFlow(0)
    private val secondsElapsed: StateFlow<Int> = _secondsElapsed

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default) // Or any appropriate dispatcher

    private fun startTimer() {
        if (timerJob?.isActive == true) return // We should probably raise an exception here

        _state.update {State.RUNNING}

        timerJob = scope.launch {
            while (true) {
                delay(1000)
                _secondsElapsed.update { it + 1 }
            }
        }
    }

    private fun pauseTimer() {
        _state.update {State.PAUSED}
        timerJob?.cancel()
    }

    private fun suspendTimer() {
        _state.update {State.SUSPENDED}
        timerJob?.cancel()
    }

    private fun getHours() : Int {
        return _secondsElapsed.value / 3600
    }

    private fun getMinutesInHour() : Int {
        return (_secondsElapsed.value % 3600) / 60
    }
}