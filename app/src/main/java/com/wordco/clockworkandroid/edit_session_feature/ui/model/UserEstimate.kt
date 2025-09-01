package com.wordco.clockworkandroid.edit_session_feature.ui.model

import java.time.Duration

data class UserEstimate (
    val minutes: Int,
    val hours: Int,
) {
    companion object {
        fun fromDuration(data: Duration?): UserEstimate? {
            if (data == null){
                return null
            }
            return UserEstimate(
                minutes = (data.toMinutes() % 60).toInt(),
                hours = data.toHours().toInt()
            )
        }
    }
}