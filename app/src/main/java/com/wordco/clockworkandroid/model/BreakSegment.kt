package com.wordco.clockworkandroid.model

import java.time.Duration
import java.time.Instant

class BreakSegment(startTime: Instant, duration: Duration?) : Segment(startTime, duration) {
    companion object : DefaultInstance<BreakSegment> {
        override fun new(): BreakSegment {
            return BreakSegment(Instant.now(), null)
        }
    }

}