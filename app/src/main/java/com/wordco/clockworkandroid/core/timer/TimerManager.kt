package com.wordco.clockworkandroid.core.timer

import TimerState
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerManager(private val context: Context) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var timerService: TimerService? = null
    private var isBound = false

    private val _state = MutableStateFlow<TimerState>(TimerState.Dormant)
    val state = _state.asStateFlow()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            val binder = p1 as TimerService.TimerBinder
            timerService = binder.getService()
            isBound = true
            coroutineScope.launch {
                timerService?.state?.collect { serviceState ->
                    _state.update { serviceState }
                }
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            timerService = null
        }

    }

    init {
        Intent(
            context,
            TimerService::class.java
        ).also { intent ->
            context.startService(intent)
            context.bindService(
                intent,
                connection,
                Context.BIND_AUTO_CREATE,
            )
        }
    }

    fun start(taskId: Long) = timerService?.start(taskId)
    fun resume() = timerService?.resume()
    fun pause() = timerService?.pause()
    fun suspend(replaceWith: Long? = null) = timerService?.suspend(replaceWith)
}