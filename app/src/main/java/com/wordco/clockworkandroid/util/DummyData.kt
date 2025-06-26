package com.wordco.clockworkandroid.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import java.time.Instant

object DummyData {
    val TASKS = listOf(
        Task(0,"Assignment", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Green, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Project Plan", Instant.parse("2025-04-17T18:29:04Z"), 2, Color.Blue, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Homework 99", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.White, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Homework 99.5", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Cyan, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Homework -1", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Black, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Homework 100", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Red, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Evil Homework 101", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Magenta, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
        Task(0,"Super Homework 102", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Yellow, ExecutionStatus.NOT_STARTED, emptyList(), emptyList()),
    )
}