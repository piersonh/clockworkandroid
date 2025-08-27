package com.wordco.clockworkandroid.core.domain.model

import java.time.Instant


data class Marker (
    val markerId: Long,
    val taskId: Long,
    var startTime: Instant,
    var label: String
)