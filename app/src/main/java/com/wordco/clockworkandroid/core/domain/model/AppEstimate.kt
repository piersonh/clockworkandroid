package com.wordco.clockworkandroid.core.domain.model

import java.time.Duration

data class AppEstimate(
    val low: Duration,
    val high: Duration
)
