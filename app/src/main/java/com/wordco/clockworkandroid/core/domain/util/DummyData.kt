package com.wordco.clockworkandroid.core.domain.util

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile

object DummyData {
    val SESSIONS = listOf(
        NewTask(
            taskId = 1,
            profileId = 1,
            name = "Session 1",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(0f, 1f, 1f),
            userEstimate = null,
        ),
        NewTask(
            taskId = 2,
            profileId = 1,
            name = "Session 2",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(0f, 1f, 1f),
            userEstimate = null,
        ),
        NewTask(
            taskId = 3,
            profileId = 1,
            name = "Session 3",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(0f, 1f, 1f),
            userEstimate = null,
        ),
        NewTask(
            taskId = 4,
            profileId = 2,
            name = "Session 4",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(120f, 1f, 1f),
            userEstimate = null,
        ),
        NewTask(
            taskId = 5,
            profileId = 2,
            name = "Session 5",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(120f, 1f, 1f),
            userEstimate = null,
        ),
        NewTask(
            taskId = 6,
            profileId = 3,
            name = "Session 6",
            dueDate = null,
            difficulty = 1,
            color = Color.hsv(240f, 1f, 1f),
            userEstimate = null,
        ),
    )


    val PROFILES = listOf(
        Profile(
            id = 1,
            name = "Profile 1",
            color = Color.hsv(0f, 1f, 1f),
            defaultDifficulty = 0,
            sessions = SESSIONS.filter { it.profileId == 1L }
        ),
        Profile(
            id = 2,
            name = "Profile 2",
            color = Color.hsv(120f, 1f, 1f),
            defaultDifficulty = 0,
            sessions = SESSIONS.filter { it.profileId == 2L }
        ),
        Profile(
            id = 3,
            name = "Profile 3",
            color = Color.hsv(240f, 1f, 1f),
            defaultDifficulty = 0,
            sessions = SESSIONS.filter { it.profileId == 3L }
        ),
    )
}