package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme

class MainActivity : ComponentActivity() {

    @SuppressLint(
        "SourceLockedOrientationActivity",
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            ClockworkTheme {
                NavHost()
            }
        }
    }
}
