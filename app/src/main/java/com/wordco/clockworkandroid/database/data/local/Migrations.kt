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

val MIGRATION_13_14: Migration = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create the new ReminderEntity table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `ReminderEntity` (
                `reminderId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `sessionId` INTEGER NOT NULL, 
                `workRequestId` TEXT NOT NULL, 
                `scheduledTime` INTEGER NOT NULL, 
                `status` TEXT NOT NULL, 
                FOREIGN KEY(`sessionId`) REFERENCES `TaskEntity`(`taskId`) 
                    ON DELETE CASCADE
            )
        """)

        // 2. Create the index on the foreign key for fast lookups
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_ReminderEntity_sessionId` 
            ON `ReminderEntity` (`sessionId`)
        """)
    }
}