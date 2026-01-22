package com.wordco.clockworkandroid.session_editor_feature.domain.util

import com.wordco.clockworkandroid.session_editor_feature.domain.model.UserEstimate
import java.time.Duration

fun Duration.toEstimate() : UserEstimate {
    return UserEstimate(
        minutes = (toMinutes() % 60).toInt(),
        hours = toHours().toInt()
    )
}