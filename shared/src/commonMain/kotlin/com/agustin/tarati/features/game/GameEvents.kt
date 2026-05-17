package com.agustin.tarati.features.game

import androidx.compose.material3.DrawerState
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
import com.agustin.tarati.services.dialogs.IDialogViewModel
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.ui.components.game.animation.AnimationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class GameEvents(
    private val drawerState: DrawerState,
    private val gameService: IGameService,
    private val onGamesLibrary: () -> Unit,
    private val onSaveGame: () -> Unit,
    private val onCopyMovesToClipboard: (moves: List<Move>) -> Unit,
    private val animationCoordinator: com.agustin.tarati.ui.components.game.animation.AnimationCoordinator,
    private val tutorialService: com.agustin.tarati.ui.components.tutorial.ITutorialService,
    private val soundService: ISoundService,
    private val dialogService: IDialogViewModel,
    private val achievementsManager: IAchievementsManager,
    private val specialEventManager: ISpecialEventManager,
    /** Difficulty of the AI controlling the White band — used for achievement reporting. */
    private val difficulty: () -> Difficulty,
    private val playerSide: () -> CobColor,
    private val whiteIsAI: () -> Boolean,
    private val blackIsAI: () -> Boolean,
    private val aiEngine: IAIEngine,
    private val scope: CoroutineScope,
    /**
     * Servicio del reloj. Se invoca desde [applyMove] para acreditar el
     * incremento y transferir el turno; desde [onTimeout] para materializar
     * la pérdida por tiempo; y desde [startNewGame] para reiniciar con el
     * modo actual.
     *
     * Las transiciones por cambio de `gameStatus` (PLAYING/NO_PLAYING/GAME_OVER)
     * son manejadas por un `LaunchedEffect` en
     * [GameScreenSideEffects] observando
     * [IGameService.gameStatus]. Esto cubre de forma uniforme todos los
     * caminos — incluidos `undoMove` y `redoMove` del ViewModel, que cambian
     * status directamente sin pasar por [GameEvents].
     */
    private val clockService: IClockService,
    /**
     * Provee el [TimeControlMode] activo al iniciar una partida nueva.
     * Típicamente, envuelve el StateFlow de settings: `{ settingsState.timeControl }`.
     * En el paso 3 puede cablearse temporalmente a `{ TimeControlMode.Unlimited }`
     * hasta que el selector de UI se implemente.
     */
    private val timeControlProvider: () -> TimeControlMode,
) {
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

    /**
     * Guard que determina si los logros pueden otorgarse en la partida actual.
     *
     * ## Condiciones requeridas
     * - **Exactamente un bando es humano:** los logros requieren un humano jugando
     *   contra la IA. AI vs. AI y Human vs. Human quedan excluidos.
     * - **Tablero no editado:** las partidas iniciadas desde el editor permiten
     *   posiciones arbitrarias que harían trivial forzar condiciones de victoria.
     * - **Historial no alterado por Undo:** si el jugador retrocedió movimientos y
     *   luego jugó desde esa posición, el logro en esa rama no cuenta.
     * - **Tipo de jugador no cambiado en medio de la partida:** cambiar Human/AI
     *   mid-game invalida los logros para esa partida.
     *
     * ## Por qué el tutorial es exento
     * [resetTutorial] y [endTutorial] llaman a [IAchievementsManager.onTutorialCompleted]
     * directamente sin pasar por este guard.
     */
    private val achievementsEnabled: Boolean
        get() {
            // Exactly one band must be human: Human vs AI only.
            // AI vs AI (both true) and Human vs Human (both false) are excluded.
            val exactlyOneHuman = whiteIsAI() != blackIsAI()
            return exactlyOneHuman &&
                    !gameService.startedFromEditedBoard.value &&
                    !gameService.startedFromImportedGame.value &&
                    !playerTypeChangedMidGame &&
                    gameService.moveIndex.value >= gameService.history.value.size - 1
        }

    /**
     * Aplica un movimiento humano o de IA a la partida en curso.
     *
     * ## Orden de operaciones (crítico)
     * 1. Calcula el nuevo estado lógico y notifica logros por movimiento.
     * 2. Registra el movimiento en el historial
     *    ([GameManager.addMove]) y actualiza
     *    el historial de posiciones de [IAIEngine] vía `onMoveRecord`.
     * 3. Calcula `isGameOver` **después** de que [IAIEngine.putState] ha registrado
     *    la posición. Esto es necesario porque [GameState.isGameOver] puede depender
     *    del historial de posiciones para detectar triple repetición: si se evaluara
     *    antes de `putState()`, la posición actual no estaría contabilizada y la
     *    condición de fin sería falsa aunque fuera la tercera ocurrencia.
     * 4. Encola el [AnimationEvent.MoveEvent] con el flag `isGameOver` ya correcto.
     *    La animación es asíncrona (va al `moveChannel`), por lo que el orden visual
     *    no se ve afectado por haber llamado a `addMove` antes.
     * 5. Si la partida terminó, notifica la capa lógica ([gameService.gameOver]) para
     *    detener la IA y cambiar el [GameStatus].
     *    La animación y el diálogo de resultado se disparan más tarde, cuando
     *    [IBoardAnimationViewModel.gameOverReady]
     *    emite tras completar la animación del movimiento terminal.
     * 6. Si la partida **no** terminó, acredita el incremento al bando que movió
     *    ([IClockService.applyIncrementAfterMove]) y transfiere el reloj al nuevo
     *    turno ([IClockService.startClockFor]). El cambio a `GAME_OVER` por parte
     *    del caso contrario lo maneja el `LaunchedEffect` de gameStatus, que llama
     *    `stopClock` de forma unificada.
     */
    fun applyMove(
        move: Move,
        gameState: GameState,
    ) {
        // Guard against duplicate moves caused by stale Compose snapshots.
        // On slow devices, Compose recomposition lags behind gameService.addMove():
        // the pointerInput coroutine still holds the old gameState, so a second tap
        // before recompose dispatches the same move against the now-superseded state.
        // Comparing by value (GameState is a data class) is sufficient: after addMove()
        // the live StateFlow value is already the next state, so any call with the
        // previous state is immediately rejected without any additional lock or timer.
        if (gameState != gameService.gameState.value) return

        val nextState = gameState.applyMove(move)

        if (achievementsEnabled) {
            // Capture values; nextState is immutable so safe to pass into coroutine.
            val capturedMove = move
            scope.launch(Dispatchers.IO) {
                achievementsManager.onMoveApplied(capturedMove, gameState, nextState)
            }
        }

        // addMove se llama PRIMERO para que aiEngine.putState() registre la posición
        // antes de evaluar isGameOver(). De otro modo, un fin por triple repetición
        // (3ª ocurrencia de la misma posición) no sería detectado: putState() sería
        // invocado con la cuenta en 2 cuando isGameOver() ya fue evaluado.
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

        // isGameOver se evalúa DESPUÉS de addMove → el historial de posiciones ya
        // incluye este movimiento, garantizando resultados correctos para todos los
        // tipos de fin de partida (capturas, stalemate, triple repetición).
        val isGameOver = nextState.isGameOver(aiEngine.positionHistory)

        // El MoveEvent se encola DESPUÉS de addMove. Al ser asíncrono (moveChannel),
        // la animación no comienza hasta que la corutina actual ceda el control, por
        // lo que el orden visual no cambia respecto al código original.
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

            scope.launch(Dispatchers.IO) {
                if (capturedAchievementsEnabled) {
                    achievementsManager.onGameOver(matchState, capturedPlayerSide, capturedDifficulty)
                }
                if (specialEventsEnabled) {
                    specialEventManager.onGameResult(matchState, humanSide)
                }
            }

            // gameService.gameOver() dispara el cambio a GAME_OVER. El LaunchedEffect
            // de GameScreenSideEffects lo observa y llama clockService.stopClock().
            gameService.gameOver()
        } else {
            // Transferencia de reloj: acredita al que movió y arranca el del nuevo turno.
            // Para promociones (from == to → nextState.currentTurn == gameState.currentTurn),
            // esto equivale a reiniciar activeMoveStartEpochMs para el mismo bando —
            // correcto porque la promoción cuenta como medio-movimiento a efectos de
            // incremento en Fischer/Bronstein.
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

            scope.launch(Dispatchers.IO) {
                achievementsManager.onGameOver(matchState, capturedSide, capturedDifficulty)
                if (specialEventsEnabled) {
                    specialEventManager.onGameResult(matchState, humanSide)
                }
            }
        }

        gameService.gameOver()
        // Sin movimiento animado pendiente: señalizar gameOverReady inmediatamente,
        // mismo mecanismo que claimFiftyMoveDraw.
        animationCoordinator.handleEvent(AnimationEvent.NotifyGameOver)
    }

    /**
     * Reclama tablas por la regla de 50 movimientos.
     *
     * Este camino de fin de partida **no genera un movimiento animado**, por lo que
     * [AnimationEvent.MoveEvent] no se encola. En su lugar se despacha
     * [AnimationEvent.NotifyGameOver] para emitir
     * [IBoardAnimationViewModel.gameOverReady]
     * de forma inmediata, garantizando que la secuencia de highlights y el diálogo de
     * resultado se disparen por el mismo mecanismo que el resto de fines de partida.
     */
    fun claimFiftyMoveDraw(gameState: GameState) {
        val claimedState = gameState.copy(claimedFiftyMoveDraw = true)
        if (achievementsEnabled) {
            val matchState = claimedState.getMatchState(aiEngine.positionHistory)
            val capturedSide = playerSide()
            val capturedDifficulty = difficulty()
            scope.launch(Dispatchers.IO) {
                achievementsManager.onGameOver(matchState, capturedSide, capturedDifficulty)
            }
        }
        gameService.updateGameState(claimedState)
        gameService.gameOver()
        // Sin movimiento animado: señalizar gameOverReady de forma inmediata.
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

    fun clearBoard(gameState: GameState = initialGameState()) {
        gameService.stopGame()
        aiEngine.clearHistory()
        gameService.updateGameState(gameState)
    }

    fun gameOver(scope: CoroutineScope) {
        scope.launch {
            // Pequeña pausa que permite ver el estado del tablero
            // antes de superponer el mensaje de partida terminada.
            delay(2500L.milliseconds)
            dialogService.showGameOverDialog()
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

        // Reinicia el reloj con el modo activo en settings. El arranque efectivo
        // para el color inicial (WHITE) lo hace el LaunchedEffect de gameStatus
        // en GameScreenSideEffects cuando gameService.startGame() promueva el
        // estado a PLAYING.
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
        scope.launch(Dispatchers.IO) { achievementsManager.onTutorialCompleted() }
        tutorialService.resetTutorial()
        clearBoard()
    }

    fun endTutorial() {
        scope.launch(Dispatchers.IO) { achievementsManager.onTutorialCompleted() }
        tutorialService.endTutorial()
        clearBoard()
    }

    fun showNewGameDialog(color: CobColor) {
        dialogService.showNewGameDialog(color)
    }

    fun showAboutDialog() {
        dialogService.showAboutDialog()
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

    fun showGamesLibrary() = onGamesLibrary()

    fun saveGame() = onSaveGame()

    /** Delega a [IAchievementsManager] para abrir la pantalla de logros. */
    fun showAchievementsUI() = achievementsManager.showAchievementsUI()
}

fun DrawerState.closeIfOpen(scope: CoroutineScope) {
    scope.launch { if (isOpen) close() }
}