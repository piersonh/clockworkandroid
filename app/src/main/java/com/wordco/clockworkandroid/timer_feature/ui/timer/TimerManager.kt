package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.wordco.clockworkandroid.core.ui.timer.Timer
import com.wordco.clockworkandroid.core.ui.timer.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerManager(private val context: Context) : Timer {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var timerService: TimerService? = null
    private var isBound = false

    private val _state = MutableStateFlow<TimerState>(TimerState.Dormant)
    override val state = _state.asStateFlow()

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
            Log.i("TimerServiceConnection", "Timer Service Connected")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            timerService = null
            Log.i("TimerServiceConnection", "Timer Service Disconnected")
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
        Log.i("TimerManagerInit", "Timer Manager Initialized")
    }

    override fun start(taskId: Long) = timerService?.start(taskId) ?: Unit
    override fun resume() = timerService?.resume() ?: Unit
    override fun pause() = timerService?.pause() ?: Unit
    override fun suspend(replaceWith: Long?) = timerService?.suspend(replaceWith) ?: Unit
}