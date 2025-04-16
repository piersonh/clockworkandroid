package com.wordco.clockworkandroid.model

import java.time.Duration
import java.time.Instant

class WorkSegment(startTime: Instant, duration: Duration?) : Segment(startTime, duration) {
    companion object : DefaultInstance<WorkSegment> {
        override fun new(): WorkSegment {
            return WorkSegment(Instant.now(), null)
        }
    }

}