package com.wordco.clockworkandroid.timer_feature.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.wordco.clockworkandroid.timer_feature.data.factory.TimerServiceIntentFactory
import com.wordco.clockworkandroid.timer_feature.data.service.TimerService
import com.wordco.clockworkandroid.timer_feature.domain.repository.TimerNotificationActionProvider

class TimerNotificationActionProviderImpl(
    private val context: Context,
    private val intentFactory: TimerServiceIntentFactory
) : TimerNotificationActionProvider {
    override fun getPauseIntent(): PendingIntent {
        val intent = intentFactory.createPauseIntent()
        return createServicePendingIntent(intent, TimerService.ACTION_PAUSE)
    }

    override fun getResumeIntent(): PendingIntent {
        val intent = intentFactory.createResumeIntent()
        return createServicePendingIntent(intent, TimerService.ACTION_RESUME)
    }

    override fun getMarkerIntent(): PendingIntent {
        val intent = intentFactory.createMarkerIntent()
        return createServicePendingIntent(intent, TimerService.ACTION_MARKER)
    }


    private fun createServicePendingIntent(intent: Intent, action: String): PendingIntent {
        return PendingIntent.getService(
            context,
            action.hashCode(), // Use a unique request code for each action
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}