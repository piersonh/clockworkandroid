package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme

class MainActivity : ComponentActivity() {

    @SuppressLint(
        "SourceLockedOrientationActivity",
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val permissionRequestSignaller = (application as MainApplication).permissionRequestSignaller

        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            ClockworkTheme {
                NavHost()
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    // I don't know that we should do anything here
                }
            )

            // On app launch (wait for signaller to be initialized)
            LaunchedEffect(Unit) {
                permissionRequestSignaller.stream.collect { permission ->
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            permission
                    ) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(permission)
                    }
                }
            }
        }
    }
}
