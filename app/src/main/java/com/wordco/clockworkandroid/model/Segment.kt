package com.wordco.clockworkandroid.model

import java.time.Instant
import java.time.Duration

abstract class Segment(startTime: Instant, duration: Duration?) {
    var startTime: Instant = startTime
        private set
    var duration: Duration? = duration
        private set

    fun setEnd(endTime: Instant) {
        duration = Duration.between(startTime, endTime)
    }
}


interface DefaultInstance<out T> {
    fun new(): T
}