package com.wordco.clockworkandroid.timer_feature.ui

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RestoreTimerObserver (
    private val sessionRepository: TaskRepository,
    private val timerRepository: TimerRepository,
    private val externalScope: CoroutineScope
) : DefaultLifecycleObserver {

    private var isFirstStart = true

    // is invoked everytime the app moves from the background to the foreground
    override fun onStart(owner: LifecycleOwner) {
        // only run the check the very first time the app moves to the foreground
        if (isFirstStart) {
            externalScope.launch {
                sessionRepository.getActiveTaskId()?.let { sessionId ->
                    timerRepository.start(sessionId)
                }
            }
            isFirstStart = false
        }
    }
}