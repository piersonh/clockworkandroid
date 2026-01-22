package com.wordco.clockworkandroid.session_editor_feature.domain.model

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