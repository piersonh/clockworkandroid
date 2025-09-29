package com.wordco.clockworkandroid.database.data.util

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.database.data.local.entities.MarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.ProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toMarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toProfileEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toSegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toTaskEntity
import java.time.Duration
import java.time.Instant

data class UserDataPackage(
    val sessions: List<TaskEntity>,
    val segments: List<SegmentEntity>,
    val markers: List<MarkerEntity>,
    val profiles: List<ProfileEntity>
) {
    companion object {
        fun factory(
            profiles: List<Profile>,
            orphanSessions: List<Task>
        ) : UserDataPackage {
            var profileIdInc = 0L
            var sessionIdInc = 0L
            var segmentIdInc = 0L
            var markerIdInc = 0L
            val profiles = profiles.map { profile ->
                val profileId = ++profileIdInc
                profile.copy(
                    id = profileId,
                    sessions = profile.sessions.map { session ->
                        val sessionId = ++sessionIdInc
                        when (session) {
                            is NewTask -> session.copy(
                                taskId = sessionId,
                                profileId = profileId,
                            )
                            is CompletedTask -> session.copy(
                                taskId = sessionId,
                                profileId = profileId,
                                segments = session.segments.map { segment ->
                                    segment.copy(
                                        segmentId = (++segmentIdInc),
                                        taskId = sessionId
                                    )
                                },
                                markers = session.markers.map { marker ->
                                    marker.copy(
                                        markerId = (++markerIdInc),
                                        taskId = sessionId
                                    )
                                }
                            )
                            is StartedTask -> session.copy(
                                taskId = sessionId,
                                profileId = profileId,
                                segments = session.segments.map { segment ->
                                    segment.copy(
                                        segmentId = (++segmentIdInc),
                                        taskId = sessionId
                                    )
                                },
                                markers = session.markers.map { marker ->
                                    marker.copy(
                                        markerId = (++markerIdInc),
                                        taskId = sessionId
                                    )
                                }
                            )
                        }
                    }
                )
            }

            val sessions = buildList {
                profiles.forEach {
                    addAll(it.sessions)
                }
                orphanSessions.forEach { session ->
                    val sessionId = ++sessionIdInc
                    when (session) {
                        is NewTask -> session.copy(
                            taskId = sessionId,
                            profileId = null,
                        )
                        is CompletedTask -> session.copy(
                            taskId = sessionId,
                            profileId = null,
                            segments = session.segments.map { segment ->
                                segment.copy(
                                    segmentId = (++segmentIdInc),
                                    taskId = sessionId
                                )
                            },
                            markers = session.markers.map { marker ->
                                marker.copy(
                                    markerId = (++markerIdInc),
                                    taskId = sessionId
                                )
                            }
                        )
                        is StartedTask -> session.copy(
                            taskId = sessionId,
                            profileId = null,
                            segments = session.segments.map { segment ->
                                segment.copy(
                                    segmentId = (++segmentIdInc),
                                    taskId = sessionId
                                )
                            },
                            markers = session.markers.map { marker ->
                                marker.copy(
                                    markerId = (++markerIdInc),
                                    taskId = sessionId
                                )
                            }
                        )
                    }.let {
                        add(it)
                    }
                }
            }

            val segments = buildList {
                sessions.forEach {
                    if (it is Task.HasExecutionData) {
                        addAll(it.segments)
                    }
                }
            }

            val markers = buildList {
                sessions.forEach {
                    if (it is Task.HasExecutionData) {
                        addAll(it.markers)
                    }
                }
            }

            return UserDataPackage(
                sessions = sessions.map(Task::toTaskEntity),
                segments = segments.map(Segment::toSegmentEntity),
                markers = markers.map(Marker::toMarkerEntity),
                profiles = profiles.map(Profile::toProfileEntity)
            )
        }
    }
}


object DummyData {

    val package0_empty by lazy {
        UserDataPackage.factory(
            profiles = emptyList(),
            orphanSessions = emptyList()
        )
    }

