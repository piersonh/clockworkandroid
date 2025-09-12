package com.wordco.clockworkandroid.core.ui.util

import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.model.TopLevelDestination
import kotlinx.serialization.Serializable

@Serializable
data object FakeDestination

val FAKE_TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        route = FakeDestination::class,
        icon = R.drawable.user,
        label = "Statistics",
    ),
    TopLevelDestination(
        route = FakeDestination::class,
        icon = R.drawable.cal,
        label = "Sessions",
    ),
    TopLevelDestination(
        route = FakeDestination::class,
        icon = R.drawable.star,
        label = "Profiles",
    ),
)