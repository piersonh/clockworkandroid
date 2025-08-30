package com.wordco.clockworkandroid.core.ui.timer

import kotlinx.coroutines.flow.StateFlow

interface Timer {
    val state: StateFlow<TimerState>

    fun start (taskId: Long)

    fun resume()

    fun pause()

    fun suspend(replaceWith: Long? = null)
}