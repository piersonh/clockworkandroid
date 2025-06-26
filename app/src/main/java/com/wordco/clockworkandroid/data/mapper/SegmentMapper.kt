package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.domain.model.Segment

fun SegmentEntity.toSegment() : Segment {
    return Segment(
        segmentId = segmentId,
        taskId = taskId,
        startTime = startTime,
        duration = duration
    )
}

fun Segment.toSegmentEntity() : SegmentEntity {
    return SegmentEntity(
        segmentId = segmentId,
        taskId = taskId,
        startTime = startTime,
        duration = duration
    )
}