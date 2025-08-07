package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.TaskWithExecutionDataObject
import com.wordco.clockworkandroid.domain.model.SegmentType
import com.wordco.clockworkandroid.domain.model.Task
import java.time.Duration

fun TaskWithExecutionDataObject.toTask() : Task {
    val segments = segments.map { segmentEntity -> segmentEntity.toSegment() }
    val workTime = segments.filter { it.type == SegmentType.WORK }
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration) }
    val breakTime = segments.filter { it.type == SegmentType.BREAK }
        .fold(Duration.ZERO) { acc, seg -> acc.plus(seg.duration) }
    return Task(
        taskId = taskEntity.taskId,
        name = taskEntity.name,
        dueDate = taskEntity.dueDate,
        difficulty = taskEntity.difficulty,
        color = taskEntity.color,
        status = taskEntity.status,
        segments = segments,
        markers = markers.map { markerEntity -> markerEntity.toMarker() },
        workTime = workTime,
        breakTime = breakTime
    )
}