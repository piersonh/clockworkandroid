package com.wordco.clockworkandroid.core.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class StartedTask (
    override val taskId: Long,
    override val name: String,
    override val dueDate: Instant?,
    override val difficulty: Int,
    override val color: Color,
    override val userEstimate: Duration?,
    override val segments: List<Segment>,
    override val markers: List<Marker>,
    override val profileId: Long?,
) : Task.HasExecutionData {
    override val workTime: Duration = segments
        .filter { it.type == Segment.Type.WORK && it.duration != null }
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration!!) }
    override val breakTime: Duration = segments
        .filter { it.type == Segment.Type.BREAK && it.duration != null }
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration!!) }

    init {
        if (segments.isEmpty()) {
            error("Attempted to create a Started Task with no segments")
        }
    }

    enum class Status {
        RUNNING, PAUSED, SUSPENDED
    }

    fun status() : Status {
        return when (segments.last().type) {
            Segment.Type.WORK -> Status.RUNNING
            Segment.Type.BREAK -> Status.PAUSED
            Segment.Type.SUSPEND -> Status.SUSPENDED
        }
    }
}