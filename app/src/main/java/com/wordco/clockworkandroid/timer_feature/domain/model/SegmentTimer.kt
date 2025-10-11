package com.wordco.clockworkandroid.timer_feature.domain.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SegmentTimer(
    coroutineScope: CoroutineScope,
    private val startTime: Long
) {
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds = _elapsedSeconds.asStateFlow()

    private val job = coroutineScope.launch {
        while (true) {
            val elapsedTimeSeconds = (System.currentTimeMillis() - startTime).toInt() / 1000

            if (elapsedTimeSeconds != elapsedSeconds.value) {
                _elapsedSeconds.update { elapsedTimeSeconds }
            }

            delay(100)
        }
    }

    fun stop() {
        job.cancel()
    }
}