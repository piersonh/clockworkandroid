package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.wordco.clockworkandroid.MainActivity
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.timer.TimerState
import com.wordco.clockworkandroid.timer_feature.ui.util.toHours
import com.wordco.clockworkandroid.timer_feature.ui.util.toMinutesInHour
import java.util.Locale

class TimerNotificationManager(
    private val context: Context,
) {

    companion object {
        const val NOTIFICATION_ID = 1

        // every time the channel changes, the id needs to change
        // or it won't be reflected without a restart
        const val CHANNEL_ID = "TimerChannel_v2"
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Live notifications for timer status"
            setSound(null,null)
            enableVibration(false)
            enableLights(false)
            setShowBadge(false)
            lockscreenVisibility = VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission") // MANAGER IS ONLY CREATED AFTER CHECK SUCCEEDS
    fun showNotification(timerState: TimerState.Active) {
        val notification = buildNotification(timerState)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun buildPreparingNotification() : Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Preparing...")
            .build()
    }

    fun buildNotification(timerState: TimerState.Active): Notification {
        val markerIntent = createServiceIntent(
            "ACTION_MARKER",
        )
        val markerAction = NotificationCompat.Action(
            null,
            "Add Marker",
            markerIntent
        )
        val resumePauseAction = when (timerState) {
            is TimerState.Running -> {
                val pauseIntent = createServiceIntent(
                    "ACTION_PAUSE",
                )


                NotificationCompat.Action(
                    null,
                    "Pause",
                    pauseIntent
                )

            }
            is TimerState.Paused -> {
                val resumeIntent = createServiceIntent(
                    "ACTION_RESUME",
                )
                NotificationCompat.Action(
                    null,
                    "Resume",
                    resumeIntent
                )
            }
        }

        val deepLinkIntent = createDeepLinkIntent(timerState.task.taskId)

        val content = when (timerState) {
            is TimerState.Paused -> timerState.elapsedBreakMinutes.let {
                String.format(
                    Locale.getDefault(),
                    "On Break: %02d:%02d",
                    it / 60, it % 60
                )
            }
            is TimerState.Running -> timerState.elapsedWorkSeconds.let {
                String.format(
                    Locale.getDefault(),
                    "Working: %02d:%02d",
                    it.toHours(), it.toMinutesInHour()
                )
            }
        }

        val icon = when (timerState) {
            is TimerState.Paused -> R.drawable.mug
            is TimerState.Running -> R.drawable.running
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(content)
            .setOngoing(true)
            .setSmallIcon(icon)
            .setAutoCancel(false)
            .setColor(timerState.task.color.toArgb())  //accent with task color?
            .addAction(resumePauseAction) // resume/pause
            .setContentIntent(deepLinkIntent)
            .setContentText(timerState.task.name)
            .setOnlyAlertOnce(true)
            .apply {
                if (timerState is TimerState.Running) {
                    addAction(markerAction)
                }
            }
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

    private fun createDeepLinkIntent(id: Long): PendingIntent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "com.wordco.clockworkandroid://timer_route?id=$id".toUri(),
            context,
            MainActivity::class.java
        )

        return PendingIntent.getActivity(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}