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
        private val RECENCY_WINDOW = Duration.ofDays(60)
    }

    suspend operator fun invoke(profileId: Long): ((difficulty: Int) -> Duration)? {
        val recency = Instant.now().minus(RECENCY_WINDOW)
        val sessions = sessionRepository.getSessionsForProfile(profileId).first() // TODO make repo function for this
            .filterIsInstance<CompletedTask>()
            .filter { it.completedAt > recency }
            .sortedByDescending { it.completedAt }

        if (sessions.size < 2) return null

        val results = getLinearRegression(sessions)

        return { difficulty ->
            Duration.ofSeconds(
                (results.baseTime + (difficulty * results.difficultyFactor)).toLong()
            )
        }
    }


    private fun getLinearRegression(sessions: List<CompletedTask>): LinearRegressionResults {
        val regression = SimpleRegression()

        for (session in sessions) {
            regression.addData(
                session.difficulty.toDouble(),
                session.totalTime.seconds.toDouble()
            )
        }

        return LinearRegressionResults(
            baseTime = regression.intercept,
            difficultyFactor = regression.slope,
            rSquared = regression.rSquare
        )
    }

    data class LinearRegressionResults(
        val baseTime: Double,
        val difficultyFactor: Double,
        val rSquared: Double,
    )
}