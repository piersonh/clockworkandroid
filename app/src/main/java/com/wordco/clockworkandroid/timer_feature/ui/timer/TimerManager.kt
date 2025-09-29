package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.Timer
import com.wordco.clockworkandroid.core.ui.timer.TimerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerManager(
    private val context: Context,
    private val taskRepository: TaskRepository,
) : Timer {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var serviceCollectorJob: Job? = null

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

            serviceCollectorJob?.cancel()
            serviceCollectorJob = scope.launch {
                timerService?.state?.collect { serviceState ->
                    _state.update { serviceState }
                }
            }

            restoreAfterExit()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            timerService = null
            serviceCollectorJob?.cancel()
        }

    }

    init {
        Intent(
            context,
            TimerService::class.java
        ).also { intent ->
            context.bindService(
                intent,
                connection,
                Context.BIND_AUTO_CREATE,
            )
        }
    }

    private fun restoreAfterExit() {
        scope.launch {
            taskRepository.getActiveTask()?.first()?.let {
                timerService?.prepareAndStartActiveSession(it.taskId)
            }
        }
    }

    override fun start(taskId: Long) {
        ContextCompat.startForegroundService(context, Intent(context, TimerService::class.java))
        timerService?.start(taskId)
    }
    override fun resume() = timerService?.resume() ?: Unit
    override fun pause() = timerService?.pause() ?: Unit
    override fun suspend(replaceWith: Long?) {
        timerService?.suspend(replaceWith)
        if (replaceWith == null) {
            timerService?.stop()
        }
    }
    override fun finish() {
        timerService?.finish()
        timerService?.stop()
    }
    override fun addMarker() : String = timerService?.addMarker() ?: error("Timer service failed")
}