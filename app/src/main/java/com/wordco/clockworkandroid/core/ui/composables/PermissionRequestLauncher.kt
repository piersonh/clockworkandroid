package com.wordco.clockworkandroid.core.ui.composables

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun getPermsLauncher(
    onResult: (Boolean) -> Unit,
) = rememberLauncherForActivityResult(
ActivityResultContracts.RequestPermission()
) { isGranted ->
    onResult(isGranted)
}

fun runIfPermitted(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    permission: String,
    block: () -> Unit,
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        block()
    }
}