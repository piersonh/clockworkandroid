package com.wordco.clockworkandroid

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAppEstimateUseCase
import org.junit.Test
import java.time.Duration
import java.time.Instant

class GetAppEstimateUseCaseTest {

    @Test
    fun generateEstimate() {

        val sessionHistory = listOf(
            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Overestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(2),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(2).plusMinutes(30),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Underestimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(30),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            ),

            // Equal Estimate
            CompletedTask(
                taskId = 0,
                name = "TestItem",
                dueDate = null,
                difficulty = 2,
                color = Color.Red,
                userEstimate = Duration.ofHours(1).plusMinutes(45),
                segments = listOf(
                    Segment(
                        segmentId = 0,
                        taskId = 0,
                        startTime = Instant.now(),
                        duration = Duration.ofHours(1).plusMinutes(45),
                        type = Segment.Type.WORK
                    )
                ),
                markers = emptyList(),
                profileId = 0,
                appEstimate = null,
            )
        )

        val newSession = NewTask(
            taskId = 0,
            name = "TestNewTask",
            dueDate = null,
            difficulty = 3,
            color = Color.Blue,
            userEstimate = Duration.ofHours(2).plusMinutes(45),
            profileId = 0,
            appEstimate = null,
        )

        val estimate = GetAppEstimateUseCase()

        val estimateString = estimate(
            newSession,
            sessionHistory
        )

        println(estimateString)
    }
}