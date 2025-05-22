package com.wordco.clockworkandroid.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.data.model.Task
import java.time.Instant

object DummyData {
    val TASKS = listOf(
        Task("Assignment", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Green),
        Task("Project Plan", Instant.parse("2025-04-17T18:29:04Z"), 2, Color.Blue),
        Task("Homework 99", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.White),
        Task("Homework 99.5", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Cyan),
        Task("Homework -1", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Black),
        Task("Homework 100", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Red),
        Task("Evil Homework 101", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Magenta),
        Task("Super Homework 102", Instant.parse("2025-04-17T18:29:04Z"), 3, Color.Yellow),
    )
}