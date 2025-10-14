package com.wordco.clockworkandroid.timer_feature.domain.repository

import android.app.Notification
import com.wordco.clockworkandroid.core.domain.model.TimerState
import kotlinx.coroutines.flow.Flow

interface TimerNotificationManager {
    fun observeState(stateFlow: Flow<TimerState>)
    fun getForegroundNotification(): Notification
}