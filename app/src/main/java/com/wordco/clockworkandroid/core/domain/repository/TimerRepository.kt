package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.TimerState
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    val state: Flow<TimerState>

    fun start(taskId: Long)

    fun resume()

    fun pause()

    fun suspend(replaceWith: Long? = null)

    fun finish()
}