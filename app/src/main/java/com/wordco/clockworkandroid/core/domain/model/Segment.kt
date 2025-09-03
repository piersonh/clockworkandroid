package com.wordco.clockworkandroid.core.domain.model

import java.time.Duration
import java.time.Instant

data class Segment(
    val segmentId: Long,
    val taskId: Long,
    val startTime: Instant,
    var duration: Duration?,
    val type: Type
) {
    enum class Type {
        WORK, BREAK, SUSPEND, FINISHED
    }
}