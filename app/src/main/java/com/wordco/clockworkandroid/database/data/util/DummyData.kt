package com.wordco.clockworkandroid.database.data.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import java.time.Duration
import java.time.Instant

object DummyData {
    val TASKS = listOf(
        StartedTask(
            1,
            "Assignment",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Green,
            null,
            listOf(
                Segment(
                    segmentId = 0,
                    taskId = 1,
                    startTime = Instant.parse("2025-04-17T18:31:04Z"),
                    duration = Duration.ofSeconds(12345),
                    type = Segment.Type.WORK
                ),
                Segment(
                    segmentId = 0,
                    taskId = 1,
                    startTime = Instant.parse("2025-04-17T18:31:04Z").plusSeconds(12345),
                    duration = null,
                    type = Segment.Type.SUSPEND
                )
            ),
            emptyList(),
            null,
        ),
        NewTask(
            2,
            "Project Plan",
            Instant.parse("2025-04-17T18:29:04Z"),
            2,
            Color.Companion.Blue,
            null,
            null
        ),
        NewTask(
            3,
            "Homework 99",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.White,
            null,
            null,
        ),
        NewTask(
            4,
            "Homework 99.5",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Cyan,
            null,
            null,
        ),
        NewTask(
            5,
            "Homework -1",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Black,
            null,
            null,
        ),
        NewTask(
            6,
            "Homework 100",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Red,
            null,
            null,
        ),
        NewTask(
            7,
            "Evil Homework 101",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Magenta,
            null,
            null,
        ),
        NewTask(
            8,
            "Super Homework 102",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Yellow,
            null,
            null,
        ),
        CompletedTask(
            taskId = 9,
            name = "Finalized Report",
            dueDate = Instant.parse("2025-04-18T10:00:00Z"),
            difficulty = 4,
            color = Color.Companion.DarkGray,
            userEstimate = Duration.ofHours(5),
            segments = listOf(
                Segment(
                    segmentId = 1,
                    taskId = 9,
                    startTime = Instant.parse("2025-04-18T09:00:00Z"),
                    duration = Duration.ofHours(2),
                    type = Segment.Type.WORK
                )
            ),
            markers = listOf(
                Marker(
                    markerId = 1,
                    taskId = 9,
                    startTime = Instant.parse("2025-04-18T09:30:00Z"),
                    label = "Research Phase"
                )
            ),
            profileId = null,
        )
    )

    val RELOADRUNNING = listOf(
        StartedTask(
            taskId = 1,
            name = "Running",
            dueDate = null,
            difficulty = 1,
            color = Color.Companion.Green,
            userEstimate = null,
            segments = listOf(
                Segment(
                    segmentId = 0,
                    taskId = 1,
                    startTime = Instant.now().minusSeconds(5 * 60 * 60 + 5 * 60),
                    duration = null,
                    type = Segment.Type.BREAK
                )
            ),
            markers = emptyList(),
            null,
        )
    )
}