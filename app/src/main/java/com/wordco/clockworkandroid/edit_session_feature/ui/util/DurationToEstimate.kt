package com.wordco.clockworkandroid.edit_session_feature.ui.util

import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.Duration

fun Duration.toEstimate() : UserEstimate {
    return UserEstimate(
        minutes = (toMinutes() % 60).toInt(),
        hours = toHours().toInt()
    )
}