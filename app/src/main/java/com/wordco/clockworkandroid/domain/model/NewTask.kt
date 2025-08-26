package com.wordco.clockworkandroid.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class NewTask(
    override val taskId: Long,
    override val name: String,
    override val dueDate: Instant?,
    override val difficulty: Int,
    override val color: Color,
    override val userEstimate: Duration?
) : Task
