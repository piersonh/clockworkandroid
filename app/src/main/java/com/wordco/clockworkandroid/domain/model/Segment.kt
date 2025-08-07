package com.wordco.clockworkandroid.domain.model

import java.time.Duration
import java.time.Instant

data class Segment(
    val segmentId: Long,
    val taskId: Long,
    val startTime: Instant,
    var duration: Duration?,
    val type: SegmentType
) {

    fun setEnd(endTime: Instant) {
        duration = Duration.between(startTime, endTime)
    }
}