package com.wordco.clockworkandroid.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.SegmentType
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

fun fromTaskStatus(status: ExecutionStatus): Int {
    return status.ordinal
}

fun toTaskStatus(status: Int): ExecutionStatus {
    return ExecutionStatus.entries[status]
}

fun fromSegmentType(variant: SegmentType): Int {
    return variant.ordinal
}

fun toSegmentType(ordinal: Int): SegmentType {
    return SegmentType.entries[ordinal]
}