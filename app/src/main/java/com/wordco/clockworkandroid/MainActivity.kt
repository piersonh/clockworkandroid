package com.wordco.clockworkandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wordco.clockworkandroid.core.domain.model.PermissionRequest
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    @SuppressLint(
        "SourceLockedOrientationActivity",
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider.create(
            store = this.viewModelStore,
            factory = MainViewModel.Factory,
            extras = this.defaultViewModelCreationExtras
        )[MainViewModel::class]

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val appContainer = (application as MainApplication).appContainer
        val permissionRequestSignaller = appContainer.permissionRequestSignal

        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            ClockWorkTheme {
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

        observeViewModelState()
    }

    private fun observeViewModelState() {
        lifecycleScope.launch {
            // This block repeats execution whenever the lifecycle is STARTED
            // and suspends when it's STOPPED.
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {

                viewModel.isFirstLaunch.collect { isFirstLaunch ->

                    if (isFirstLaunch) {
                        Log.d("MainActivity", "First launch detected. Showing onboarding.")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ActivityCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            (this@MainActivity.application as MainApplication).appContainer
                                .permissionRequestSignal.request(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        viewModel.onFirstLaunchHandled()

                    } else {
                        Log.d("MainActivity", "Subsequent launch. Loading main content.")
                    }
                }
            }
        }
    }
}
