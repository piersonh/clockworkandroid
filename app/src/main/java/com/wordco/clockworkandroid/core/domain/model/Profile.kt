package com.wordco.clockworkandroid.core.domain.model

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.Task

data class Profile(
    val id: Long,
    val name: String,
    val color: Color,
    val sessions: List<Task>,
)
