package com.wordco.clockworkandroid

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.data.fake.FakeSessionRepository
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageEstimateErrorUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Duration
import java.time.Instant

class GetAverageEstimateErrorUseCaseTest {

    @Test
    fun `50 percent underestimate`() = runTest{
        val sessionRepository = FakeSessionRepository(
            initialValues = listOf(
                CompletedTask(
                    taskId = 1,
                    name = "1",
                    dueDate = null,
                    difficulty = 1,
                    color = Color.Red,
                    userEstimate = Duration.ofHours(1),
                    segments = listOf(
                        Segment(
                            segmentId = 1,
                            taskId = 1,
                            startTime = Instant.now(),
                            duration = Duration.ofHours(2).plusSeconds(1),
                            type = Segment.Type.WORK
                        )
                    ),
                    markers = emptyList(),
                    profileId = 1,
                    appEstimate = null
                ),
                CompletedTask(
                    taskId = 2,
                    name = "2",
                    dueDate = null,
                    difficulty = 1,
                    color = Color.Red,
                    userEstimate = Duration.ofMinutes(30),
                    segments = listOf(
                        Segment(
                            segmentId = 2,
                            taskId = 2,
                            startTime = Instant.now(),
                            duration = Duration.ofHours(1).minusSeconds(1),
                            type = Segment.Type.WORK
                        )
                    ),
                    markers = emptyList(),
                    profileId = 1,
                    appEstimate = null
                ),
                CompletedTask(
                    taskId = 3,
                    name = "3",
                    dueDate = null,
                    difficulty = 1,
                    color = Color.Red,
                    userEstimate = Duration.ofHours(5),
                    segments = listOf(
                        Segment(
                            segmentId = 3,
                            taskId = 3,
                            startTime = Instant.now(),
                            duration = Duration.ofHours(10).plusSeconds(2),
                            type = Segment.Type.WORK
                        )
                    ),
                    markers = emptyList(),
                    profileId = 1,
                    appEstimate = null
                ),
            )
        )

        val getAverageEstimateErrorUseCase = GetAverageEstimateErrorUseCase(
            sessionRepository = sessionRepository
        )

        val estimateCalculator = getAverageEstimateErrorUseCase(1)

        org.junit.Assert.assertNotNull(estimateCalculator)

        val error = estimateCalculator!!(1)

        org.junit.Assert.assertEquals(0.5, error, 0.0001)
    }
}