package com.wordco.clockworkandroid.core.ui.model

import androidx.annotation.DrawableRes

data class TopLevelDestination <T: Any> (
    val route: T,
    @param:DrawableRes val icon: Int,
    val label: String,
)