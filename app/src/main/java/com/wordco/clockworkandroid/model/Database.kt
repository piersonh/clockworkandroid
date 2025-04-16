package com.wordco.clockworkandroid.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class], version = 1)
@TypeConverters(TimestampConverter::class, DurationConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun taskDao(): SegmentDao
}