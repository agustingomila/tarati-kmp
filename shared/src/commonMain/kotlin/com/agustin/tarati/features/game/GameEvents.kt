package com.agustin.tarati.features.game

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Stable
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.cleanGameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.features.seasonal.ISpecialEventManager
import com.agustin.tarati.services.achievements.IAchievementsManager
import com.agustin.tarati.services.clock.IClockService
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import com.agustin.tarati.ui.components.game.animation.AnimationEvent
import com.agustin.tarati.ui.components.tutorial.ITutorialService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Stable
class GameEvents(
    private val drawerState: DrawerState,
    private val gameService: IGameService,
    private val onGamesLibrary: () -> Unit,
    private val onSaveGame: () -> Unit,
    private val onCopyMovesToClipboard: (moves: List<Move>) -> Unit,
    private val animationCoordinator: AnimationCoordinator,
    private val tutorialService: ITutorialService,
    private val soundService: ISoundService,
    private val achievementsManager: IAchievementsManager,
    private val specialEventManager: ISpecialEventManager,
    /** Difficulty of the AI controlling the White band — used for achievement reporting. */
    private val difficulty: () -> Difficulty,
    private val playerSide: () -> CobColor,
    private val whiteIsAI: () -> Boolean,
    private val blackIsAI: () -> Boolean,
    private val aiEngine: IAIEngine,
    private val scope: CoroutineScope,
    private val clockService: IClockService,
    private val timeControlProvider: () -> TimeControlMode,
) {
    // ── Dialog requests ───────────────────────────────────────────────────────

    private val _dialogRequest = MutableSharedFlow<DialogRequest>(extraBufferCapacity = 4)

    /** [GameScreen] collects este flow y despacha la alerta correspondiente al [UIMessageBus]. */
    val dialogRequest: SharedFlow<DialogRequest> = _dialogRequest.asSharedFlow()

    // ── Player tracking ───────────────────────────────────────────────────────

    /**
     * Set to true when the human/AI assignment of any band is changed mid-game.
     * Cleared only when a new game begins via [startNewGame].
     *
     * Rationale: allowing player-type changes mid-game would let a player hand
     * off a losing position to the AI, making achievement conditions manipulable.
     */
    private var playerTypeChangedMidGame: Boolean = false

    /**
     * Called by the sidebar when a band's Human/AI assignment changes.
     * Marks [playerTypeChangedMidGame] so achievements are disabled for the
     * rest of the current game without restarting it.
     *
     * Only marks the flag when moves have been played (history.size > 1).
     * Setup changes before the first move — e.g. switching from the default
     * White=Human to Black=Human — should not disqualify achievements for
     * the game that follows.
     */
    fun onPlayerTypeChanged() {
        if (gameService.history.value.size > 1) {
            playerTypeChangedMidGame = true
        }
    }

    /** True only for Human vs AI games on unmodified boards with no mid-game player-type changes. */
    private val achievementsEnabled: Boolean
        get() {
            val exactlyOneHuman = whiteIsAI() != blackIsAI()
            return exactlyOneHuman &&
                    !gameService.startedFromEditedBoard.value &&
                    !gameService.startedFromImportedGame.value &&
                    !playerTypeChangedMidGame &&
                    gameService.moveIndex.value >= gameService.history.value.size - 1
        }

    fun applyMove(
        move: Move,
        gameState: GameState,
    ) {
        // Rejects stale moves: Compose recomposition can lag behind gameService state,
        // so a second tap before recompose may arrive with a superseded gameState.
        if (gameState != gameService.gameState.value) return

        val nextState = gameState.applyMove(move)

        if (achievementsEnabled) {
            scope.launch(Dispatchers.Default) {
                achievementsManager.onMoveApplied(move, gameState, nextState)
            }
        }

        // addMove must precede isGameOver so aiEngine.putState() registers the position
        // before the triple-repetition check — otherwise the 3rd occurrence is missed.
        gameService.addMove(
            move = move,
            nextState = nextState,
            onMoveRecord = {
                aiEngine.putState(
                    gameState = nextState,
                    moveBy = gameState.currentTurn,
                )
            },
        )

        val isGameOver = nextState.isGameOver(aiEngine.positionHistory)

        animationCoordinator.handleEvent(
            AnimationEvent.MoveEvent(
                move = move,
                oldGameState = gameState,
                newGameState = nextState,
                isGameOver = isGameOver,
            ),
        )

        if (isGameOver) {
            // Capture state before launching async work — the game state may advance.
            val matchState = nextState.getMatchState(aiEngine.positionHistory)
            val capturedDifficulty = difficulty()
            val capturedPlayerSide = playerSide()
            val capturedAchievementsEnabled = achievementsEnabled
            val specialEventsEnabled = (whiteIsAI() != blackIsAI()) && !playerTypeChangedMidGame
            val humanSide = if (whiteIsAI()) CobColor.BLACK else CobColor.WHITE

            scope.launch(Dispatchers.Default) {
                if (capturedAchievementsEnabled) {
                    achievementsManager.onGameOver(matchState, capturedPlayerSide, capturedDifficulty)
                }
                if (specialEventsEnabled) {
                    specialEventManager.onGameResult(matchState, humanSide)
                }
            }

            gameService.gameOver()
        } else {
            // Promotions (from == to) still credit an increment to the promoting side.
            clockService.applyIncrementAfterMove(gameState.currentTurn)
            clockService.startClockFor(nextState.currentTurn)
        }
    }

    /**
     * Materializa una pérdida por tiempo. Idempotente: si el estado ya tiene
     * [GameState.timedOutColor] o la partida ya está en [GameStatus.GAME_OVER],
     * la llamada es un no-op.
     */
    fun onTimeout(loser: CobColor) {
        val currentState = gameService.gameState.value
        if (currentState.timedOutColor != null) return
        if (gameService.gameStatus.value == GameStatus.GAME_OVER) return

        val timedOutState = currentState.copy(timedOutColor = loser)
        gameService.updateGameState(timedOutState)

        if (achievementsEnabled) {
            val matchState = timedOutState.getMatchState(aiEngine.positionHistory)
            val capturedSide = playerSide()
            val capturedDifficulty = difficulty()
            val specialEventsEnabled = (whiteIsAI() != blackIsAI()) && !playerTypeChangedMidGame
            val humanSide = if (whiteIsAI()) CobColor.BLACK else CobColor.WHITE

            scope.launch(Dispatchers.Default) {
                achievementsManager.onGameOver(matchState, capturedSide, capturedDifficulty)
                if (specialEventsEnabled) {
                    specialEventManager.onGameResult(matchState, humanSide)
                }
            }
        }

        gameService.gameOver()
        // No animated move precedes this end — signal gameOverReady immediately.
        animationCoordinator.handleEvent(AnimationEvent.NotifyGameOver)
    }

    fun claimFiftyMoveDraw(gameState: GameState) {
        val claimedState = gameState.copy(claimedFiftyMoveDraw = true)
        if (achievementsEnabled) {
            val matchState = claimedState.getMatchState(aiEngine.positionHistory)
            val capturedSide = playerSide()
            val capturedDifficulty = difficulty()
            scope.launch(Dispatchers.Default) {
                achievementsManager.onGameOver(matchState, capturedSide, capturedDifficulty)
            }
        }
        gameService.updateGameState(claimedState)
        gameService.gameOver()
        animationCoordinator.handleEvent(AnimationEvent.NotifyGameOver)
    }

    fun removeAIHistoryState(
        gameState: GameState,
        onRemove: () -> Unit,
    ) {
        aiEngine.removeState(gameState)
        onRemove()
    }

    fun putAIHistoryState(
        gameState: GameState,
        onPut: () -> Unit,
    ) {
        aiEngine.putState(gameState, gameState.currentTurn)
        onPut()
    }

    private fun clearBoard(gameState: GameState = initialGameState()) {
        gameService.stopGame()
        aiEngine.clearHistory()
        gameService.updateGameState(gameState)
    }

    fun gameOver(scope: CoroutineScope) {
        scope.launch {
            delay(2500L.milliseconds)
            _dialogRequest.tryEmit(DialogRequest.GameOver(playerSide()))
        }
    }

    fun stopGame() {
        gameService.stopGame()
    }

    fun resumeGame() {
        gameService.resumeGame()
    }

    fun startNewGame(
        scope: CoroutineScope,
        playerSide: CobColor,
    ) {
        clearBoard()
        playerTypeChangedMidGame = false
        tutorialService.closeTutorial()

        animationCoordinator.handleEvent(AnimationEvent.StopHighlights)
        animationCoordinator.handleEvent(AnimationEvent.SyncState)

        drawerState.closeIfOpen(scope)

        soundService.playNewGameSound()
        clockService.resetClock(timeControlProvider())

        gameService.startGame(playerSide)
    }

    fun startTutorial(scope: CoroutineScope) {
        gameService.suppressLogoTransition()
        gameService.endEditing()
        gameService.stopGame()

        animationCoordinator.handleEvent(AnimationEvent.StopHighlights)
        animationCoordinator.handleEvent(AnimationEvent.SyncState)

        drawerState.closeIfOpen(scope)

        tutorialService.startTutorial()
    }

    fun resetTutorial() {
        scope.launch(Dispatchers.Default) { achievementsManager.onTutorialCompleted() }
        tutorialService.resetTutorial()
        clearBoard()
    }

    fun endTutorial() {
        scope.launch(Dispatchers.Default) { achievementsManager.onTutorialCompleted() }
        tutorialService.endTutorial()
        clearBoard()
    }

    fun showNewGameDialog(color: CobColor) {
        _dialogRequest.tryEmit(DialogRequest.NewGame(color))
    }

    fun showAboutDialog() {
        _dialogRequest.tryEmit(DialogRequest.About)
    }

    fun copyMovesToClipboard(moves: List<Move>) {
        onCopyMovesToClipboard(moves)
    }

    fun preStepTutorial() {
        clearBoard(cleanGameState())
    }

    fun applyTutorialMove(
        move: Move,
        gameState: GameState,
    ) {
        val nextState = gameState.applyMove(move)
        animationCoordinator.handleEvent(AnimationEvent.MoveEvent(move, gameState, nextState))
        gameService.updateGameState(nextState)
    }

    fun applyTutorialRejectedMove(
        move: Move,
        gameState: GameState,
    ) {
        val nextState = gameState.applyMove(move)
        animationCoordinator.handleEvent(AnimationEvent.MoveEvent(move, gameState, nextState))
        gameService.updateGameState(nextState)

        val undoMove = Move(move.to to move.from)
        animationCoordinator.handleEvent(AnimationEvent.MoveEvent(undoMove, nextState, gameState))
        gameService.updateGameState(gameState)
    }

    fun showGamesLibrary(): Unit = onGamesLibrary()

    fun saveGame(): Unit = onSaveGame()

    /** Delega a [IAchievementsManager] para abrir la pantalla de logros. */
    fun showAchievementsUI(onNavigateToScreen: () -> Unit = {}): Unit =
        achievementsManager.showAchievementsUI(onNavigateToScreen)
}

fun DrawerState.closeIfOpen(scope: CoroutineScope) {
    scope.launch { if (isOpen) close() }
}

// ── Dialog request model ──────────────────────────────────────────────────────

sealed class DialogRequest {
    data class GameOver(val playerSide: CobColor) : DialogRequest()
    data class NewGame(val initialColor: CobColor) : DialogRequest()
    data object About : DialogRequest()
}