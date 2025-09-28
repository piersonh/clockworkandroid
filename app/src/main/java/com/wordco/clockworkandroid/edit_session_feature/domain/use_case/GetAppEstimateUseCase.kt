package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.AppEstimate
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import java.time.Duration
import java.time.Instant
import kotlin.math.absoluteValue
import kotlin.math.min
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
                weight = 2.0
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
                    if (s1.profileId == s2.profileId && s1.profileId != null) {
                        1.0
                    } else {
                        0.0
                    }
                },
                weight = 3.0
            )
        )

        // return List of Doubles between 0 and 1
        val similarityScores = sessionHistory.map {
            gowerSimilarity(it, todoSession, gowerFields)
        }

        //println(similarityScores)

        val recencyWeight = 1.0
        val similarityWeight = 1.0
        val now = Instant.now()
        val weights = sessionHistory
            .zip(similarityScores)
            .map {
                val (session, similarity) = it
                getRecencyScore(
                    session = session,
                    fromInstant = now,
                ).times(recencyWeight)
                    .plus(
                        similarity.times(similarityWeight)
                    ).div(
                        recencyWeight.plus(similarityWeight)
                    )

            }
        //val weights = similarityScores

        // History of error (difference between user estimate and actual time)
        val historicalErrorWork = sessionHistory.map {
            it.userEstimate!!.minus(it.workTime).toMillis()
        }

        val historicalErrorTotal = sessionHistory.map {
            it.userEstimate!!.minus(it.workTime.plus(it.breakTime)).toMillis()
        }

        //println("historicalError $historicalError")

        val weightedMeanWork = weightedMean(
            points = historicalErrorWork,
            weights = weights
        )

        val weightedMeanTotal = weightedMean(
            points = historicalErrorTotal,
            weights = weights
        )

        //println(Duration.ofMillis(weightedMean.toLong()))

//        val weightedStandardDeviation = unBiasedWeightedStandardDeviation(
//            weightedMean = weightedMean,
//            points = historicalError,
//            weights = weights,
//        )

        //println(Duration.ofMillis(weightedStandardDeviation.toLong()))


        val correctedUserEstimateWork = todoSession.userEstimate!!
            .minusMillis(weightedMeanWork.toLong())

        val correctedUserEstimateTotal = todoSession.userEstimate!!
                    .minusMillis(weightedMeanTotal.toLong())

//        val lowEstimate = correctedUserEstimate.minusMillis(
//            (weightedStandardDeviation).toLong()
//        )
//
//        val highEstimate = correctedUserEstimate.plusMillis(
//            (weightedStandardDeviation).toLong()
//        )

        val lowEstimate = minOf(
            a = correctedUserEstimateWork,
            b = correctedUserEstimateTotal
        )

        val highEstimate = maxOf(
            a = correctedUserEstimateWork,
            b = correctedUserEstimateTotal
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
            it.similarityExpr(session1,session2).times(it.weight)
        }

        return sigmaWeightedSimilarities / sigmaWeights
    }

    private val timeUnit = Duration.ofDays(30).toMillis()
    private val decayProtraction = 4 // greater values make the slope more extreme
                                        // around half life but gradual elsewhere
    private val halfLife = 4 // number of time units for weight to be 0.5
    private val offset = halfLife.toDouble().pow(decayProtraction)
    private fun getRecencyScore(
        session: CompletedTask,
        fromInstant: Instant,
    ) : Double {
        // offset / ((units between session and instant)^decayProtraction + offset)
        return Duration
            .between(session.startedAt, fromInstant)
            .toMillis()
            .div(timeUnit.toDouble())
            .pow(decayProtraction)
            .plus(offset)
            .let { offset.div(it) }
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

    private fun unBiasedWeightedStandardDeviation(
        weightedMean: Double,
        points: List<Long>,
        weights: List<Double>
    ): Double {
        // 1. Calculate the numerator: the sum of weighted squared deviations.
        // This part was already correct.
        val sumOfWeightedSquareDeviations = points.zip(weights).sumOf { (point, weight) ->
            val deviation = point - weightedMean
            weight * deviation.pow(2)
        }

        // 2. Calculate the components for the standard denominator.
        val sumOfWeights = weights.sum()
        val sumOfSquaredWeights = weights.sumOf { it * it }

        // Edge Case: If the sum of weights is zero, the deviation is undefined.
        if (sumOfWeights.absoluteValue < 1E-10) {
            return 0.0
        }

        // 3. Calculate the unbiased denominator.
        val denominator = sumOfWeights - (sumOfSquaredWeights / sumOfWeights)

        // Edge Case: If the denominator is not positive, the result is undefined.
        // This can happen if there is only one data point with a non-zero weight.
        if (denominator <= 0) {
            return 0.0
        }

        // 4. Calculate the variance and return its square root.
        val weightedVariance = sumOfWeightedSquareDeviations / denominator
        return sqrt(weightedVariance)
    }

}