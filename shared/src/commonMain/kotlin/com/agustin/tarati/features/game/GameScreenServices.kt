package com.agustin.tarati.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.agustin.tarati.services.clipboard.GameClipboardHelper
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import com.agustin.tarati.ui.components.game.animation.IBoardAnimationViewModel
import org.koin.compose.koinInject

/**
 * Servicios de vida de composable de [GameScreen], creados una única vez
 * y compartidos entre los distintos composables hijos de la pantalla.
 */
class GameScreenServices(
    val animationCoordinator: AnimationCoordinator,
    val clipboardHelper: GameClipboardHelper,
)

@Composable
fun rememberGameScreenServices(
    animationViewModel: IBoardAnimationViewModel,
    clipboardHelper: GameClipboardHelper = koinInject(),
): GameScreenServices =
    remember(animationViewModel) {
        GameScreenServices(
            animationCoordinator = AnimationCoordinator(animationViewModel),
            clipboardHelper = clipboardHelper,
        )
    }
