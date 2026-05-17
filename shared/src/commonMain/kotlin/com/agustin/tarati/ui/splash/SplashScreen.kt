package com.agustin.tarati.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.TaratiLogo
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onNavigateToGame: () -> Unit = {}) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec =
                repeatable(
                    iterations = 1,
                    animation =
                        tween(
                            durationMillis = 2800,
                            delayMillis = 10,
                            easing = FastOutSlowInEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
        )
        rotation.snapTo(0f)
        delay(10.milliseconds)

        onNavigateToGame()
    }

    DrawRotatedLogo(rotation = rotation.value)
}

@Composable
fun DrawRotatedLogo(rotation: Float) {
    TaratiBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TaratiLogo(
                size = 100.dp,
                rotationDeg = rotation,
            )
        }
    }
}