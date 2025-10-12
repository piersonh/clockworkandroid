package com.wordco.clockworkandroid.timer_feature.data

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.wordco.clockworkandroid.MainActivity
import com.wordco.clockworkandroid.PermissionRequestSignaller
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.timer_feature.ui.util.toHours
import com.wordco.clockworkandroid.timer_feature.ui.util.toMinutesInHour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

class TimerNotificationManager(
    private val context: Context,
    private val permissionSignal:  PermissionRequestSignaller,
    private val coroutineScope: CoroutineScope,
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
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(
        timerState: TimerState.Active,
        session: Task,
    ) {
        val notification = buildNotification(timerState, session)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            coroutineScope.launch {
                val hasPermission = permissionSignal.request(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                if (hasPermission) {
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }
        } else {
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun buildPreparingNotification() : Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Preparing...")
            .build()
    }

    fun buildNotification(
        timerState: TimerState.Active,
        session: Task,
    ): Notification {
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

        val deepLinkIntent = createDeepLinkIntent(timerState.taskId)

        val content = when (timerState) {
            is TimerState.Paused -> timerState.elapsedBreakMinutes.let {
                String.Companion.format(
                    Locale.getDefault(),
                    "On Break: %02d:%02d",
                    it / 60, it % 60
                )
            }
            is TimerState.Running -> timerState.elapsedWorkSeconds.let {
                String.Companion.format(
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
            .setColor(session.color.toArgb())  //accent with task color?
            .addAction(resumePauseAction) // resume/pause
            .setContentIntent(deepLinkIntent)
            .setContentText(session.name)
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