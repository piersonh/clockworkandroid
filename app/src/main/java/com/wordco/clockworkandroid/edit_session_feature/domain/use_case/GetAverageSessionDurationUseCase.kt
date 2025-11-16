package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.time.Duration
import java.time.Instant

class GetAverageSessionDurationUseCase(
    private val sessionRepository: TaskRepository,
) {
    companion object {
        private val RECENCY_WINDOW = Duration.ofDays(120)
    }

    suspend operator fun invoke(profileId: Long): ((difficulty: Int) -> Duration)? {
        val recency = Instant.now().minus(RECENCY_WINDOW)
        val sessions = sessionRepository.getCompletedSessionsForProfile(profileId).first()
            .filter { it.completedAt > recency }
            .sortedByDescending { it.completedAt }

        if (sessions.isEmpty()) return null

        val firstDifficulty = sessions.first().difficulty
        if (sessions.size >= 2 && sessions.any { it.difficulty != firstDifficulty }) {
            val results = getLinearRegression(sessions)

            return { difficulty ->
                Duration.ofSeconds(
                    (results.baseTime + (difficulty * results.difficultyFactor)).toLong()
                )
            }
        } else {
            val average = getSimpleAverage(sessions)

            return { difficulty ->
                average
            }
        }
    }


    private fun getLinearRegression(sessions: List<CompletedTask>): LinearRegressionResults {
        val regression = SimpleRegression(true)

        for (session in sessions) {
            regression.addData(
                session.difficulty.toDouble(),
                session.totalTime.seconds.toDouble()
            )
        }

        if (regression.intercept >= 0) {
            return LinearRegressionResults(
                baseTime = regression.intercept,
                difficultyFactor = regression.slope,
                rSquared = regression.rSquare
            )
        } else {
            // redo regression to force all points to be non-negative
            val noInterceptRegression = SimpleRegression(false)
            for (session in sessions) {
                noInterceptRegression.addData(
                    session.difficulty.toDouble(),
                    session.totalTime.seconds.toDouble()
                )
            }

            return LinearRegressionResults(
                baseTime = 0.0,
                difficultyFactor = noInterceptRegression.slope,
                rSquared = noInterceptRegression.rSquare
            )
        }
    }

    private data class LinearRegressionResults(
        val baseTime: Double,
        val difficultyFactor: Double,
        val rSquared: Double,
    )

    private fun getSimpleAverage(sessions: List<CompletedTask>): Duration {
        val averageSeconds = sessions
            .map { it.workTime.seconds }
            .average()

        return Duration.ofSeconds(averageSeconds.toLong())
    }
}