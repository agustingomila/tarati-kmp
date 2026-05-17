package com.agustin.tarati.features.game

import androidx.compose.runtime.Composable
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.dialogs.GameDialogs

/**
 * All dialogs rendered by [GameScreen], extracted for readability.
 *
 * Contains:
 * - [UndoWarningDialog]: shown once per game when achievements are active.
 * - [GameDialogs]: game-over, new-game, and about dialogs.
 *
 * Dialog visibility is driven by [UndoRedoState] and the dialog service state
 * collected in [GameScreen]. All callbacks bubble up to [GameScreen] so that
 * state ownership remains clear.
 */
@Composable
fun GameScreenDialogs(
    gameManagerState: GameManagerState,
    undoRedo: UndoRedoState,
    services: GameScreenServices,
    events: GameEvents,
    showGameOverDialog: Boolean,
    showNewGameDialog: Boolean,
    newGameColor: CobColor,
    showAboutDialog: Boolean,
    playerSide: CobColor,
    onStartNewGame: (CobColor) -> Unit,
    onStartTutorial: () -> Unit,
    onGameOverDialogDismissed: () -> Unit,
    onGameOverDialogReset: () -> Unit,
) {
    // One-time undo warning — shared source of truth with Sidebar.
    if (undoRedo.showUndoWarning) {
        UndoWarningDialog(
            onConfirm = undoRedo.onUndoWarningConfirmed,
            onDismiss = undoRedo.onUndoWarningDismissed,
        )
    }

    GameDialogs(
        gameState = gameManagerState.gameState,
        showGameOverDialog = showGameOverDialog,
        onGameOverConfirmed = {
            onGameOverDialogReset()
            onStartNewGame(playerSide)
        },
        onGameOverDismissed = {
            onGameOverDialogDismissed()
            services.dialogService.resetDialogs()
            events.stopGame()
        },
        showNewGameDialog = showNewGameDialog,
        newGameCobColor = newGameColor,
        onNewGameConfirmed = {
            onGameOverDialogReset()
            onStartNewGame(it)
        },
        onNewGameDismissed = {
            services.dialogService.resetDialogs()
            events.stopGame()
        },
        onShowTutorial = {
            onGameOverDialogReset()
            onStartTutorial()
        },
        showAboutDialog = showAboutDialog,
        onAboutDismissed = services.dialogService::resetDialogs,
    )
}