package com.agustin.tarati.features.game

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.services.clock.ClockViewModel
import com.agustin.tarati.services.clock.IClockService
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.board_position_copied_to_clipboard
import com.agustin.tarati.ui.components.game.animation.IBoardAnimationViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable sin UI que centraliza todos los [LaunchedEffect] de [GameScreen]:
 * persistencia, tutorial automático, promoción forzada, tablas por 50 movimientos,
 * clipboard, fin de partida, reloj y sincronización de settings.
 *
 * El fin de partida se dispara desde [IBoardAnimationViewModel.gameOverReady],
 * que emite exactamente una vez por fin de partida, después de que la animación
 * del movimiento terminal concluye:
 *
 * ```
 * última animación → gameOverReady → animateGameOver() → events.gameOver()
 * ```
 *
 * Para fines sin movimiento animado (tablas por 50 movimientos),
 * [AnimationEvent.NotifyGameOver]
 * emite [gameOverReady] de forma inmediata.
 */
@Composable
fun GameScreenSideEffects(
    aiEngine: IAIEngine = koinInject(),
    clockService: IClockService = koinViewModel<ClockViewModel>(),
    gameManagerState: GameManagerState,
    screenState: GameScreenState,
    services: GameScreenServices,
    events: GameEvents,
    viewModel: IGameModel,
    settingsViewModel: ISettingsViewModel,
    animationViewModel: IBoardAnimationViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()

    val isEditing by viewModel.isEditing.collectAsState()
    val aiEnabled by viewModel.aIEnabled.collectAsState(true)
    val playerSide by viewModel.playerSide.collectAsState()
    val boardOrientation by viewModel.boardOrientation.collectAsState()
    val pasteRequested by viewModel.pasteRequested.collectAsState()
    val boardPosition by viewModel.boardPosition.collectAsState()
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val hasTutorialBeenSeen by settingsViewModel.hasTutorialBeenSeen.collectAsState()

    // Persiste el estado de partida cuando cambia el gameManagerState.
    LaunchedEffect(gameManagerState) {
        viewModel.saveGameState()
    }

    // Persiste cuando cambian las preferencias de configuración de partida.
    LaunchedEffect(isEditing, playerSide, aiEnabled, boardOrientation) {
        viewModel.saveGameState()
    }

    // Lanza el tutorial automáticamente en el primer arranque.
    // hasTutorialBeenSeen arranca en `true` por defecto, así que este efecto
    // no dispara nada hasta que DataStore confirme que aún no fue visto.
    LaunchedEffect(hasTutorialBeenSeen) {
        if (!hasTutorialBeenSeen) {
            settingsViewModel.markTutorialSeen()
            events.startTutorial(scope)
        }
    }

    // Autoaplica promoción forzada cuando hay un único candidato.
    // Si hay múltiples candidatos, el jugador debe seleccionar manualmente.
    //
    // Solo se autoaplica en turno HUMANO. En turno de IA (incluso en AI vs AI)
    // la IA maneja la promoción vía allMovesForTurn() → getNextMove().
    // Usar whiteIsAI/blackIsAI en lugar de (playerSide == currentTurn) para que
    // AI vs AI no active este bloque (el cual causaría una doble-aplicación con
    // la promoción que el motor ya seleccionó).
    LaunchedEffect(gameManagerState.gameState) {
        val state = gameManagerState.gameState
        val isCurrentTurnHuman =
            (state.currentTurn == CobColor.WHITE && !screenState.whiteIsAI) ||
                    (state.currentTurn == CobColor.BLACK && !screenState.blackIsAI)
        if (!isCurrentTurnHuman || isEditing || screenState.isTutorialActive) return@LaunchedEffect
        if (gameManagerState.gameStatus != GameStatus.PLAYING) return@LaunchedEffect

        val forcedPromotions = state.getForcedPromotions()
        if (forcedPromotions.size == 1) {
            handleGameMove(events, gameManagerState, forcedPromotions.first(), viewModel)
        }
    }

    // La IA reclama tablas por la regla de 50 movimientos automáticamente.
    LaunchedEffect(gameManagerState.gameState) {
        val state = gameManagerState.gameState
        val isCurrentTurnAI = (state.currentTurn == CobColor.WHITE && screenState.whiteIsAI) ||
                (state.currentTurn == CobColor.BLACK && screenState.blackIsAI)
        if (!isCurrentTurnAI || isEditing || screenState.isTutorialActive) return@LaunchedEffect
        if (gameManagerState.gameStatus != GameStatus.PLAYING) return@LaunchedEffect
        if (state.canClaimFiftyMoveDraw()) {
            events.claimFiftyMoveDraw(state)
        }
    }

    // ── Fin de partida ────────────────────────────────────────────────────────
    //
    // rememberUpdatedState garantiza que el lambda de collect siempre lee los
    // valores más recientes de cada parámetro, aunque LaunchedEffect(Unit) no
    // se reinicie en cada recomposición.
    val latestGameManagerState by rememberUpdatedState(gameManagerState)
    val latestScreenState by rememberUpdatedState(screenState)
    val latestIsEditing by rememberUpdatedState(isEditing)

    // gameOverReady emite después de que la animación del último movimiento termina.
    // Para fines sin movimiento animado, notifyGameOver() emite antes de la recomposición:
    // leer gameStatus directamente del StateFlow evita el return@collect prematuro.
    LaunchedEffect(Unit) {
        animationViewModel.gameOverReady.collect {
            val status = viewModel.gameStatus.value
            if (status != GameStatus.GAME_OVER) return@collect
            if (latestScreenState.isTutorialActive || latestIsEditing) return@collect
            val gameState = viewModel.gameState.value
            animationViewModel.animateGameOver(gameState.getMatchState(aiEngine.positionHistory))
            events.gameOver(scope)
        }
    }

    // Transiciona el reloj con cada cambio de gameStatus. Las transferencias
    // turno-a-turno (applyIncrement + startClockFor) las maneja GameEvents.applyMove
    // porque gameStatus permanece en PLAYING entre movimientos.
    LaunchedEffect(gameManagerState.gameStatus) {
        val status = gameManagerState.gameStatus
        val clockState = clockService.clockState.value
        when (status) {
            GameStatus.PLAYING -> {
                if (clockState.activeColor == null) {
                    clockService.startClockFor(gameManagerState.gameState.currentTurn)
                } else if (!clockState.running) {
                    clockService.resumeClock()
                }
            }

            GameStatus.NO_PLAYING -> {
                if (clockState.running) clockService.pauseClock()
            }

            GameStatus.GAME_OVER -> clockService.stopClock()
        }
    }

    // El guard idempotente de onTimeout evita drops espurios — no duplicar aquí.
    LaunchedEffect(Unit) {
        clockService.timeoutEvents.collect { loser ->
            if (latestIsEditing || latestScreenState.isTutorialActive) return@collect
            events.onTimeout(loser)
        }
    }

    // Al arrancar siempre aplica (firstClockSync) aunque haya partida guardada,
    // ya que el ClockState no se persiste. Cambios posteriores respetan la
    // partida activa para no borrar tiempo acumulado.
    var firstClockSync by remember { mutableStateOf(true) }
    LaunchedEffect(settingsState.timeControl) {
        if (firstClockSync || gameManagerState.gameStatus != GameStatus.PLAYING) {
            clockService.resetClock(settingsState.timeControl)
        }
        firstClockSync = false
    }

    // Aplica una posición pegada desde el portapapeles.
    LaunchedEffect(pasteRequested) {
        if (pasteRequested) {
            viewModel.pasteBoardFromClipboard(false)
            val newGameState = services.clipboardHelper.pasteBoardPosition()
            if (newGameState != null) {
                viewModel.setGame(newGameState)
            }
        }
    }

    // Copia la posición del tablero al portapapeles cuando el ViewModel la emite.
    val boardPositionCopiedMessage = localizedString(Res.string.board_position_copied_to_clipboard)
    LaunchedEffect(boardPosition) {
        if (boardPosition == "") return@LaunchedEffect
        viewModel.boardPositionCopied()
        services.clipboardHelper.copyBoardPosition(boardPosition)
        snackbarHostState.showSnackbar(
            message = boardPositionCopiedMessage,
            duration = SnackbarDuration.Short,
        )
    }

    // Sincroniza el flag de efectos animados con el ViewModel de animación.
    LaunchedEffect(settingsState.boardVisualState.animateEffects) {
        animationViewModel.updateAnimateEffects(settingsState.boardVisualState.animateEffects)
    }

    // Sincroniza el estilo de animación de conversión con el ViewModel de animación.
    LaunchedEffect(settingsState.boardVisualState.conversionAnimationStyle) {
        animationViewModel.updateConversionAnimationStyle(
            settingsState.boardVisualState.conversionAnimationStyle
        )
    }

    // Propaga el nombre de usuario al ViewModel de juego.
    LaunchedEffect(settingsState.userName) {
        viewModel.updateUserName(settingsState.userName)
    }
}