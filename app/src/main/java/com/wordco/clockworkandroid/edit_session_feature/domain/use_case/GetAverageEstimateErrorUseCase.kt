package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.time.Duration
import java.time.Instant

class GetAverageEstimateErrorUseCase(
    private val sessionRepository: TaskRepository,
) {
    companion object {
        private val RECENCY_WINDOW = Duration.ofDays(60)
    }

    suspend operator fun invoke(profileId: Long): ((difficulty: Int) -> Double)? {
        val recency = Instant.now().minus(RECENCY_WINDOW)
        val sessions = sessionRepository.getCompletedSessionsForProfile(profileId).first()
            .filter { it.completedAt > recency && it.userEstimate != null }
            .sortedByDescending { it.completedAt }

        if (sessions.size < 2) return null

        val firstDifficulty = sessions.first().difficulty
        if (sessions.any { it.difficulty != firstDifficulty }) {
            val results = getLinearRegression(sessions)

            return { difficulty ->
                results.baseError + (difficulty * results.difficultyFactor)
            }
        } else {
            val average = getSimpleAverage(sessions)

            return { difficulty ->
                average
            }
        }
    }


    private fun CompletedTask.getRelativeError(): Double {
        val sessionMillis = totalTime.toMillis().toDouble()
        val estimateMillis = userEstimate?.toMillis()?.toDouble() ?:
        error("You must filter the sessions for null estimates before calling getLinearRegression")

        val percentOfTruth =  estimateMillis / sessionMillis

        return 1 - percentOfTruth
    }


    private fun getLinearRegression(sessions: List<CompletedTask>): LinearRegressionResults {
        val regression = SimpleRegression()

        for (session in sessions) {
            val relativeError = session.getRelativeError()

            regression.addData(
                session.difficulty.toDouble(),
                relativeError
            )
        }

        return LinearRegressionResults(
            baseError = regression.intercept,
            difficultyFactor = regression.slope,
            rSquared = regression.rSquare
        )
    }

    private data class LinearRegressionResults(
        val baseError: Double,
        val difficultyFactor: Double,
        val rSquared: Double,
    )


    private fun getSimpleAverage(sessions: List<CompletedTask>): Double {
        return sessions.map {
            it.getRelativeError()
        }.average()
    }
}