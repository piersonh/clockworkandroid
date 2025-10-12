package com.wordco.clockworkandroid.timer_feature.domain.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SegmentTimer(
    coroutineScope: CoroutineScope,
    private var startTime: Long
) {
    private val _elapsedSeconds = MutableStateFlow(getElapsedSeconds())
    val elapsedSeconds = _elapsedSeconds.asStateFlow()

    private val job = coroutineScope.launch {
        while (true) {
            val elapsedTimeSeconds = getElapsedSeconds()

            if (elapsedTimeSeconds != elapsedSeconds.value) {
                _elapsedSeconds.update { elapsedTimeSeconds }
            }

            delay(100)
        }
    }

    fun reset(startTime: Long) {
        this.startTime = startTime

        _elapsedSeconds.update { getElapsedSeconds() }
    }

    fun stop() {
        job.cancel()
    }

    private fun getElapsedSeconds() : Int {
        return (System.currentTimeMillis() - startTime).toInt() / 1000
    }
}