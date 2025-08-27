package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import com.wordco.clockworkandroid.core.ui.NavHost
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            ClockworkTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                NavHost(onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = Short,
                    ) == SnackbarResult.ActionPerformed
                })
            }
        }
    }
}

