package com.wordco.clockworkandroid.profile_list_feature.domain.model

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.Task

data class Profile(
    val id: Long,
    val name: String,
    val color: Color,
    val sessions: List<Task>,
)
