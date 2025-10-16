package com.wordco.clockworkandroid.session_completion_feature.domain.use_case

import java.time.Duration
import kotlin.math.abs

/**
 * Calculates the accuracy of a user's time estimate compared to the actual task time.
 *
 * The accuracy is a percentage from 0% to 100%, where 100% is a perfect estimate.
 * An estimate that is off by 100% or more (e.g., double the actual time) results
 * in an accuracy of 0%.
 */
class CalculateEstimateAccuracyUseCase {

    /**
     * @param taskTime The actual duration the task took.
     * @param userEstimate The duration the user estimated for the task.
     * @return A [Double] representing the accuracy percentage (e.g., 95.0 for 95%).
     * Returns 0.0 if the [taskTime] is zero to prevent division by zero.
     */
    operator fun invoke(
        taskTime: Duration,
        userEstimate: Duration,
    ): Double {
        if (taskTime.isZero) {
            return if (userEstimate.isZero) 100.0 else 0.0
        }

        val taskMillis = taskTime.toMillis().toDouble()
        val estimateMillis = userEstimate.toMillis().toDouble()

        val absoluteError = abs(estimateMillis - taskMillis)

        val relativeError = absoluteError / estimateMillis

        val accuracy = (1.0 - relativeError) * 100.0

        return accuracy.coerceIn(0.0, 100.0)
    }
}