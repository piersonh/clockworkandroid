package com.wordco.clockworkandroid.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Embedded
import androidx.room.Relation
import java.time.Duration
import java.time.Instant

data class Task(
    @Embedded val taskProperties: TaskProperties,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val segments: List<Segment>,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val markers: List<Marker>
) {
    enum class Status(i: Int) {
        RUNNING(0),
        SUSPENDED(1),
        NOT_STARTED(2),
        COMPLETED(3)
    }

    val name: String
        get() = taskProperties.name
    val dueDate: Instant?
        get() = taskProperties.dueDate
    val difficulty: Int
        get() = taskProperties.difficulty
    val color: Color
        get() = taskProperties.color
    val status: Status
        get() = taskProperties.status

    val workTime: Duration by lazy { Duration.ofMillis(0) }
    val breakTime: Duration by lazy { Duration.ofMillis(0) }

    constructor(name: String, dueDate: Instant?, difficulty: Int, color: Color) : this(
        TaskProperties(0, name, dueDate, difficulty, color, Status.NOT_STARTED),
        mutableListOf(),
        mutableListOf()
    )
}
