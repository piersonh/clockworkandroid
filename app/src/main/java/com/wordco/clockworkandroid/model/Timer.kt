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
        INIT, RUNNING, PAUSED, SUSPENDED, FINISHED
    }

    private val _state = MutableStateFlow(

        // At the moment of writing, there is a status property in TaskProperties
        // It might be a better idea to pull from that instead of determining the execution
        // state of the task by judging its segments
        // Additionally, there is no logic to deal with a finished state
        // There should never be a case where we will load a task that is running or paused, but
        // I am not sure about finished tasks

        if (task.segments.isEmpty()) {
            State.INIT
        } else {
            State.SUSPENDED
        }
    )

    val state: StateFlow<State> = _state


    // Singleton pattern
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
        if (timerJob?.isActive == true) return // Prevent starting multiple times

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