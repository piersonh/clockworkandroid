package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.AppEstimate
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

class GetAppEstimateUseCase {
    operator fun invoke(
        todoSession: Task.Todo,
        sessionHistory: List<CompletedTask>
    ) : AppEstimate {
        if (todoSession.userEstimate == null || sessionHistory.any { it.userEstimate == null }) {
            error("User Estimate must be defined")
        }

        // Add any other important parameters HERE
        val gowerFields = listOf(
            // User Estimate
            GowerField(
                similarityExpr = { s1, s2 ->
                    val range = fieldRange(
                        dataSet = sessionHistory.plus(todoSession)
                    ) {
                        it.userEstimate!!
                    }.let {
                        val (min, max) = it
                        max.minus(min)
                    }.toMillis()

                    s1.userEstimate!!
                        .minus(s2.userEstimate!!)
                        .abs()
                        .toMillis()
                        .div(range.toDouble())
                },
                weight = 1.0
            ),
            // Difficulty Input
            GowerField(
                similarityExpr = { s1, s2 ->
                    val range = fieldRange(
                        dataSet = sessionHistory.plus(todoSession)
                    ) {
                        it.difficulty
                    }.let {
                        val (min, max) = it
                        max.minus(min)
                    }

                    s1.difficulty
                        .minus(s2.difficulty)
                        .absoluteValue
                        .div(range.toDouble())
                },
                weight = 1.0
            ),
            // Task Profile
            GowerField(
                similarityExpr = { s1, s2 ->
                    if (s1.profileId == s2.profileId && s1.profileId == null) {
                        1.0
                    } else {
                        0.0
                    }
                },
                weight = 1.0
            )
        )

        // return List of Doubles between 0 and 1
        val similarityScores = sessionHistory.map {
            gowerSimilarity(it, todoSession, gowerFields)
        }

        // History of error (difference between user estimate and actual time)
        val historicalError = sessionHistory.map {
            it.userEstimate!!.minus(it.workTime).toMillis()
        }

        val weightedMean = weightedMean(
            points = historicalError,
            weights = similarityScores
        )

        val weightedStandardDeviation = weightedStandardDeviation(
            weightedMean = weightedMean,
            points = historicalError,
            weights = similarityScores,
        )


        val userEstimate = todoSession.userEstimate!!

        val lowEstimate = userEstimate.minusMillis(
            (weightedMean + weightedStandardDeviation).toLong()
        )

        val highEstimate = userEstimate.plusMillis(
            (weightedMean + weightedStandardDeviation).toLong()
        )
        // +/- (mean + standardDeviation)

        return AppEstimate(lowEstimate, highEstimate)

    }

    private fun <T : Comparable<T>>fieldRange(
        dataSet: List<Task>,
        supplier: (Task) -> T
    ) : Pair<T,T> {
        val min = dataSet.minOf(supplier)
        val max = dataSet.maxOf(supplier)

        return Pair(min,max)
    }

    private data class GowerField(
        val similarityExpr: (Task, Task) -> Double,
        val weight: Double,
    )

    private fun gowerSimilarity(
        session1: Task,
        session2: Task,
        fields: List<GowerField>
    ) : Double {
        val sigmaWeights = fields.sumOf { it.weight }
        val sigmaWeightedSimilarities = fields.sumOf {
            it.similarityExpr(session1,session2)
        }

        return sigmaWeightedSimilarities / sigmaWeights
    }

    private fun weightedMean(
        points: List<Long>,
        weights: List<Double>,
    ) : Double {
        val sigmaWeights = weights.sum()
        val sigmaWeightedPoints = points.zip(weights).sumOf {
            val (point, weight) = it
            point.times(weight)
        }

        return sigmaWeightedPoints.div(sigmaWeights)
    }

    private fun weightedStandardDeviation(
        weightedMean: Double,
        points: List<Long>,
        weights: List<Double>
    ) : Double {
        val normalizedSigmaWeights = weights
            .sum()
            .times(
                weights
                    .count { it.absoluteValue > 1E-10 }
                    .let {
                        it.minus(1).div(it.toDouble())
                    }
            )
        val sigmaWeightedSquareDeviation = points.zip(weights).sumOf {
            val (point, weight) = it
            point
                .minus(weightedMean)
                .pow(2)
                .times(weight)
        }

        return sigmaWeightedSquareDeviation
            .div(normalizedSigmaWeights)
            .let { sqrt(it) }
    }

}