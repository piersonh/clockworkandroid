package com.wordco.clockworkandroid.timer_feature.data.factory

import android.content.Context
import android.content.Intent
import com.wordco.clockworkandroid.timer_feature.data.service.TimerService

class TimerServiceIntentFactory(private val context: Context) {
    fun createStartIntent(taskId: Long) : Intent {
        return Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_TASK_ID, taskId)
        }
    }


    fun createResumeIntent() : Intent {
        return Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_RESUME
        }
    }


    fun createPauseIntent() : Intent {
        return Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
        }
    }


    fun createSuspendIntent(replaceWith: Long?) : Intent {
        return Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_SUSPEND
            replaceWith?.let {
                putExtra(TimerService.EXTRA_TASK_ID, it)
            }
        }
    }


    fun createFinishIntent() : Intent {
        return Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_FINISH
        }
    }

    fun createMarkerIntent() : Intent {
        return Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_MARKER
        }
    }
}