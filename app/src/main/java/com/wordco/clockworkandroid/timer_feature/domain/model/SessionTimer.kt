package com.wordco.clockworkandroid.timer_feature.domain.model

import com.wordco.clockworkandroid.core.domain.model.Second
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionTimer(
    private val coroutineScope: CoroutineScope,
    initialWorkSeconds: Second,
    initialBreakMinutes: Int,
    startAsBreak: Boolean = false,
) {
    private val _elapsedWorkSeconds = MutableStateFlow<Second>(initialWorkSeconds)
    val elapsedWorkSeconds = _elapsedWorkSeconds.asStateFlow()
    private val _elapsedBreakMinutes = MutableStateFlow(initialBreakMinutes)
    val elapsedWorkMinutes = _elapsedBreakMinutes.asStateFlow()

    private val workTimer = Incrementer.of(
        interval = 1000,
        initialOffset = { initialWorkSeconds.times(1000).toLong() },
        stateField = _elapsedWorkSeconds
    )

    val breakTimer = Incrementer.of(
        interval = 60000,
        initialOffset = { initialBreakMinutes.times(60 * 1000).toLong() },
        stateField =  _elapsedBreakMinutes
    )

    private var incJob = coroutineScope.launch (
        block = if (startAsBreak) {
            breakTimer()
        } else {
            workTimer()
        }

    )


    fun setWorkIncrementer() {
        setIncrementer(workTimer)
    }

    fun setBreakIncrementer() {
        setIncrementer(breakTimer)
    }


    private fun setIncrementer(incrementer: Incrementer) {
        incJob.cancel()
        incJob = coroutineScope.launch (block = incrementer())
    }
}