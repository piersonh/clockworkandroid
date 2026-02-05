package com.wordco.clockworkandroid.profile_session_list_feature.ui.model

import androidx.compose.runtime.Composable

data class TabbedScreenItem (
    val label: String,
    val screen: @Composable () -> Unit
)