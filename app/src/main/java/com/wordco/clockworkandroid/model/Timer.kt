package com.wordco.clockworkandroid.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Timer(state: State = State.INIT) {
    enum class State {
        INIT, RUNNING, PAUSED, SUSPENDED, FINISHED
    }
    private val _state = MutableStateFlow(state)
    val state: StateFlow<State> = _state

    private val _secondsElapsed = MutableStateFlow(0)
    val secondsElapsed: StateFlow<Int> = _secondsElapsed

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default) // Or any appropriate dispatcher

    fun startTimer() {
        if (timerJob?.isActive == true) return // Prevent starting multiple times

        _state.update {State.RUNNING}

        timerJob = scope.launch {
            while (true) {
                delay(1000)
                _secondsElapsed.update { it + 1 }
            }
        }
    }

    fun stopTimer() {
        _state.update {State.PAUSED}
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        _secondsElapsed.value = 0
    }

    fun getHours() : Int {
        return _secondsElapsed.value / 3600
    }

    fun getMinutesInHour() : Int {
        return (_secondsElapsed.value % 3600) / 60
    }
}