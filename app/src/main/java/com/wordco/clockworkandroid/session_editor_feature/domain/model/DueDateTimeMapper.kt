package com.wordco.clockworkandroid.session_editor_feature.domain.model

import java.time.LocalDateTime

fun DueDateTime.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.of(date, time)
}

fun LocalDateTime.toDueDateTime(): DueDateTime {
    return DueDateTime(
        date = toLocalDate(),
        time = toLocalTime(),
    )
}