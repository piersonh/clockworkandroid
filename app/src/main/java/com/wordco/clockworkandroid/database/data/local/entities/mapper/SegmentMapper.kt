package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.database.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.database.data.util.fromInstant
import com.wordco.clockworkandroid.database.data.util.fromOptionalDuration
import com.wordco.clockworkandroid.database.data.util.fromSegmentType
import com.wordco.clockworkandroid.database.data.util.toInstant
import com.wordco.clockworkandroid.database.data.util.toOptionalDuration
import com.wordco.clockworkandroid.database.data.util.toSegmentType

fun SegmentEntity.toSegment() : Segment {
    return Segment(
        segmentId = segmentId,
        taskId = taskId,
        startTime = toInstant(startTime),
        duration = toOptionalDuration(duration),
        type = toSegmentType(type),
    )
}

fun Segment.toSegmentEntity() : SegmentEntity {
    return SegmentEntity(
        segmentId = segmentId,
        taskId = taskId,
        startTime = fromInstant(startTime),
        duration = fromOptionalDuration(duration),
        type = fromSegmentType(type),
    )
}