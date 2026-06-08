package com.agustin.tarati.features.game


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.core.domain.game.play.GameStatus

/**
 * Encapsulates all undo/redo state and actions for [GameScreen].
 *
 * Extracted from [GameScreen] to keep that composable focused on orchestration.
 * The state here is the single source of truth shared between
 * [BottomGameBar]
 * and [SidebarContent].
 *
 * @property showUndoWarning True while the one-time undo warning dialog is visible.
 * @property handleUndo      Triggers undo, showing the warning dialog on first attempt
 *                           when achievements are active.
 * @property handleRedo      Redoes the last undone move and restores AI history.
 * @property handleMoveToCurrent Jumps to the latest position in the history.
 */
class UndoRedoState(
    val showUndoWarning: Boolean,
    val onUndoWarningConfirmed: () -> Unit,
    val onUndoWarningDismissed: () -> Unit,
    val handleUndo: () -> Unit,
    val handleRedo: () -> Unit,
    val handleMoveToCurrent: () -> Unit,
    val handleMoveToIndex: (Int) -> Unit,
)

/**
 * Creates and remembers [UndoRedoState] for [GameScreen].
 *
 * [hasWarnedAboutUndo] survives configuration changes via [rememberSaveable].
 * It resets to false whenever a new game starts (history becomes empty).
 *
 * [showUndoWarning] is transient — a process death while the dialog is shown
 * is harmless: the dialog simply won't reappear on restore.
 *
 * @param onGameOverDialogReset Called when undo is confirmed while in GAME_OVER
 *                              state, so the game-over dialog resets alongside.
 */
@Composable
fun rememberUndoRedoState(
    gameManagerState: GameManagerState,
    screenState: GameScreenState,
    events: GameEvents,
    viewModel: IGameModel,
    /** Deshabilita undo, redo y navegación de historial durante una partida online. */
    isOnlineGame: Boolean = false,
    onGameOverDialogReset: () -> Unit,
): UndoRedoState {
    var hasWarnedAboutUndo by rememberSaveable { mutableStateOf(false) }
    var showUndoWarning by remember { mutableStateOf(false) }
    // Stores the action to execute when the undo warning is confirmed.
    // Both handleUndo and handleMoveToIndex set this before showing the dialog.
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Reset the warning flag whenever a new game starts (history cleared).
    LaunchedEffect(gameManagerState.history.getMoves().isEmpty()) {
        if (gameManagerState.history.getMoves().isEmpty()) {
            hasWarnedAboutUndo = false
        }
    }

    // True only when achievements are actually at stake: one human vs one AI,
    // and the game was not loaded from a saved file (imported games have
    // achievements disabled unconditionally — no point warning the user).
    val achievementsPossible = screenState.whiteIsAI != screenState.blackIsAI
            && !viewModel.startedFromImportedGame.collectAsState().value

    val performUndo: () -> Unit = {
        if (gameManagerState.moveIndex >= 0) {
            if (gameManagerState.gameStatus == GameStatus.GAME_OVER) {
                onGameOverDialogReset()
            }
            events.removeAIHistoryState(gameManagerState.gameState) {
                viewModel.undoMove()
            }
        }
    }

    val handleUndo: () -> Unit = {
        if (!isOnlineGame) {
            if (achievementsPossible && !hasWarnedAboutUndo) {
                pendingAction = performUndo
                showUndoWarning = true
            } else {
                performUndo()
            }
        }
    }

    val handleRedo: () -> Unit = {
        if (!isOnlineGame) {
            events.putAIHistoryState(gameManagerState.gameState) {
                viewModel.redoMove()
            }
        }
    }

    val handleMoveToCurrent: () -> Unit = {
        viewModel.moveToCurrentState()
    }

    val handleMoveToIndex: (Int) -> Unit = { index ->
        if (!isOnlineGame) {
            // Show the same one-time warning as undo: navigating to a non-last
            // move puts achievements at risk (moveIndex < size - 1 disables them).
            val isMovingBack = index < gameManagerState.history.size - 1
            if (isMovingBack && achievementsPossible && !hasWarnedAboutUndo) {
                pendingAction = { viewModel.moveToIndex(index) }
                showUndoWarning = true
            } else {
                viewModel.moveToIndex(index)
            }
        }
    }

    return UndoRedoState(
        showUndoWarning = showUndoWarning,
        onUndoWarningConfirmed = {
            showUndoWarning = false
            hasWarnedAboutUndo = true
            pendingAction?.invoke()
            pendingAction = null
        },
        onUndoWarningDismissed = {
            showUndoWarning = false
            pendingAction = null
        },
        handleUndo = handleUndo,
        handleRedo = handleRedo,
        handleMoveToCurrent = handleMoveToCurrent,
        handleMoveToIndex = handleMoveToIndex,
    )
}