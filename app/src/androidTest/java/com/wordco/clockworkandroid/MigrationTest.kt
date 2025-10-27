package com.wordco.clockworkandroid

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.local.MIGRATION_13_14
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test-db"

    private val PREVIOUS_VERSION = 13
    private val LATEST_VERSION = 14

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate13to14_addsReminderTable() {
        // 1. Create the database at the OLD version
        helper.createDatabase(TEST_DB, PREVIOUS_VERSION).use { db ->
            db.execSQL("""
                INSERT INTO TaskEntity (
                    taskId, name, profileId, dueDate, difficulty, color, status, 
                    userEstimate, lowAppEstimate, highAppEstimate
                ) 
                VALUES (
                    1, 'Test Task', 1, 123456, 3, -1, 0, 
                    NULL, NULL, NULL
                )
            """)
        }

        // 2. Run the migration to the NEW version
        helper.runMigrationsAndValidate(TEST_DB, LATEST_VERSION, true, MIGRATION_13_14).use { db ->

            // 3. VERIFY: Check if the old data is still there
            val taskCursor = db.query("SELECT * FROM TaskEntity WHERE taskId = 1")
            assertThat(taskCursor.count).isEqualTo(1)

            // 4. VERIFY: Check if the new table works.
            // The easiest way is to try inserting data into it.
            // This will fail if the table, columns, or types are wrong.
            try {
                db.execSQL("""
                    INSERT INTO ReminderEntity (sessionId, workRequestId, scheduledTime, status) 
                    VALUES (1, 'work-123', 123456, 'PENDING')
                """)

                // Now check if the insert was successful
                val reminderCursor = db.query("SELECT * FROM ReminderEntity WHERE workRequestId = 'work-123'")
                assertThat(reminderCursor.count).isEqualTo(1)

            } catch (e: Exception) {
                // If the insert fails, re-throw to fail the test
                throw AssertionError("Failed to insert into new ReminderEntity table", e)
            }
        }
    }
}