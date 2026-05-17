package com.agustin.tarati.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.agustin.tarati.services.clipboard.GameClipboardHelper
import com.agustin.tarati.services.dialogs.DialogService
import com.agustin.tarati.services.dialogs.IDialogViewModel
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import com.agustin.tarati.ui.components.game.animation.IBoardAnimationViewModel
import org.koin.compose.koinInject

/**
 * Servicios de vida de composable de [GameScreen], creados una única vez
 * y compartidos entre los distintos composables hijos de la pantalla.
 */
class GameScreenServices(
    val animationCoordinator: AnimationCoordinator,
    val dialogService: DialogService,
    val clipboardHelper: GameClipboardHelper,
)

@Composable
fun rememberGameScreenServices(
    animationViewModel: IBoardAnimationViewModel,
    dialogViewModel: IDialogViewModel,
    clipboardHelper: GameClipboardHelper = koinInject(),
): GameScreenServices =
    remember(animationViewModel, dialogViewModel) {
        val coordinator = AnimationCoordinator(animationViewModel)
        GameScreenServices(
            animationCoordinator = coordinator,
            dialogService = DialogService(dialogViewModel),
            clipboardHelper = clipboardHelper,
        )
    }