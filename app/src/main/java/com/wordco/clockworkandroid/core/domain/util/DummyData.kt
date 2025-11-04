package com.wordco.clockworkandroid.core.domain.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.AppEstimate
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.model.Segment
import java.time.Duration
import java.time.Instant

object DummyData {
    val SESSIONS = listOf(
        NewTask(
            taskId = 1,
            profileId = 1,
            name = "Session 1",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(0f, 1f, 1f),
            userEstimate = Duration.ofHours(1).plusMinutes(21),
            appEstimate = AppEstimate(
                low = Duration.ofHours(0).plusMinutes(21),
                high = Duration.ofHours(2).plusMinutes(21),
            ),
        ),
        NewTask(
            taskId = 2,
            profileId = 1,
            name = "Session 2",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(0f, 1f, 1f),
            userEstimate = null,
            appEstimate = null,
        ),
        NewTask(
            taskId = 3,
            profileId = 1,
            name = "Session 3",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(0f, 1f, 1f),
            userEstimate = null,
            appEstimate = null,
        ),
        NewTask(
            taskId = 4,
            profileId = 2,
            name = "Session 4",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(120f, 1f, 1f),
            userEstimate = null,
            appEstimate = null,
        ),
        NewTask(
            taskId = 5,
            profileId = 2,
            name = "Session 5",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(120f, 1f, 1f),
            userEstimate = null,
            appEstimate = null,
        ),
        NewTask(
            taskId = 6,
            profileId = 3,
            name = "Session 6",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(240f, 1f, 1f),
            userEstimate = null,
            appEstimate = null,
        ),
        CompletedTask(
            taskId = 7,
            name = "Session 7",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(123f, 1f, 1f),
            userEstimate = Duration.ofMinutes(21),
            segments = listOf(
                Segment(
                    segmentId = 1,
                    taskId = 7,
                    startTime = Instant.parse("2025-10-03T10:15:30.00Z"),
                    duration = Duration.ofSeconds(1452),
                    type = Segment.Type.WORK
                )
            ),
            markers = emptyList(),
            profileId = null,
            appEstimate = null,
        ),
        CompletedTask(
            taskId = 8,
            name = "Session 8",
            dueDate = Instant.now(),
            difficulty = 1,
            color = Color.hsv(30f, 1f, 1f),
            userEstimate = Duration.ofMinutes(45),
            segments = listOf(
                Segment(
                    segmentId = 2,
                    taskId = 8,
                    startTime = Instant.now().minus(Duration.ofHours(1)),
                    duration = Duration.ofMinutes(40),
                    type = Segment.Type.WORK
                )
            ),
            markers = emptyList(),
            profileId = 1,
            appEstimate = null,
        ),
        CompletedTask(
            taskId = 9,
            name = "Session 9",
            dueDate = Instant.now(),
            difficulty = 1,
            color = Color.hsv(60f, 1f, 1f),
            userEstimate = Duration.ofHours(1),
            segments = listOf(
                Segment(
                    segmentId = 3,
                    taskId = 9,
                    startTime = Instant.now().minus(Duration.ofDays(1)),
                    duration = Duration.ofMinutes(55),
                    type = Segment.Type.WORK
                )
            ),
            markers = emptyList(),
            profileId = 2,
            appEstimate = null,
        ),
        CompletedTask(
            taskId = 10,
            name = "Session 10",
            dueDate = Instant.now(),
            difficulty = 1,
            color = Color.hsv(90f, 1f, 1f),
            userEstimate = Duration.ofMinutes(30),
            segments = listOf(
                Segment(
                    segmentId = 4,
                    taskId = 10,
                    startTime = Instant.now().minus(Duration.ofDays(2)),
                    duration = Duration.ofMinutes(25),
                    type = Segment.Type.WORK
                ),
                Segment(
                    segmentId = 5,
                    taskId = 10,
                    startTime = Instant.now().minus(Duration.ofDays(2).plusMinutes(25)),
                    duration = Duration.ofMinutes(5),
                    type = Segment.Type.BREAK
                )
            ),
            markers = emptyList(),
            profileId = 1,
            appEstimate = null,
        ),

    )


    val PROFILES = listOf(
        Profile(
            id = 1,
            name = "Profile 1",
            color = Color.hsv(0f, 1f, 1f),
            defaultDifficulty = 0,
            sessions = SESSIONS.filter { it.profileId == 1L }
        ),
        Profile(
            id = 2,
            name = "Profile 2",
            color = Color.hsv(120f, 1f, 1f),
            defaultDifficulty = 0,
            sessions = SESSIONS.filter { it.profileId == 2L }
        ),
        Profile(
            id = 3,
            name = "Profile 3",
            color = Color.hsv(240f, 1f, 1f),
            defaultDifficulty = 0,
            sessions = SESSIONS.filter { it.profileId == 3L }
        ),
    )
}
