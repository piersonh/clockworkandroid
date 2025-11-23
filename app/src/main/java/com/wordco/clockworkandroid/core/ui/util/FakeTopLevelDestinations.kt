package com.wordco.clockworkandroid.core.ui.util

import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.model.TopLevelDestination
import kotlinx.serialization.Serializable

@Serializable
data object FakeDestination

val FAKE_TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        route = FakeDestination::class,
        icon = R.drawable.statistics,
        label = "Statistics",
    ),
    TopLevelDestination(
        route = FakeDestination::class,
        icon = R.drawable.todo_list,
        label = "To-Do List",
    ),
    TopLevelDestination(
        route = FakeDestination::class,
        icon = R.drawable.templates,
        label = "Templates",
    ),
)