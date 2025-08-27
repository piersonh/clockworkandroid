package com.wordco.clockworkandroid.core.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

sealed interface Task {
    val taskId: Long
    val name: String
    val dueDate: Instant?
    val difficulty: Int
    val color: Color
    val userEstimate: Duration?
    //val appEstimate: Duration
}