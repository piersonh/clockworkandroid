package com.wordco.clockworkandroid.profile_list_feature.domain.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.profile_list_feature.domain.model.Profile

object DummyData {
    val PROFILES = listOf(
        Profile(
            id = 1,
            name = "Profile 1",
            color = Color.hsv(0f, 1f, 1f),
            sessions = listOf(
                NewTask(
                    taskId = 1,
                    name = "",
                    dueDate = null,
                    difficulty = 1,
                    color = Color.hsv(0f, 1f, 1f),
                    userEstimate = null,
                )
            )
        ),
        Profile(
            id = 2,
            name = "Profile 2",
            color = Color.hsv(120f, 1f, 1f),
            sessions = listOf()
        ),
        Profile(
            id = 3,
            name = "Profile 3",
            color = Color.hsv(240f, 1f, 1f),
            sessions = listOf()
        ),
    )
}