package com.wordco.clockworkandroid.reminder.data

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.wordco.clockworkandroid.MainActivity
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager

class ReminderNotificationManagerImpl(
    private val context: Context,
) : ReminderNotificationManager {

    private val notificationManager = NotificationManagerCompat.from(context)

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
    override fun sendReminderNotification(
        message: String,
        sessionId: Long,
        notificationId: Int
    ) {
        val notification = buildNotification(message, sessionId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("ReminderNotifications", "Failed to show notification: no permission")
        } else {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun buildNotification(
        message: String,
        sessionId: Long,
    ) : Notification {
        val deepLinkIntent = createDeepLinkIntent(sessionId)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.stopwatch)
            .setContentTitle(message)
            .setContentText("Due Now")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss notification when tapped
            .setContentIntent(deepLinkIntent)
            .build()
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