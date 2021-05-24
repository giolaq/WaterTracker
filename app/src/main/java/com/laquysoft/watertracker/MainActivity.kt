package com.laquysoft.watertracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laquysoft.watertracker.waterrunning.MainUI
import com.laquysoft.watertracker.ui.theme.WaterTrackerTheme

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    private val soundDetector = SoundDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaterTrackerTheme {
                checkMicrophonePermission()
                Surface(color = MaterialTheme.colors.background) {
                    val viewModel: MainViewModel = viewModel(
                        factory = viewModelProviderFactoryOf { MainViewModel(soundDetector) }
                    )
                    MainUI(viewModel)
                }
            }
        }
    }

    private fun checkMicrophonePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ), 0x123
            )
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WaterTrackerTheme {
        Greeting("Android")
    }
}