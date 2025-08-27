package com.wordco.clockworkandroid.core.data.local.entities.mapper

import com.wordco.clockworkandroid.core.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.core.data.util.fromDuration
import com.wordco.clockworkandroid.core.data.util.fromInstant
import com.wordco.clockworkandroid.core.data.util.fromSegmentType
import com.wordco.clockworkandroid.core.data.util.toDuration
import com.wordco.clockworkandroid.core.data.util.toInstant
import com.wordco.clockworkandroid.core.data.util.toSegmentType
import com.wordco.clockworkandroid.core.domain.model.Segment

fun SegmentEntity.toSegment() : Segment {
    return Segment(
        segmentId = segmentId,
        taskId = taskId,
        startTime = toInstant(startTime),
        duration = toDuration(duration),
        type = toSegmentType(type),
    )
}

fun Segment.toSegmentEntity() : SegmentEntity {
    return SegmentEntity(
        segmentId = segmentId,
        taskId = taskId,
        startTime = fromInstant(startTime),
        duration = fromDuration(duration),
        type = fromSegmentType(type),
    )
}