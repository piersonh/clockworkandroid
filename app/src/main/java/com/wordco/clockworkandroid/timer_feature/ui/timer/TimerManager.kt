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
            taskRepository.getActiveTaskId()?.let { taskId ->
                val restoreIntent = Intent(context, TimerService::class.java).apply {
                    action = TimerService.ACTION_START
                    putExtra(TimerService.EXTRA_TASK_ID, taskId)
                }

                // Start the service with this explicit command
                ContextCompat.startForegroundService(context, restoreIntent)
            }
        }
    }

    override fun start(taskId: Long) {
        val startIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_TASK_ID, taskId)
        }

        // Start the service with this explicit command
        ContextCompat.startForegroundService(context, startIntent)
    }
    override fun resume() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_RESUME
        }
        // You can use startService here, as the service is already in the foreground
        context.startService(intent)
    }
    override fun pause() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
        }
        context.startService(intent)
    }
    override fun suspend(replaceWith: Long?) {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_SUSPEND
            replaceWith?.let {
                putExtra(TimerService.EXTRA_TASK_ID, it)
            }
        }
        // Use startService because we are potentially stopping the foreground session
        context.startService(intent)
    }
    override fun finish() {
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_FINISH
        }
        context.startService(intent)
    }
}