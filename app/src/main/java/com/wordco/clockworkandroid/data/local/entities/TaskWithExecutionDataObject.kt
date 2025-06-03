package com.wordco.clockworkandroid.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithExecutionDataObject(
    @Embedded val taskEntity: TaskEntity,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val segments: List<SegmentEntity>,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val markers: List<MarkerEntity>
    )
