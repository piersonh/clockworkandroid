package com.wordco.clockworkandroid.user_stats_feature.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.permission.PermissionRequestSignaller
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.user_stats_feature.domain.use_case.GetAllCompletedSessionsUseCase
import com.wordco.clockworkandroid.user_stats_feature.domain.use_case.GetAllSessionsUseCase
import com.wordco.clockworkandroid.user_stats_feature.ui.model.ExportDataError
import com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper.toCompletedSessionListItem
import com.wordco.clockworkandroid.user_stats_feature.ui.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


class UserStatsViewModel(
    application: MainApplication,
    getAllCompletedSessionsUseCase: GetAllCompletedSessionsUseCase,
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val permissionRequestSignaller: PermissionRequestSignaller
) : AndroidViewModel(application) {


    private val _uiState = MutableStateFlow<UserStatsUiState>(UserStatsUiState.Retrieving)

    val uiState: StateFlow<UserStatsUiState> = _uiState.asStateFlow()


    // TODO: make a getCompletedTasks (?)
    private val _tasks = getAllCompletedSessionsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    init {
        viewModelScope.launch {
            _tasks.map { tasks ->
                if (tasks == null) {
                    UserStatsUiState.Retrieving
                } else {
                    UserStatsUiState.Retrieved(
                        completedTasks = tasks
                            .map { it.toCompletedSessionListItem() }
                            .sortedBy { it.completedAt }
                            .reversed()
                    )
                }
            }.collect { uiState ->
                _uiState.update { uiState }
            }
        }
    }

    suspend fun onExportUserData() : Result<String, ExportDataError> {
        val content = buildString {
            appendLine("Task Profiles:")
            getAllProfilesUseCase().first().forEach { profile ->
                appendLine("$profile")
            }
            appendLine()
            appendLine("Task Sessions:")
            getAllSessionsUseCase().first().forEach { session ->
                appendLine("$session")
            }
        }

        return writeTextToNewFileInDownloads(
            context = getApplication<MainApplication>().applicationContext,
            fileName = "clockwork_export.txt",
            content = content
        )
    }


    private suspend fun writeTextToNewFileInDownloads(
        context: Context,
        fileName: String,
        content: String
    ): Result<String, ExportDataError> {
        return try {
            // Android 10 (API 29) and higher: Use MediaStore API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return Result.Error(ExportDataError.NO_URI)

                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }

                // After writing, query the URI to get the final display name
                lateinit var finalName: String
                resolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            finalName = cursor.getString(nameIndex)
                        }
                    }
                }
                return Result.Success(finalName)

            } else {
                // Android versions below 10 (API < 29): Require WRITE_EXTERNAL_STORAGE permission
                if (!permissionRequestSignaller.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Result.Error(ExportDataError.NO_PERMISSION)
                }

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                // Ensure the directory exists
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, fileName)
                file.writeText(content)
                Result.Success(fileName) // filename does not change on legacy
            }
        } catch (_: FileNotFoundException) {
            // Occurs if the path is invalid or file cannot be created/opened for writing
            return Result.Error(ExportDataError.NO_FILE)
        } catch (_: SecurityException) {
            // Can occur if a permission is implicitly denied or security policy prevents access
            return Result.Error(ExportDataError.SECURITY)
        } catch (_: IOException) {
            // Generic I/O errors (e.g., disk full, unmounted storage, stream closed)
            return Result.Error(ExportDataError.IO)
        } catch (_: Exception) {
            // Catch any other unexpected exceptions
            return Result.Error(ExportDataError.OTHER)
        }
    }


    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val application = (this[APPLICATION_KEY] as MainApplication)
                val permissionRequestSignaller = application.appContainer.permissionRequestSignal

                UserStatsViewModel(
                    application = application,
                    permissionRequestSignaller = permissionRequestSignaller,
                    getAllCompletedSessionsUseCase = application.appContainer.getAllCompletedSessionsUseCase,
                    getAllSessionsUseCase = application.appContainer.getAllSessionsUseCase,
                    getAllProfilesUseCase = application.appContainer.getAllProfilesUseCase,

                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}