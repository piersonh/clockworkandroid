package com.wordco.clockworkandroid.core.data.util

import androidx.compose.ui.graphics.Color
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
            emptyList()
        ),
        NewTask(
            2,
            "Project Plan",
            Instant.parse("2025-04-17T18:29:04Z"),
            2,
            Color.Companion.Blue,
            null
        ),
        NewTask(
            3,
            "Homework 99",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.White,
            null
        ),
        NewTask(
            4,
            "Homework 99.5",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Cyan,
            null
        ),
        NewTask(
            5,
            "Homework -1",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Black,
            null
        ),
        NewTask(
            6,
            "Homework 100",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Red,
            null
        ),
        NewTask(
            7,
            "Evil Homework 101",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Magenta,
            null
        ),
        NewTask(
            8,
            "Super Homework 102",
            Instant.parse("2025-04-17T18:29:04Z"),
            3,
            Color.Companion.Yellow,
            null
        ),
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
            markers = emptyList()
        )
    )
}