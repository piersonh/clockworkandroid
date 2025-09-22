package com.wordco.clockworkandroid.edit_session_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.ui.EditTaskFormUiState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

fun getFieldDefaults(
    profile: Profile?
) = object : EditTaskFormUiState {
    override val taskName: String = profile?.let { profile ->
        // TODO replace this with a truth from the database so that the quantity
        //  is retained after sessions are deleted or switched profiles
        "${profile.name} ${profile.sessions.size + 1}"
    } ?: ""
    override val profileName: String? = profile?.name
    override val colorSliderPos: Float =
        profile?.color?.hue()?.div(360) ?: Random.Default.nextFloat()
    override val difficulty: Float = profile?.defaultDifficulty?.toFloat() ?: 0f
    override val dueDate: LocalDate? = null
    override val dueTime: LocalTime? = LocalTime.of(23,59)
    override val currentModal: PickerModal? = null
    override val estimate: UserEstimate? = UserEstimate(
        minutes = 15,
        hours = 0
    )
}