package com.wordco.clockworkandroid.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Segment
import com.wordco.clockworkandroid.domain.model.SegmentType
import com.wordco.clockworkandroid.domain.model.Task
import java.time.Duration
import java.time.Instant

object DummyData {
    val TASKS = listOf(
        Task(1,"Assignment", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Green, ExecutionStatus.SUSPENDED, listOf(
            Segment(
                segmentId = 0,
                taskId = 1,
                startTime = Instant.parse("2025-04-17T18:31:04Z"),
                duration = Duration.ofSeconds(12345),
                type = SegmentType.WORK
            )), emptyList()),
        Task(2,"Project Plan", Instant.parse("2025-04-17T18:29:04Z"), 2, Color.Blue, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(3,"Homework 99", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.White, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(4,"Homework 99.5", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Cyan, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(5,"Homework -1", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Black, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(6,"Homework 100", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Red, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(7,"Evil Homework 101", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Magenta, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(8,"Super Homework 102", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Yellow, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
    )
}