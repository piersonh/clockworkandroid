package com.wordco.clockworkandroid.database.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Create the new table with the correct foreign key action
        db.execSQL("""
            CREATE TABLE `TaskEntity_new` (
                `taskId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `profileId` INTEGER,
                `dueDate` INTEGER,
                `difficulty` INTEGER NOT NULL,
                `color` INTEGER NOT NULL,
                `status` INTEGER NOT NULL,
                `userEstimate` INTEGER,
                `lowAppEstimate` INTEGER,
                `highAppEstimate` INTEGER,
                FOREIGN KEY(`profileId`) REFERENCES `ProfileEntity`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
        """.trimIndent())

        // Step 2: Copy the data from the old table to the new table
        db.execSQL("""
            INSERT INTO `TaskEntity_new` (
                taskId, name, profileId, dueDate, difficulty, color, status, userEstimate, lowAppEstimate, highAppEstimate
            )
            SELECT
                taskId, name, profileId, dueDate, difficulty, color, status, userEstimate, lowAppEstimate, highAppEstimate
            FROM `TaskEntity`
        """.trimIndent())

        // Step 3: Remove the old table
        db.execSQL("DROP TABLE `TaskEntity`")

        // Step 4: Rename the new table to the original table name
        db.execSQL("ALTER TABLE `TaskEntity_new` RENAME TO `TaskEntity`")

        // Step 5 (Optional but recommended): Recreate the index from your @Entity definition
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_TaskEntity_profileId` ON `TaskEntity`(`profileId`)")
    }
}

/**
 * Migration from database version 13 to 14.
 *
 * This migration adds the new `ReminderEntity` table and its index.
 */
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Step 1: Create the new ReminderEntity table.
        // The schema is taken directly from your 14.json file.
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `ReminderEntity` (" +
                    "`reminderId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`sessionId` INTEGER NOT NULL, " +
                    "`workRequestId` TEXT NOT NULL, " +
                    "`scheduledTime` INTEGER NOT NULL, " +
                    "`status` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`sessionId`) REFERENCES `TaskEntity`(`taskId`) " +
                    "ON UPDATE NO ACTION ON DELETE CASCADE" +
                    ")"
        )

        // Step 2: Create the index for the ReminderEntity table.
        // This is also from 14.json and improves query performance on `sessionId`.
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_ReminderEntity_sessionId` " +
                    "ON `ReminderEntity` (`sessionId`)"
        )
    }
}