    val package1_sillyTasks by lazy {
        UserDataPackage.factory(
            profiles = emptyList(),
            orphanSessions = listOf(
                StartedTask(
                    0,
                    "Assignment",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Green,
                    null,
                    listOf(
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-17T18:31:04Z"),
                            duration = Duration.ofSeconds(12345),
                            type = Segment.Type.WORK
                        ),
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-17T18:31:04Z").plusSeconds(12345),
                            duration = null,
                            type = Segment.Type.SUSPEND
                        )
                    ),
                    emptyList(),
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    0,
                    "Project Plan",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    2,
                    Color.Companion.Blue,
                    null,
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    0,
                    "Homework 99",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.White,
                    null,
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    0,
                    "Homework 99.5",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Cyan,
                    null,
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    0,
                    "Homework -1",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Black,
                    null,
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    0,
                    "Homework 100",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Red,
                    null,
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    0,
                    "Evil Homework 101",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Magenta,
                    null,
                    null,
                    appEstimate = null,
                ),
                NewTask(
                    8,
                    "Super Homework 102",
                    Instant.parse("2025-04-17T18:29:04Z"),
                    3,
                    Color.Companion.Yellow,
                    null,
                    null,
                    appEstimate = null,
                ),
                CompletedTask(
                    taskId = 0,
                    name = "Finalized Report",
                    dueDate = Instant.parse("2025-04-18T10:00:00Z"),
                    difficulty = 4,
                    color = Color.Companion.DarkGray,
                    userEstimate = Duration.ofHours(5),
                    segments = listOf(
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-18T09:00:00Z"),
                            duration = Duration.ofHours(2),
                            type = Segment.Type.WORK
                        )
                    ),
                    markers = listOf(
                        Marker(
                            markerId = 0,
                            taskId = 0,
                            startTime = Instant.parse("2025-04-18T09:30:00Z"),
                            label = "Research Phase"
                        )
                    ),
                    profileId = null,
                    appEstimate = null,
                )
            )
        )
    }

    val package2_reloadRunning by lazy {
        UserDataPackage.factory(
            profiles = emptyList(),
            orphanSessions = listOf(
                StartedTask(
                    taskId = 0,
                    name = "Running",
                    dueDate = null,
                    difficulty = 1,
                    color = Color.Green,
                    userEstimate = null,
                    segments = listOf(
                        Segment(
                            segmentId = 0,
                            taskId = 0,
                            startTime = Instant.now().minusSeconds(5 * 60 * 60 + 5 * 60),
                            duration = null,
                            type = Segment.Type.BREAK
                        )
                    ),
                    markers = emptyList(),
                    null,
                    appEstimate = null,
                )
            )
        )
    }
    
    val package3_csStudentHistory by lazy {
        UserDataPackage.factory(
            profiles = listOf(
                Profile(
                    id = 0,
                    name = "Homework",
                    color = Color.Green,
                    defaultDifficulty = 1,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 1,
                            name = "Algorithms HW 1 - Big O Notation",
                            dueDate = Instant.parse("2025-09-08T23:59:59Z"),
                            difficulty = 2,
                            color = Color.Green,
                            userEstimate = Duration.ofHours(3),
                            segments = listOf(
                                Segment(
                                    segmentId = 1,
                                    taskId = 1,
                                    startTime = Instant.parse("2025-09-05T14:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(15),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 2, // Homework Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 6,
                            name = "Theory of Computation PSet 2",
                            dueDate = Instant.parse("2025-09-22T23:59:59Z"),
                            difficulty = 3,
                            color = Color.Green,
                            userEstimate = Duration.ofHours(5),
                            segments = listOf(
                                Segment(
                                    segmentId = 11,
                                    taskId = 6,
                                    startTime = Instant.parse("2025-09-19T15:30:00Z"),
                                    duration = Duration.ofHours(4).plusMinutes(40),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 2, // Homework Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 10,
                            name = "Algorithms HW 2 - Sorting Algorithms",
                            dueDate = Instant.parse("2025-09-12T23:59:59Z"),
                            difficulty = 2,
                            color = Color.Green,
                            userEstimate = Duration.ofHours(4),
                            segments = listOf(
                                Segment(
                                    segmentId = 15,
                                    taskId = 10,
                                    startTime = Instant.parse("2025-09-11T16:00:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(50),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 16,
                                    taskId = 10,
                                    startTime = Instant.parse("2025-09-11T17:50:00Z"),
                                    duration = Duration.ofMinutes(15),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 17,
                                    taskId = 10,
                                    startTime = Instant.parse("2025-09-11T18:05:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(20),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 2, // Homework Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 18,
                            name = "Theory of Computation PSet 3",
                            dueDate = Instant.parse("2025-09-24T23:59:59Z"),
                            difficulty = 3,
                            color = Color.Green,
                            userEstimate = Duration.ofHours(6),
                            segments = listOf(
                                Segment(
                                    segmentId = 33,
                                    taskId = 18,
                                    startTime = Instant.parse("2025-09-23T18:00:00Z"),
                                    duration = Duration.ofHours(5).plusMinutes(40),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 2, // Homework Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 19,
                            name = "Algorithms HW 4 - Dynamic Programming",
                            dueDate = Instant.parse("2025-10-06T23:59:59Z"),
                            difficulty = 3,
                            color = Color.Green,
                            userEstimate = Duration.ofHours(4), // Underestimated
                            segments = listOf(
                                Segment(
                                    segmentId = 34,
                                    taskId = 19,
                                    startTime = Instant.parse("2025-10-04T13:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(15),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 35,
                                    taskId = 19,
                                    startTime = Instant.parse("2025-10-05T18:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(50),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 2, // Homework Profile
                            appEstimate = null
                        ),
                    )
                ),
                Profile(
                    id = 0,
                    name = "Project",
                    color = Color.Magenta,
                    defaultDifficulty = 2,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 2,
                            name = "Data Structures Project - Part 1 (Hash Table)",
                            dueDate = Instant.parse("2025-09-15T23:59:59Z"),
                            difficulty = 3,
                            color = Color.Magenta,
                            userEstimate = Duration.ofHours(8),
                            segments = listOf(
                                Segment(
                                    segmentId = 2,
                                    taskId = 2,
                                    startTime = Instant.parse("2025-09-10T18:30:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(45),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 3,
                                    taskId = 2,
                                    startTime = Instant.parse("2025-09-11T20:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(5),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 4,
                                    taskId = 2,
                                    startTime = Instant.parse("2025-09-14T11:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(30),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 5, // Project Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 9,
                            name = "Fix Bugs in Personal Website",
                            dueDate = null,
                            difficulty = 2,
                            color = Color.Magenta,
                            userEstimate = Duration.ofHours(2),
                            segments = listOf(
                                Segment(
                                    segmentId = 14,
                                    taskId = 9,
                                    startTime = Instant.parse("2025-09-28T16:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(10),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 5, // Project Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 12,
                            name = "Data Structures Project - Part 2 (Graph Traversal)",
                            dueDate = Instant.parse("2025-09-29T23:59:59Z"),
                            difficulty = 4,
                            color = Color.Magenta,
                            userEstimate = Duration.ofHours(15),
                            segments = listOf(
                                Segment(
                                    segmentId = 19,
                                    taskId = 12,
                                    startTime = Instant.parse("2025-09-19T14:00:00Z"),
                                    duration = Duration.ofHours(3),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 20,
                                    taskId = 12,
                                    startTime = Instant.parse("2025-09-21T11:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(30),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 21,
                                    taskId = 12,
                                    startTime = Instant.parse("2025-09-21T13:30:00Z"),
                                    duration = Duration.ofMinutes(25),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 22,
                                    taskId = 12,
                                    startTime = Instant.parse("2025-09-21T13:55:00Z"),
                                    duration = Duration.ofHours(2),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 23,
                                    taskId = 12,
                                    startTime = Instant.parse("2025-09-26T18:00:00Z"),
                                    duration = Duration.ofHours(4).plusMinutes(45),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 5, // Project Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 17,
                            name = "Personal Project: Database Schema Design",
                            dueDate = null,
                            difficulty = 2,
                            color = Color.Magenta,
                            userEstimate = Duration.ofHours(3),
                            segments = listOf(
                                Segment(
                                    segmentId = 30,
                                    taskId = 17,
                                    startTime = Instant.parse("2025-09-14T15:00:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(30),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 31,
                                    taskId = 17,
                                    startTime = Instant.parse("2025-09-14T16:30:00Z"),
                                    duration = Duration.ofMinutes(10),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 32,
                                    taskId = 17,
                                    startTime = Instant.parse("2025-09-14T16:40:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(15),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 5, // Project Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 22,
                            name = "Debug Null Pointer Exception in DS Project",
                            dueDate = null,
                            difficulty = 2,
                            color = Color.Magenta,
                            userEstimate = Duration.ofMinutes(45), // Classic "quick fix" underestimate
                            segments = listOf(
                                Segment(
                                    segmentId = 45,
                                    taskId = 22,
                                    startTime = Instant.parse("2025-10-08T22:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(35),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 5, // Project Profile
                            appEstimate = null
                        ),
                    )
                ),
                Profile(
                    id = 0,
                    name = "Reading",
                    color = Color.Yellow,
                    defaultDifficulty = 0,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 3,
                            name = "Read OS Textbook Ch. 3-4 (Processes & Threads)",
                            dueDate = null,
                            difficulty = 1,
                            color = Color.Yellow,
                            userEstimate = Duration.ofHours(2),
                            segments = listOf(
                                Segment(
                                    segmentId = 5,
                                    taskId = 3,
                                    startTime = Instant.parse("2025-09-12T09:15:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(50),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 4, // Reading Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 15,
                            name = "Read 'Clean Code' Ch. 2-3",
                            dueDate = null,
                            difficulty = 1,
                            color = Color.Yellow,
                            userEstimate = Duration.ofHours(1),
                            segments = listOf(
                                Segment(
                                    segmentId = 28,
                                    taskId = 15,
                                    startTime = Instant.parse("2025-09-18T21:00:00Z"),
                                    duration = Duration.ofMinutes(55),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 4, // Reading Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 24,
                            name = "Read Distributed Systems Paper",
                            dueDate = null,
                            difficulty = 3,
                            color = Color.Yellow,
                            userEstimate = Duration.ofMinutes(90), // Underestimated density of paper
                            segments = listOf(
                                Segment(
                                    segmentId = 47,
                                    taskId = 24,
                                    startTime = Instant.parse("2025-10-13T10:00:00Z"),
                                    duration = Duration.ofHours(1),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 48,
                                    taskId = 24,
                                    startTime = Instant.parse("2025-10-13T11:00:00Z"),
                                    duration = Duration.ofMinutes(15),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 49,
                                    taskId = 24,
                                    startTime = Instant.parse("2025-10-13T11:15:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(30),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 4, // Reading Profile
                            appEstimate = null
                        ),
                    )
                ),
                Profile(
                    id = 0,
                    name = "Studying",
                    color = Color.Blue,
                    defaultDifficulty = 4,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 4,
                            name = "Study for Discrete Math Quiz 1",
                            dueDate = Instant.parse("2025-09-18T10:00:00Z"),
                            difficulty = 2,
                            color = Color.Yellow,
                            userEstimate = Duration.ofHours(4),
                            segments = listOf(
                                Segment(
                                    segmentId = 6,
                                    taskId = 4,
                                    startTime = Instant.parse("2025-09-17T16:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(10),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 7,
                                    taskId = 4,
                                    startTime = Instant.parse("2025-09-17T20:30:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(40),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 3, // Studying Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 8,
                            name = "Review Lecture Notes - Week 4",
                            dueDate = null,
                            difficulty = 0,
                            color = Color.Yellow,
                            userEstimate = Duration.ofMinutes(90),
                            segments = listOf(
                                Segment(
                                    segmentId = 13,
                                    taskId = 8,
                                    startTime = Instant.parse("2025-09-27T12:00:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(20),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 3, // Studying Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 13,
                            name = "Study for OS Midterm",
                            dueDate = Instant.parse("2025-10-02T11:00:00Z"),
                            difficulty = 3,
                            color = Color.Yellow,
                            userEstimate = Duration.ofHours(6),
                            segments = listOf(
                                Segment(
                                    segmentId = 24,
                                    taskId = 13,
                                    startTime = Instant.parse("2025-09-27T13:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(5),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 25,
                                    taskId = 13,
                                    startTime = Instant.parse("2025-09-27T15:05:00Z"),
                                    duration = Duration.ofMinutes(20),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 26,
                                    taskId = 13,
                                    startTime = Instant.parse("2025-09-27T15:25:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(30),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 3, // Studying Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 21,
                            name = "Review for Algorithms Midterm",
                            dueDate = Instant.parse("2025-10-17T13:00:00Z"),
                            difficulty = 3,
                            color = Color.Yellow,
                            userEstimate = Duration.ofHours(6), // Underestimated
                            segments = listOf(
                                Segment(
                                    segmentId = 41,
                                    taskId = 21,
                                    startTime = Instant.parse("2025-10-15T16:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(30),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 42,
                                    taskId = 21,
                                    startTime = Instant.parse("2025-10-16T18:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(15),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 43,
                                    taskId = 21,
                                    startTime = Instant.parse("2025-10-16T20:15:00Z"),
                                    duration = Duration.ofMinutes(20),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 44,
                                    taskId = 21,
                                    startTime = Instant.parse("2025-10-16T20:35:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(55),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 3, // Studying Profile
                            appEstimate = null
                        ),
                    )
                ),
                Profile(
                    id = 0,
                    name = "Programming Assignment",
                    color = Color.Red,
                    defaultDifficulty = 4,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 5,
                            name = "Operating Systems Lab 2 - Shell Implementation",
                            dueDate = Instant.parse("2025-09-26T23:59:59Z"),
                            difficulty = 4,
                            color = Color.Blue,
                            userEstimate = Duration.ofHours(12),
                            segments = listOf(
                                Segment(
                                    segmentId = 8,
                                    taskId = 5,
                                    startTime = Instant.parse("2025-09-20T13:00:00Z"),
                                    duration = Duration.ofHours(4),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 9,
                                    taskId = 5,
                                    startTime = Instant.parse("2025-09-22T19:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(30),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 10,
                                    taskId = 5,
                                    startTime = Instant.parse("2025-09-25T17:00:00Z"),
                                    duration = Duration.ofHours(5).plusMinutes(15),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 1, // Programming Assignment Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 11,
                            name = "Set up Docker for OS Class",
                            dueDate = null,
                            difficulty = 1,
                            color = Color.Blue,
                            userEstimate = Duration.ofMinutes(90),
                            segments = listOf(
                                Segment(
                                    segmentId = 18,
                                    taskId = 11,
                                    startTime = Instant.parse("2025-09-02T19:00:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(15),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 1, // Programming Assignment Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 20,
                            name = "OS Lab 3 - Virtual Memory",
                            dueDate = Instant.parse("2025-10-15T23:59:59Z"),
                            difficulty = 4,
                            color = Color.Blue,
                            userEstimate = Duration.ofHours(10), // Significantly underestimated
                            segments = listOf(
                                Segment(
                                    segmentId = 36,
                                    taskId = 20,
                                    startTime = Instant.parse("2025-10-10T19:00:00Z"),
                                    duration = Duration.ofHours(4).plusMinutes(5),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 37,
                                    taskId = 20,
                                    startTime = Instant.parse("2025-10-12T14:30:00Z"),
                                    duration = Duration.ofHours(3),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 38,
                                    taskId = 20,
                                    startTime = Instant.parse("2025-10-12T17:30:00Z"),
                                    duration = Duration.ofMinutes(30),
                                    type = Segment.Type.BREAK
                                ),
                                Segment(
                                    segmentId = 39,
                                    taskId = 20,
                                    startTime = Instant.parse("2025-10-12T18:00:00Z"),
                                    duration = Duration.ofHours(2).plusMinutes(40),
                                    type = Segment.Type.WORK
                                ),
                                Segment(
                                    segmentId = 40,
                                    taskId = 20,
                                    startTime = Instant.parse("2025-10-14T20:00:00Z"),
                                    duration = Duration.ofHours(4).plusMinutes(10),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 1, // Programming Assignment Profile
                            appEstimate = null
                        ),
                    )
                ),
                Profile(
                    id = 0,
                    name = "Career/Admin",
                    color = Color.Green,
                    defaultDifficulty = 3,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 7,
                            name = "Update Resume for Career Fair",
                            dueDate = null,
                            difficulty = 1,
                            color = Color.Gray,
                            userEstimate = Duration.ofHours(1),
                            segments = listOf(
                                Segment(
                                    segmentId = 12,
                                    taskId = 7,
                                    startTime = Instant.parse("2025-09-24T21:00:00Z"),
                                    duration = Duration.ofMinutes(45),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 6, // Career/Admin Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 14,
                            name = "Apply for SWE Internship at Microsoft",
                            dueDate = null,
                            difficulty = 1,
                            color = Color.Gray,
                            userEstimate = Duration.ofMinutes(45),
                            segments = listOf(
                                Segment(
                                    segmentId = 27,
                                    taskId = 14,
                                    startTime = Instant.parse("2025-09-16T20:30:00Z"),
                                    duration = Duration.ofMinutes(35),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 6, // Career/Admin Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 23,
                            name = "Draft Final Project Proposal",
                            dueDate = Instant.parse("2025-10-22T23:59:59Z"),
                            difficulty = 2,
                            color = Color.Gray,
                            userEstimate = Duration.ofHours(2), // Underestimated
                            segments = listOf(
                                Segment(
                                    segmentId = 46,
                                    taskId = 23,
                                    startTime = Instant.parse("2025-10-20T11:00:00Z"),
                                    duration = Duration.ofHours(3).plusMinutes(20),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 6, // Career/Admin Profile
                            appEstimate = null
                        ),
                    )
                ),
                Profile(
                    id = 0,
                    name = "Personal/Chores",
                    color = Color.Cyan,
                    defaultDifficulty = 0,
                    sessions = listOf(
                        CompletedTask(
                            taskId = 16,
                            name = "Plan Weekly Schedule",
                            dueDate = null,
                            difficulty = 0,
                            color = Color.Cyan,
                            userEstimate = Duration.ofMinutes(20),
                            segments = listOf(
                                Segment(
                                    segmentId = 29,
                                    taskId = 16,
                                    startTime = Instant.parse("2025-09-22T09:00:00Z"),
                                    duration = Duration.ofMinutes(25),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 7, // Personal/Chores Profile
                            appEstimate = null
                        ),
                        CompletedTask(
                            taskId = 25,
                            name = "Organize Project Files on GitHub",
                            dueDate = null,
                            difficulty = 0,
                            color = Color.Cyan,
                            userEstimate = Duration.ofMinutes(30), // Underestimated
                            segments = listOf(
                                Segment(
                                    segmentId = 50,
                                    taskId = 25,
                                    startTime = Instant.parse("2025-10-25T17:00:00Z"),
                                    duration = Duration.ofHours(1).plusMinutes(5),
                                    type = Segment.Type.WORK
                                )
                            ),
                            markers = emptyList(),
                            profileId = 7, // Personal/Chores Profile
                            appEstimate = null
                        ),
                    )
                )
            ),
            orphanSessions = emptyList()
        )
    }
}