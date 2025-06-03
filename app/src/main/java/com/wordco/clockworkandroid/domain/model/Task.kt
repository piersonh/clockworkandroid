package com.wordco.clockworkandroid.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class Task(
    val taskId: Long,
    val name: String,
    val dueDate: Instant?,
    val difficulty: Int,
    val color: Color,
    val status: Status,
    val segments: List<Segment>,
    val markers: List<Marker>
) {
    enum class Status(i: Int) {
        RUNNING(0),
        SUSPENDED(1),
        NOT_STARTED(2),
        COMPLETED(3)
    }


    val workTime: Duration by lazy { Duration.ofMillis(0) }
    val breakTime: Duration by lazy { Duration.ofMillis(0) }

//    constructor(name: String, dueDate: Instant?, difficulty: Int, color: Color) : this(
//        0, name, dueDate, difficulty, color, Status.NOT_STARTED,
//        mutableListOf(),
//        mutableListOf()
//    )
}
