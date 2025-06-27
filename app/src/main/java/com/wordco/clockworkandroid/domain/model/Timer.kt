package com.wordco.clockworkandroid.domain.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Timer() {

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _secondsElapsed = MutableStateFlow(0)
    val secondsElapsed: StateFlow<Int> = _secondsElapsed

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default) // Or any appropriate dispatcher

    fun startTimer() {
        if (timerJob?.isActive == true) return // Prevent starting multiple times

        _isRunning.update { true }

        timerJob = scope.launch {
            while (true) {
                delay(1000)
                _secondsElapsed.update { it + 1 }
            }
        }
    }
    fun setTimer(setTime: Int) {
        _secondsElapsed.update { setTime }
    }

    fun stopTimer() {
        _isRunning.update { false }
        timerJob?.cancel()
    }

//    fun resetTimer() {
//        stopTimer()
//        _secondsElapsed.value = 0
//    }

}