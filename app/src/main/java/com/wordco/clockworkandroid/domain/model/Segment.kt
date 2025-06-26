package com.wordco.clockworkandroid.domain.model

import java.time.Duration
import java.time.Instant

data class Segment(
    val segmentId: Long,
    val taskId: Long,
    var startTime: Instant,
    var duration: Duration?
) {

    fun setEnd(endTime: Instant) {
        duration = Duration.between(startTime, endTime)
    }
}