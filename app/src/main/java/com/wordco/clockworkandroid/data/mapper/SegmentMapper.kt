package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.SegmentTypeConverter.Companion.fromSegmentType
import com.wordco.clockworkandroid.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.domain.model.Segment
import com.wordco.clockworkandroid.data.local.SegmentTypeConverter.Companion.toSegmentType

fun SegmentEntity.toSegment() : Segment {
    return Segment(
        segmentId = segmentId,
        taskId = taskId,
        startTime = startTime,
        duration = duration,
        type = type.toSegmentType(),
    )
}

fun Segment.toSegmentEntity() : SegmentEntity {
    return SegmentEntity(
        segmentId = segmentId,
        taskId = taskId,
        startTime = startTime,
        duration = duration,
        type = type.fromSegmentType(),
    )
}