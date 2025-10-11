package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme

class MainActivity : ComponentActivity() {

    @SuppressLint(
        "SourceLockedOrientationActivity",
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val appContainer = (application as MainApplication).appContainer
        val permissionRequestSignaller = appContainer.permissionRequestSignal

        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            ClockworkTheme {
                NavHost()
            }

            var activeRequest by remember { mutableStateOf<PermissionRequest?>(null) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    Log.i("Permission Request", "${activeRequest!!.permission}: $isGranted")
                    activeRequest?.result?.complete(isGranted)
                    activeRequest = null
                }
            )

            // On app launch (wait for signaller to be initialized)
            LaunchedEffect(Unit) {
                permissionRequestSignaller.requestStream.collect { request ->
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            request.permission
                    ) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("Permission Request", "Received ${request.permission}, already granted")
                        request.result.complete(true)
                    } else {
                        Log.i("Permission Request", "Received ${request.permission}, launching launcher")
                        activeRequest = request
                        permissionLauncher.launch(request.permission)
                    }
                }
            }
        }
    }
}
