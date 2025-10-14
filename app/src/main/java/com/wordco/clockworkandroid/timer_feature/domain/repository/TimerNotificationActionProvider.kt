package com.wordco.clockworkandroid.timer_feature.domain.repository

import android.app.PendingIntent

interface TimerNotificationActionProvider {
    fun getPauseIntent(): PendingIntent
    fun getResumeIntent(): PendingIntent
    fun getMarkerIntent(): PendingIntent
}