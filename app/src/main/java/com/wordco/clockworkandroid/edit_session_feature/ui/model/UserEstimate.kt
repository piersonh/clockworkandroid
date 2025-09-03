package com.wordco.clockworkandroid.edit_session_feature.ui.model

import java.time.Duration

data class UserEstimate (
    val minutes: Int,
    val hours: Int,
) {

    fun toDuration() : Duration {
        return Duration.ofHours(hours.toLong())
            .plusMinutes(minutes.toLong())
    }
}

fun Duration.toEstimate() : UserEstimate {
    return UserEstimate(
        minutes = (toMinutes() % 60).toInt(),
        hours = toHours().toInt()
    )
}