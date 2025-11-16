package com.wordco.clockworkandroid

import android.content.ContentValues
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.local.MIGRATION_13_14
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private val TEST_DB_NAME = "migration-test.db"

    // This rule manages the test databases and schema validation
    @get:Rule
    val testHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate13To14_addsReminderTableAndPreservesData() {
        // --- 1. ARRANGE (Create v13 database) ---

        // Create a database with version 13
        var db = testHelper.createDatabase(TEST_DB_NAME, 13)

        // Insert sample data that should be preserved
        val profileId = 1L
        val taskId = 10L

        // Insert a sample profile
        val profileValues = ContentValues().apply {
            put("id", profileId)
            put("name", "Test Profile")
            put("difficulty", 3)
            put("color", -65536) // Red
        }
        db.insert("ProfileEntity", 0, profileValues)

        // Insert a sample task linked to the profile
        val taskValues = ContentValues().apply {
            put("taskId", taskId)
            put("name", "Test Task")
            put("profileId", profileId)
            put("difficulty", 5)
            put("color", -16711936) // Green
            put("status", 0)
        }
        db.insert("TaskEntity", 0, taskValues)

        // Close the v13 database
        db.close()

        // --- 2. ACT (Run the migration) ---

        // Re-open the database with version 14 and run the migration.
        // The testHelper will automatically validate the new schema against 14.json.
        // If the schema doesn't match, this line will throw an exception.
        db = testHelper.runMigrationsAndValidate(TEST_DB_NAME, 14, true, MIGRATION_13_14)

        // --- 3. ASSERT (Verify the results) ---

        // 3a. Check that old data is still present
        var cursor = db.query("SELECT * FROM ProfileEntity WHERE id = $profileId")
        assertTrue("Profile data was not preserved.", cursor.moveToFirst())
        assertEquals("Test Profile", cursor.getString(cursor.getColumnIndexOrThrow("name")))
        cursor.close()

        cursor = db.query("SELECT * FROM TaskEntity WHERE taskId = $taskId")
        assertTrue("Task data was not preserved.", cursor.moveToFirst())
        assertEquals("Test Task", cursor.getString(cursor.getColumnIndexOrThrow("name")))
        assertEquals(profileId, cursor.getLong(cursor.getColumnIndexOrThrow("profileId")))
        cursor.close()

        // 3b. Check that the new table (`ReminderEntity`) exists and is writable
        val newReminderValues = ContentValues().apply {
            put("sessionId", taskId) // Foreign key to our sample task
            put("workRequestId", "test-work-request-uuid")
            put("scheduledTime", System.currentTimeMillis())
            put("status", 0)
        }
        val newRowId = db.insert("ReminderEntity", 0, newReminderValues)
        assertTrue("Insert into new ReminderEntity table failed.", newRowId > 0)

        // 3c. Read the data back from the new table
        cursor = db.query("SELECT * FROM ReminderEntity WHERE sessionId = $taskId")
        assertTrue("Data not found in new ReminderEntity table.", cursor.moveToFirst())
        assertEquals("test-work-request-uuid", cursor.getString(cursor.getColumnIndexOrThrow("workRequestId")))
        assertEquals(taskId, cursor.getLong(cursor.getColumnIndexOrThrow("sessionId")))
        cursor.close()
    }
}