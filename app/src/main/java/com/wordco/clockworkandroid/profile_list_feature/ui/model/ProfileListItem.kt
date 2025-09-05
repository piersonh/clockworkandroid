package com.wordco.clockworkandroid.profile_list_feature.ui.model

import androidx.compose.ui.graphics.Color

data class ProfileListItem(
    val id: Long,
    val name: String,
    val color: Color,
    val numSessions: Int,
)
