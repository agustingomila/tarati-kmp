package com.agustin.tarati.features.game


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalWindowInfo
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.PieceCounts
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.tutorial.TutorialState
import com.agustin.tarati.services.ai.IAIService
import com.agustin.tarati.ui.components.editor.DistributionState
import com.agustin.tarati.ui.components.turnIndicator.TurnIndicatorState
import com.agustin.tarati.ui.components.tutorial.ITutorialViewModel

/**
 * Agrupa todos los estados derivados de [GameScreen].
 *
 * Centralizar aquí la derivación permite que [GameScreen] solo construya
 * este objeto y lo pase a sus hijos, sin dispersar la lógica de derivación
 * a lo largo del composable principal.
 */
class GameScreenState(
    val isLandscape: Boolean,
    val evalConfigWhite: EvaluationConfig,
    val evalConfigBlack: EvaluationConfig,
    val turnState: TurnIndicatorState,
    val canClaimDraw: Boolean,
    val pieceCounts: PieceCounts,
    val distributionState: DistributionState,
    val isTutorialActive: Boolean,
    val whiteIsAI: Boolean,
    val blackIsAI: Boolean,
)

/**
 * @param tutorialViewModel instancia de [ITutorialViewModel], anotada con [@Stable],
 * lo que permite al compilador de Compose inferir estabilidad sin warnings.
 * El flow se colecta internamente para que [derivedStateOf] reaccione a sus cambios.
 */
@Composable
fun rememberGameScreenState(
    viewModel: IGameModel,
    aiViewModel: IAIService,
    tutorialViewModel: ITutorialViewModel,
    /** True while an online opponent is expected to move. Drives [TurnIndicatorState.AI_THINKING]. */
    isWaitingForOpponent: Boolean = false,
    /** True while spectating a live game. Both players are remote → indicator spins for both turns. */
    isSpectating: Boolean = false,
): GameScreenState {
    val windowInfo = LocalWindowInfo.current

    val gameManagerState by viewModel.gameManagerState
    val isEditing by viewModel.isEditing.collectAsState()
    val playerSide by viewModel.playerSide.collectAsState(WHITE)

    val isAIThinking by aiViewModel.isAIThinking.collectAsState(false)

    // Collected as Compose State so derivedStateOf reacts to emissions
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()

    val isLandscape = windowInfo.containerSize.width > windowInfo.containerSize.height

    // Per-band AI flags — sourced directly from the ViewModel so they update
    // immediately when the user toggles Human/AI in the sidebar mid-game.
    val whiteIsAI by viewModel.whiteIsAI.collectAsState()
    val blackIsAI by viewModel.blackIsAI.collectAsState()

    // Per-side evaluation configs driven by the per-band difficulty flows in the ViewModel,
    // not by SettingsViewModel, so they also update immediately on sidebar changes.
    val difficultyWhite by viewModel.difficultyWhite.collectAsState()
    val difficultyBlack by viewModel.difficultyBlack.collectAsState()
    val evalConfigWhite = EvaluationConfig.getByDifficulty(difficultyWhite)
    val evalConfigBlack = EvaluationConfig.getByDifficulty(difficultyBlack)

    val isTutorialActive by remember {
        derivedStateOf {
            tutorialState != TutorialState.Idle && tutorialState != TutorialState.Completed
        }
    }

    // rememberUpdatedState garantiza que derivedStateOf lea siempre el valor actual
    // de estos parámetros, aunque el remember del bloque no tenga clave explícita.
    // Sin esto quedan capturados por cierre en el primer valor y el bloque no se
    // re-evalúa cuando cambian.
    val currentIsWaitingForOpponent by rememberUpdatedState(isWaitingForOpponent)
    val currentIsSpectating by rememberUpdatedState(isSpectating)

    val turnState: TurnIndicatorState by remember {
        derivedStateOf {
            val state = gameManagerState.gameState
            val status = gameManagerState.gameStatus
            val isAITurn = (state.currentTurn == WHITE && whiteIsAI) ||
                    (state.currentTurn == BLACK && blackIsAI)
            when {
                // Espectando una partida en vivo: ambos jugadores son remotos →
                // el indicador gira siempre. No se consulta el gameManagerState local
                // porque su status no refleja el estado real de la partida observada.
                currentIsSpectating -> TurnIndicatorState.AI_THINKING

                // Turno del oponente en partida online → gira
                currentIsWaitingForOpponent -> TurnIndicatorState.AI_THINKING

                // Spinning during AI computation AND while waiting for the pre-think delay
                // (isAITurn + status PLAYING covers AI vs AI between moves so the logo
                // rotates continuously and direction changes are visible without interruption).
                isAIThinking || (isAITurn && status == GameStatus.PLAYING) -> TurnIndicatorState.AI_THINKING
                // AI turn waiting for user to trigger: always show NEUTRAL (clickeable)
                // regardless of whether the board is in initial state.
                isAITurn && status != GameStatus.PLAYING -> TurnIndicatorState.NEUTRAL
                // Non-AI, non-playing: show NEUTRAL unless the board is in the
                // initial setup (where NEUTRAL starts a new game on tap).
                status != GameStatus.PLAYING && !state.isInitialState(playerSide) ->
                    TurnIndicatorState.NEUTRAL

                !isAITurn && state.getForcedPromotions().isNotEmpty() ->
                    TurnIndicatorState.MUST_PROMOTE

                else -> TurnIndicatorState.HUMAN_TURN
            }
        }
    }

    val canClaimDraw: Boolean by remember {
        derivedStateOf {
            val state = gameManagerState.gameState
            val isAITurn = (state.currentTurn == WHITE && whiteIsAI) ||
                    (state.currentTurn == BLACK && blackIsAI)
            !isAITurn &&
                    gameManagerState.gameStatus == GameStatus.PLAYING &&
                    !isEditing && !isTutorialActive &&
                    state.canClaimFiftyMoveDraw()
        }
    }

    val pieceCounts = remember(gameManagerState.gameState) {
        val gameState = gameManagerState.gameState
        PieceCounts(
            white = gameState.cobs.values.count { it.color == WHITE },
            black = gameState.cobs.values.count { it.color == BLACK },
        )
    }

    val distributionState = remember(pieceCounts) {
        DistributionState.fromPieceCounts(pieceCounts)
    }

    return remember(
        isLandscape,
        evalConfigWhite,
        evalConfigBlack,
        turnState,
        canClaimDraw,
        pieceCounts,
        distributionState,
        isTutorialActive,
        whiteIsAI,
        blackIsAI,
    ) {
        GameScreenState(
            isLandscape = isLandscape,
            evalConfigWhite = evalConfigWhite,
            evalConfigBlack = evalConfigBlack,
            turnState = turnState,
            canClaimDraw = canClaimDraw,
            pieceCounts = pieceCounts,
            distributionState = distributionState,
            isTutorialActive = isTutorialActive,
            whiteIsAI = whiteIsAI,
            blackIsAI = blackIsAI,
        )
    }
}