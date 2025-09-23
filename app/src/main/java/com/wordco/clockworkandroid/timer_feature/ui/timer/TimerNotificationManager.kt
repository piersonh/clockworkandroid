package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.timer.TimerState

class TimerNotificationManager(
    private val context: Context,
    private val onRequestNotificationPermission: () -> Unit,
) {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "TimerChannel"
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer Notifications",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Live notifications for timer status"
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(timerState: TimerState.Active) {
        val notification = buildNotification(timerState)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            onRequestNotificationPermission()
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun buildNotification(timerState: TimerState.Active): Notification {
        val resumePauseAction = when (timerState) {
            is TimerState.Running -> {
                val pauseIntent = createServiceIntent("ACTION_PAUSE")
                NotificationCompat.Action(
                    null,
                    "Pause",
                    pauseIntent
                )
            }
            is TimerState.Paused -> {
                val resumeIntent = createServiceIntent("ACTION_RESUME")
                NotificationCompat.Action(
                    null,
                    "Resume",
                    resumeIntent
                )
            }
        }

        val content = when (timerState) {
            is TimerState.Paused -> "On Break: ${timerState.elapsedBreakMinutes}"
            is TimerState.Running -> "Working: ${timerState.elapsedWorkSeconds}"
        }

        val icon = when (timerState) {
            is TimerState.Paused -> R.drawable.mug
            is TimerState.Running -> R.drawable.running
        }


        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(content)
            .setOngoing(true)
            .setSmallIcon(icon)
            //.setSound(null)
            // .setColor()  //accent with task color?
            //.setSilent(true)
            .addAction(resumePauseAction) // resume/pause
            // .addAction() // suspend
            // .addAction() // finish?
            .build()
    }

    private fun createServiceIntent(action: String?): PendingIntent {
        val intent = Intent(context, TimerService::class.java).apply {
            this.action = action
        }

        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}