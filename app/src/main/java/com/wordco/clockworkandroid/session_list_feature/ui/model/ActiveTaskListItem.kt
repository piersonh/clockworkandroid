package com.wordco.clockworkandroid.session_list_feature.ui.model

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.Second
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.model.TimerState

data class ActiveTaskListItem(
    val taskId: Long,
    val name: String,
    val status: Status,
    val color: Color,
    val elapsedSeconds: Second,
    val currentSegmentElapsedSeconds: Second,
    val progressToEstimate: Float?,
) {
    enum class Status {
        RUNNING, PAUSED
    }

    companion object {
        fun from(
            session: Task.Todo, // TODO: to be completely correct, this should be StartedTask, but the database does not update fast enough
            timerState: TimerState.Active,
        ): ActiveTaskListItem {
            val estimate = session.userEstimate

            val progress = if (estimate != null) {
                timerState.totalElapsedSeconds / estimate.seconds.toFloat()
            } else null

            return ActiveTaskListItem(
                name = session.name,
                taskId = timerState.taskId,
                status = when (timerState) {
                    is TimerState.Paused -> Status.PAUSED
                    is TimerState.Running -> Status.RUNNING
                },
                color = session.color,
                elapsedSeconds = timerState.totalElapsedSeconds,
                currentSegmentElapsedSeconds = timerState.currentSegmentElapsedSeconds,
                progressToEstimate = progress,
            )
        }
    }
}