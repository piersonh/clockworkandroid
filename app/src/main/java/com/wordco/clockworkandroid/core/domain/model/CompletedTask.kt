package com.wordco.clockworkandroid.core.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class CompletedTask(
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
    override val workTime: Duration = segments.filter { it.type == Segment.Type.WORK && it.duration != null}
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration!!) }
    override val breakTime: Duration = segments.filter { it.type == Segment.Type.BREAK && it.duration != null}
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration!!) }
}
