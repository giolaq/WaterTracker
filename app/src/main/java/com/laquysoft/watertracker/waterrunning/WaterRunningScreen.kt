package com.laquysoft.watertracker.waterrunning

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.laquysoft.watertracker.MainViewModel
import com.laquysoft.watertracker.R
import com.laquysoft.watertracker.ui.theme.Blue400
import com.laquysoft.watertracker.ui.theme.Green200


@ExperimentalAnimationApi
@Composable
fun MainUI(timePackViewModel: MainViewModel) {
    val playPauseState = remember { mutableStateOf(true) }

    val isWaterRunning by timePackViewModel.isWaterRunning.observeAsState(false)
    val timeSpec by timePackViewModel.timeState.observeAsState(0)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = String.format("%.2f Liters", timeSpec * 0.08),
            fontWeight = FontWeight.Bold,
            color = Green200,
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        TimeWave(
            isWaterRunning,
            playPauseState.value,
            modifier = Modifier
                .fillMaxSize()
        )

        DetectorStatus(
            playPauseState.value,
            Modifier
                .align(Alignment.TopCenter)
                .padding(32.dp)
        ) {
            if (playPauseState.value) {
                playPauseState.value = false
                timePackViewModel.startDetection()
            } else {
                playPauseState.value = true
                timePackViewModel.stopDetection()
            }
        }
    }
}


@Composable
fun TimeWave(
    isWaterRunning: Boolean,
    init: Boolean,
    modifier: Modifier
) {
    if (!init) {
        val waveColor = Blue400
        val deltaXAnim = rememberInfiniteTransition()
        val dx by deltaXAnim.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing)
            )
        )

        val screenHeightPx = with(LocalDensity.current) {
            (LocalConfiguration.current.screenHeightDp * density) - 150.dp.toPx()
        }

        val waveHeight = 125f
        val waveWidth = 2000
        val originalY = 150f
        val path = Path()

        val animTranslate by animateFloatAsState(
            targetValue = if (isWaterRunning) 0f else screenHeightPx,
            animationSpec = TweenSpec(
                durationMillis = if (isWaterRunning) 50000 else Int.MAX_VALUE,
                easing = LinearEasing
            )
        )

        Canvas(
            modifier = modifier.fillMaxSize(),
            onDraw = {
                translate(top = animTranslate) {
                    drawWave(path, waveColor, waveWidth, dx, originalY, waveHeight)
                    drawWave(path, waveColor, waveWidth - 500, dx, originalY, waveHeight - 200)
                }
            }
        )
    }
}

private fun DrawScope.drawWave(
    path: Path,
    waveColor: Color,
    waveWidth: Int,
    dx: Float,
    originalY: Float,
    waveHeight: Float
) {
    drawPath(path = path, color = waveColor)
    val halfWaveWidth = waveWidth / 2

    path.apply {
        reset()
        moveTo(-waveWidth + (waveWidth * dx), originalY.dp.toPx())
        for (i in -waveWidth..(size.width.toInt() + waveWidth) step waveWidth) {
            relativeQuadraticBezierTo(
                halfWaveWidth.toFloat() / 2,
                -waveHeight,
                halfWaveWidth.toFloat(),
                0f
            )
            relativeQuadraticBezierTo(
                halfWaveWidth.toFloat() / 2,
                waveHeight,
                halfWaveWidth.toFloat(),
                0f
            )
        }
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
}

@Composable
private fun DetectorStatus(state: Boolean, modifier: Modifier, action: () -> Unit) {
    FloatingActionButton(
        onClick = {
            action.invoke()
        },
        backgroundColor = Green200,
        content = {
            if (state) {
                Image(
                    painterResource(R.drawable.listening),
                    contentDescription = "Activate Detector"
                )
            } else {
                Image(
                    painterResource(R.drawable.notlistening),
                    contentDescription = "Deactivate Detector",
                )
            }
        },
        modifier = modifier
            .size(80.dp)
    )
}
