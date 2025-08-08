package com.wordco.clockworkandroid.ui

import kotlinx.serialization.Serializable

// See
// https://developer.android.com/guide/navigation/design#compose-arguments
// https://developer.android.com/guide/navigation/use-graph/navigate#composable
// https://developer.android.com/develop/ui/compose/navigation <- for view model arg passing

sealed class PageRoutes () {

    @Serializable
    object TaskList
    @Serializable
    object NewTask
    @Serializable
    data class Timer(val id: Long)
    @Serializable
    data class TaskComplete(val id: Long)
}

