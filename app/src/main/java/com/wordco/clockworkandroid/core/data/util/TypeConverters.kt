package com.wordco.clockworkandroid.core.data.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import java.time.Duration
import java.time.Instant

fun fromDuration(duration: Duration?): Long? {
    return duration?.toMillis()
}

fun toDuration(millis: Long?): Duration? {
    return millis?.let { Duration.ofMillis(it) }
}

fun toInstant(value: Long): Instant {
    return Instant.ofEpochMilli(value)
}

fun fromInstant(date: Instant): Long {
    return date.toEpochMilli()
}

fun toOptionalInstant(value: Long?): Instant? {
    return value?.let { Instant.ofEpochMilli(it) }
}

fun fromOptionalInstant(date: Instant?): Long? {
    return date?.toEpochMilli()
}

fun fromColor(color: Color): Int {
    return color.toArgb()
}

fun toColor(color: Int): Color {
    return Color(color)
}

fun fromSegmentType(variant: Segment.Type): Int {
    return variant.ordinal
}

fun toSegmentType(ordinal: Int): Segment.Type {
    return Segment.Type.entries[ordinal]
}

fun Task.getStatus() : Int {
    return when (this) {
        is NewTask -> 0
        is StartedTask -> 1
        is CompletedTask -> 2
    }
}