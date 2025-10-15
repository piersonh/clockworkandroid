package com.wordco.clockworkandroid.edit_session_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalTime
import kotlin.random.Random

fun getFieldDefaults(
    profile: Profile?
) =  SessionFormUiState (
    taskName = profile?.let { profile ->
        // TODO replace this with a truth from the database so that the quantity
        //  is retained after sessions are deleted or switched profiles
        "${profile.name} ${profile.sessions.size + 1}"
    } ?: "",
    profileName = profile?.name,
    colorSliderPos =
        profile?.color?.hue()?.div(360) ?: Random.Default.nextFloat(),
    difficulty = profile?.defaultDifficulty?.toFloat() ?: 0f,
    dueDate = null,
    dueTime = LocalTime.of(23,59),
    estimate = UserEstimate(
        minutes = 15,
        hours = 0
    ),
)