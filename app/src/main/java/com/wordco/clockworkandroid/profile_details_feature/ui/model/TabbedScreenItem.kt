package com.wordco.clockworkandroid.profile_details_feature.ui.model

import androidx.compose.runtime.Composable

data class TabbedScreenItem (
    val label: String,
    val screen: @Composable () -> Unit
)