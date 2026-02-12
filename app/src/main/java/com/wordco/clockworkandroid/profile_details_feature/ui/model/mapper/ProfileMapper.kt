package com.wordco.clockworkandroid.profile_details_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.profile_details_feature.ui.model.ProfileDisplayData

fun Profile.toProfileDisplayData(): ProfileDisplayData {
    val (complete, todo) = this.sessions.partition { it is CompletedTask }
    return ProfileDisplayData(
        name = this.name,
        color = this.color,
        todoSessions = todo.map { it.toTodoSessionListItem() },
        completeSessions = complete.map { (it as CompletedTask).toCompletedSessionListItem() }
    )
}