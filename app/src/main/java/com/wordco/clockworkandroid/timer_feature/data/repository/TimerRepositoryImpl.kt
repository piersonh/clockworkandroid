package com.wordco.clockworkandroid.timer_feature.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository
import com.wordco.clockworkandroid.timer_feature.data.factory.TimerServiceIntentFactory
import com.wordco.clockworkandroid.timer_feature.data.service.TimerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class TimerRepositoryImpl(
    private val context: Context,
    private val intentFactory: TimerServiceIntentFactory,
) : TimerRepository {
    override val state: Flow<TimerState> = callbackFlow {
        var collectorJob: Job? = null

        val connection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as TimerService.TimerBinder
                val timerService = binder.getService()

                collectorJob?.cancel()
                collectorJob = launch {
                    timerService.state.collect { serviceState ->
                        send(serviceState)
                    }
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                collectorJob?.cancel()
            }
        }

        // bind to the service when the flow is first collected
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        // when the collector of this flow cancels its coroutine, cancel the connection
        //  between the ui and the service.  this should not stop the service (or the notification)
        awaitClose {
            context.unbindService(connection)
        }
    }


    override fun start(taskId: Long) {
        val startIntent = intentFactory.createStartIntent(taskId)

        // Start the service with this explicit command
        ContextCompat.startForegroundService(context, startIntent)
    }
    override fun resume() {
        val intent = intentFactory.createResumeIntent()

        // You can use startService here, as the service is already in the foreground
        context.startService(intent)
    }
    override fun pause() {
        val intent = intentFactory.createPauseIntent()

        context.startService(intent)
    }
    override fun suspend(replaceWith: Long?) {
        val intent = intentFactory.createSuspendIntent(replaceWith)

        // Use startService because we are potentially stopping the foreground session
        context.startService(intent)
    }
    override fun finish() {
        val intent = intentFactory.createFinishIntent()

        context.startService(intent)
    }
}