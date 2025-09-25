package com.wordco.clockworkandroid.session_completion_feature.domain.use_case

import java.time.Duration
import kotlin.math.max

class CalculateEstimateAccuracyUseCase {
    operator fun invoke(
        taskTime: Duration,
        userEstimate: Duration,
    ): Double {
        return userEstimate
            .minus(taskTime)
            .abs()
            .toMillis()
            .div(
                taskTime
                    .toMillis()
                    .toDouble()
            ).let { 1.0.minus(it) }
            .times(100)
            .let {
                max(it, 0.0)
            }
    }
}