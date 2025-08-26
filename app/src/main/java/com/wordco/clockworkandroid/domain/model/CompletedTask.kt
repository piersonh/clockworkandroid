package com.wordco.clockworkandroid.domain.model

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
    val segments: List<Segment>,
    val markers: List<Marker>,
) : Task {
    val workTime: Duration = segments.filter { it.type == SegmentType.WORK && it.duration != null}
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration!!) }
    val breakTime: Duration = segments.filter { it.type == SegmentType.BREAK && it.duration != null}
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration!!) }
}
