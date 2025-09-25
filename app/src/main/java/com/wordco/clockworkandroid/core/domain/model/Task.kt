package com.wordco.clockworkandroid.core.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

sealed interface Task {
    val taskId: Long
    val profileId: Long?
    val name: String
    val dueDate: Instant?
    val difficulty: Int
    val color: Color
    val userEstimate: Duration?
    //val appEstimate: Duration

    sealed interface HasExecutionData : Task {
        val segments: List<Segment>
        val markers: List<Marker>

        val workTime: Duration
        val breakTime: Duration

        val startedAt: Instant
    }
}