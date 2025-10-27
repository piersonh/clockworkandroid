package com.wordco.clockworkandroid.reminder

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.permission.PermissionRequestSignaller
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager
import com.wordco.clockworkandroid.timer_feature.ui.notification.TimerNotificationManagerImpl.Companion.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ReminderNotificationManagerImpl(
    private val context: Context,
    private val permissionSignal: PermissionRequestSignaller,
    private val coroutineScope: CoroutineScope,
) : ReminderNotificationManager {

    private val notificationManager = NotificationManagerCompat.from(context)

    private var permissionJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "ReminderChannel_v1"
    }

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Task Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for task reminders"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }


    /**
     * Helper method to build and show the notification.
     */
    override fun sendReminderNotification(message: String, notificationId: Int) {
        val notification = buildNotification(message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (permissionJob == null) {
                permissionJob = coroutineScope.launch {
                    val hasPermission = permissionSignal.request(
                        Manifest.permission.POST_NOTIFICATIONS
                    )

                    if (hasPermission) {
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    }
                }
            }
        } else {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun buildNotification(
        message: String
    ) : Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.stopwatch) // TODO: Add your icon
            .setContentTitle("Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss notification when tapped
            .build()
    }
}