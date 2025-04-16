package com.wordco.clockworkandroid.model

import java.time.Duration
import java.time.Instant

class SusSegment(startTime: Instant, duration: Duration?) : Segment(startTime, duration) {
    companion object : DefaultInstance<SusSegment> {
        override fun new(): SusSegment {
            return SusSegment(Instant.now(), null)
        }
    }

}