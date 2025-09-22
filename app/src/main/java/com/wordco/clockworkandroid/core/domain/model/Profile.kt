package com.wordco.clockworkandroid.core.domain.model

import androidx.compose.ui.graphics.Color

data class Profile(
    val id: Long,
    val name: String,
    val color: Color,
    val defaultDifficulty: Int,
    val sessions: List<Task>,
)
