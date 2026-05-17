package com.agustin.tarati.features.game

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.ai.services.displayNameRes
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.features.seasonal.ISpecialEventManager
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.features.settings.SettingsViewModel
import com.agustin.tarati.services.achievements.IAchievementsManager
import com.agustin.tarati.services.ai.AIViewModel
import com.agustin.tarati.services.ai.IAIService
import com.agustin.tarati.services.clock.ClockViewModel
import com.agustin.tarati.services.clock.IClockService
import com.agustin.tarati.services.dialogs.DialogViewModel
import com.agustin.tarati.services.dialogs.IDialogViewModel
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.services.sound.LocalSoundService
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.board_position_copied_to_clipboard
import com.agustin.tarati.shared.generated.resources.game_saved
import com.agustin.tarati.shared.generated.resources.move_history_copied_to_clipboard
import com.agustin.tarati.shared.generated.resources.player_ai
import com.agustin.tarati.shared.generated.resources.player_human
import com.agustin.tarati.ui.components.game.BoardEvents
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import com.agustin.tarati.ui.components.game.animation.BoardAnimationViewModel
import com.agustin.tarati.ui.components.game.animation.BoardGeometryViewModel
import com.agustin.tarati.ui.components.game.animation.IBoardAnimationViewModel
import com.agustin.tarati.ui.components.game.animation.IBoardGeometryViewModel
import com.agustin.tarati.ui.components.game.behaviors.BoardSelectionViewModel
import com.agustin.tarati.ui.components.game.behaviors.IBoardSelectionViewModel
import com.agustin.tarati.ui.components.sidebar.SidebarContent
import com.agustin.tarati.ui.components.turnIndicator.TurnIndicatorState
import com.agustin.tarati.ui.components.tutorial.ITutorialService
import com.agustin.tarati.ui.components.tutorial.TutorialService
import com.agustin.tarati.ui.components.tutorial.TutorialViewModel
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.getBoardColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: IGameModel = koinViewModel<GameViewModel>(),
    animationViewModel: IBoardAnimationViewModel = koinViewModel<BoardAnimationViewModel>(),
    geometryViewModel: IBoardGeometryViewModel = koinViewModel<BoardGeometryViewModel>(),
    selectViewModel: IBoardSelectionViewModel = koinViewModel<BoardSelectionViewModel>(),
    settingsViewModel: ISettingsViewModel = koinViewModel<SettingsViewModel>(),
    aiViewModel: IAIService = koinViewModel<AIViewModel>(),
    dialogViewModel: IDialogViewModel = koinViewModel<DialogViewModel>(),
    onNavigateToSettings: () -> Unit = {},
    onGamesLibrary: () -> Unit,
    onSaveGame: (match: MatchDto) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Services ─────────────────────────────────────────────────────────────
    val services = rememberGameScreenServices(animationViewModel, dialogViewModel)

    // ── Tutorial ──────────────────────────────────────────────────────────────
    val tutorialAnimationCoordinator = remember { AnimationCoordinator(animationViewModel) }
    val tutorialViewModel: TutorialViewModel = koinViewModel(
        parameters = { parametersOf(tutorialAnimationCoordinator) },
    )
    val tutorialService = remember(tutorialViewModel) { TutorialService(tutorialViewModel) }
    val tutorialState by tutorialViewModel.tutorialState.collectAsState()

    // ── Derived state ─────────────────────────────────────────────────────────
    val screenState = rememberGameScreenState(
        viewModel = viewModel,
        aiViewModel = aiViewModel,
        tutorialViewModel = tutorialViewModel,
    )

    // ── ViewModel state ───────────────────────────────────────────────────────
    val gameManagerState by viewModel.gameManagerState
    val isEditing by viewModel.isEditing.collectAsState()
    val editBoardOrientation by viewModel.editBoardOrientation.collectAsState()
    val aiEnabled by viewModel.aIEnabled.collectAsState(true)
    val playerSide by viewModel.playerSide.collectAsState(WHITE)
    val boardOrientation by viewModel.boardOrientation.collectAsState()
    val isManuallyRotated by viewModel.isManuallyRotated.collectAsState()
    val showLogoTransition by viewModel.showLogoTransition.collectAsState()
    val boardSize by geometryViewModel.boardSize.collectAsState()
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val pendingPreMove by selectViewModel.pendingPreMove.collectAsState()

    // ── Dialogs ───────────────────────────────────────────────────────────────
    val showNewGameDialog by services.dialogService.showNewGameDialog.collectAsState(false)
    val newGameColor by services.dialogService.newGameColor.collectAsState(WHITE)
    val showAboutDialog by services.dialogService.showAboutDialog.collectAsState(false)
    val showGameOverDialog by services.dialogService.showGameOverDialog.collectAsState(false)
    var gameOverDialogDismissed by rememberSaveable { mutableStateOf(false) }

    // ── Clipboard / save actions ──────────────────────────────────────────────
    val gameSavedMessage = stringResource(Res.string.game_saved)
    val aiLabel = stringResource(Res.string.player_ai)
    val humanLabel = stringResource(Res.string.player_human)
    val whiteDifficultyLabel = stringResource(screenState.evalConfigWhite.difficulty.displayNameRes)
    val blackDifficultyLabel = stringResource(screenState.evalConfigBlack.difficulty.displayNameRes)
    val saveCurrentGame: () -> Unit = {
        // Computar los labels usando el contexto de localización y el estado
        // de configuración de cada banda (whiteIsAI, blackIsAI, dificultad, nombre).
        // El ViewModel recibe los strings ya resueltos para no acoplarse a Context.
        // Resolver strings aquí — dentro del contexto Composable

        val whiteLabel = buildPlayerLabel(
            aiLabel = aiLabel,
            humanLabel = humanLabel,
            difficultyLabel = whiteDifficultyLabel,
            isAI = screenState.whiteIsAI,
            isCurrentUser = playerSide == WHITE,
            userName = settingsState.userName,
        )
        val blackLabel = buildPlayerLabel(
            aiLabel = aiLabel,
            humanLabel = humanLabel,
            difficultyLabel = blackDifficultyLabel,
            isAI = screenState.blackIsAI,
            isCurrentUser = playerSide == CobColor.BLACK,
            userName = settingsState.userName,
        )
        scope.launch {
            onSaveGame(viewModel.exportGameToMatchDto(whiteLabel, blackLabel))
            snackbarHostState.showSnackbar(
                message = gameSavedMessage,
                duration = SnackbarDuration.Short,
            )
        }
    }

    val moveHistoryCopiedMessage = localizedString(Res.string.move_history_copied_to_clipboard)
    val copyHistoryToClipboard: (List<Move>) -> Unit = { moves ->
        scope.launch {
            val success = services.clipboardHelper.copyMoveHistory(
                moves = moves,
                gameState = gameManagerState.gameState,
                playerSide = playerSide,
                aiEnabled = aiEnabled,
                positionHistory = aiViewModel.positionHistory,
            )
            if (success) {
                snackbarHostState.showSnackbar(
                    message = moveHistoryCopiedMessage,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    val boardPositionCopiedMessage = localizedString(Res.string.board_position_copied_to_clipboard)
    val copyPositionToClipboard: () -> Unit = {
        scope.launch {
            val success = services.clipboardHelper.copyBoardPosition(
                gameManagerState.gameState.toPositionNotation(),
            )
            if (success) {
                snackbarHostState.showSnackbar(
                    message = boardPositionCopiedMessage,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    // ── Clock ────────────────────────────────────────────────────────────────
    val clockService: IClockService = koinViewModel<ClockViewModel>()

    // ── Events ────────────────────────────────────────────────────────────────
    val events = rememberMainScreenEvents(
        drawerState = drawerState,
        gameService = viewModel,
        difficulty = { viewModel.difficultyWhite.value },
        playerSide = { viewModel.playerSide.value },
        whiteIsAI = { viewModel.whiteIsAI.value },
        blackIsAI = { viewModel.blackIsAI.value },
        onGamesLibrary = onGamesLibrary,
        onSaveGame = saveCurrentGame,
        onCopyMoveHistoryToClipboard = copyHistoryToClipboard,
        animationCoordinator = services.animationCoordinator,
        dialogService = services.dialogService,
        tutorialService = tutorialService,
        clockService = clockService,
        timeControlProvider = { settingsState.timeControl },
    )

    // ── Undo / Redo ───────────────────────────────────────────────────────────
    val undoRedo = rememberUndoRedoState(
        gameManagerState = gameManagerState,
        screenState = screenState,
        events = events,
        viewModel = viewModel,
        onGameOverDialogReset = {
            gameOverDialogDismissed = false
            services.dialogService.resetDialogs()
        },
    )

    // ── Move handling ─────────────────────────────────────────────────────────
    val handleMove: (Move) -> Unit = { move ->
        when {
            screenState.isTutorialActive ->
                handleTutorialMove(events, gameManagerState, move, tutorialService)

            else ->
                handleGameMove(events, gameManagerState, move, viewModel)
        }
    }

    // ── AI move result ────────────────────────────────────────────────────────
    // Collected here rather than inside GameEffects so the result reaches
    // handleGameMove without going through a Composable-scoped callback.
    // The computation runs in AIViewModel.viewModelScope and survives rotation;
    // this LaunchedEffect is the re-attachment point after the new composition
    // enters — it picks up any move that the engine emitted during the gap.
    LaunchedEffect(aiViewModel) {
        aiViewModel.pendingAIMove.collect { move ->
            if (gameManagerState.gameStatus == GameStatus.PLAYING) {
                handleGameMove(events, gameManagerState, move, viewModel)
            }
        }
    }

    // Ejecuta el pendingPreMove cuando cambia el gameState (turno humano) o el gameStatus.
    // Simétrico al LaunchedEffect de pendingAIMove; handleGameMove solo es visible aquí.
    LaunchedEffect(gameManagerState.gameState, gameManagerState.gameStatus) {
        val move = pendingPreMove ?: return@LaunchedEffect
        val state = gameManagerState.gameState

        if (gameManagerState.gameStatus != GameStatus.PLAYING) {
            selectViewModel.resetPreMove()
            return@LaunchedEffect
        }

        if (isEditing || screenState.isTutorialActive) {
            selectViewModel.resetPreMove()
            return@LaunchedEffect
        }

        // Turno AI: no limpiar; el efecto se re-disparará al volver el turno humano.
        val isAITurn = (state.currentTurn == WHITE && screenState.whiteIsAI) ||
                (state.currentTurn == CobColor.BLACK && screenState.blackIsAI)
        if (isAITurn) return@LaunchedEffect

        if (move !in state.allMovesForTurn()) {
            selectViewModel.resetPreMove()
            return@LaunchedEffect
        }

        // Pausa para que el usuario registre el move de la IA antes del suyo.
        delay(200L.milliseconds)

        // pendingPreMove no es key del efecto y puede cambiar durante el delay.
        val liveMove = selectViewModel.pendingPreMove.value
        if (liveMove != move) return@LaunchedEffect

        handleGameMove(events, gameManagerState, move, viewModel)
        selectViewModel.resetPreMove()
    }

    // Limpia el pre-move al deshabilitar la feature en Settings.
    LaunchedEffect(settingsState.preMovesEnabled) {
        if (!settingsState.preMovesEnabled) {
            selectViewModel.resetPreMove()
        }
    }

    // ── Side effects ──────────────────────────────────────────────────────────
    GameScreenSideEffects(
        gameManagerState = gameManagerState,
        screenState = screenState,
        services = services,
        events = events,
        viewModel = viewModel,
        settingsViewModel = settingsViewModel,
        animationViewModel = animationViewModel,
        snackbarHostState = snackbarHostState,
    )

    // ── AI / orientation / difficulty effects ─────────────────────────────────
    GameEffects(
        drawerState = drawerState,
        isLandscape = screenState.isLandscape,
        isManuallyRotated = isManuallyRotated,
        gameState = gameManagerState.gameState,
        aiThinkingDependencies = createAiThinkingDependencies(
            gameStatus = gameManagerState.gameStatus,
            currentTurn = gameManagerState.gameState.currentTurn,
            // Durante el tutorial ambos bandos son siempre humanos: se ignora la
            // configuración real para que AiThinkingDependencies.isAITurn nunca
            // sea true y el motor no dispare movimientos automáticos.
            whiteIsAI = if (screenState.isTutorialActive) false else screenState.whiteIsAI,
            blackIsAI = if (screenState.isTutorialActive) false else screenState.blackIsAI,
            evalConfigWhite = screenState.evalConfigWhite,
            evalConfigBlack = screenState.evalConfigBlack,
            isEditing = isEditing,
            boardHash = gameManagerState.gameState.hashBoard(),
        ),
        onAITurn = { gameState -> aiViewModel.requestAIMove(gameState) },
        onBoardOrientationChanged = viewModel::updateBoardOrientation,
        isTutorialActive = screenState.isTutorialActive,
        tutorialState = tutorialState,
        onTutorialEnd = events::resetTutorial,
    )

    val aiEngine: IAIEngine = koinInject()

    // ── UI ────────────────────────────────────────────────────────────────────
    // El SnackbarHost se coloca fuera del ModalNavigationDrawer, como último
    // hijo del Box raíz, garantizando que se renderice por encima del sidebar
    // y de cualquier otro overlay. El Scaffold de MainContent ya no tiene
    // snackbarHost — el estado se sigue compartiendo desde este nivel.

    TaratiBackground {
        GameScreenDialogs(
            gameManagerState = gameManagerState,
            undoRedo = undoRedo,
            services = services,
            events = events,
            showGameOverDialog = showGameOverDialog,
            showNewGameDialog = showNewGameDialog,
            newGameColor = newGameColor,
            showAboutDialog = showAboutDialog,
            playerSide = playerSide,
            onStartNewGame = { color -> events.startNewGame(scope, color) },
            onStartTutorial = { events.startTutorial(scope) },
            onGameOverDialogDismissed = { gameOverDialogDismissed = true },
            onGameOverDialogReset = {
                gameOverDialogDismissed = false
                services.dialogService.resetDialogs()
            },
        )

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SidebarContent(
                    gameManagerState = gameManagerState,
                    playerSide = playerSide,
                    evalConfigWhite = screenState.evalConfigWhite,
                    evalConfigBlack = screenState.evalConfigBlack,
                    aiEnabled = aiEnabled,
                    boardOrientation = boardOrientation,
                    events = events,
                    viewModel = viewModel,
                    aiEngine = aiEngine,
                    onNavigateToSettings = onNavigateToSettings,
                    onUndo = undoRedo.handleUndo,
                )
            },
        ) {
            MainContent(
                drawerState = drawerState,
                isLandscape = screenState.isLandscape,
                boardSize = boardSize,
                boardOrientation = boardOrientation,
                editBoardOrientation = editBoardOrientation,
                gameState = gameManagerState.gameState,
                moveIndex = gameManagerState.moveIndex,
                history = gameManagerState.history,
                turnState = screenState.turnState,
                canClaimDraw = screenState.canClaimDraw,
                onClaimFiftyMoveDraw = { events.claimFiftyMoveDraw(gameManagerState.gameState) },
                showLogoTransition = showLogoTransition,
                distributionState = screenState.distributionState,
                isTutorialActive = screenState.isTutorialActive,
                isAIThinking = screenState.turnState == TurnIndicatorState.AI_THINKING,
                pieceCounts = screenState.pieceCounts,
                boardEvents = object : BoardEvents {
                    override fun onMove(move: Move) = handleMove(move)
                    override fun onEditPiece(from: Vertex) = viewModel.editPiece(from)
                    override fun onResetCompleted() = Unit
                },
                events = events,
                playerManager = viewModel,
                boardManager = viewModel,
                editBoard = viewModel,
                boardVisualState = settingsState.boardVisualState,
                preMovesEnabled = settingsState.preMovesEnabled,
                animationViewModel = animationViewModel,
                geometryViewModel = geometryViewModel,
                selectViewModel = selectViewModel,
                tutorialViewModel = tutorialViewModel,
                onCopyPositionToClipboard = copyPositionToClipboard,
                onTouchIndicator = {
                    when {
                        gameManagerState.gameStatus == GameStatus.GAME_OVER ->
                            events.showNewGameDialog(playerSide)

                        else -> events.resumeGame()
                    }
                },
                // ── BottomGameBar ─────────────────────────────────────────────
                onUndo = undoRedo.handleUndo,
                onRedo = undoRedo.handleRedo,
                onMoveToCurrent = undoRedo.handleMoveToCurrent,
                onMoveToIndex = undoRedo.handleMoveToIndex,
            )
        }

        // ── SnackbarHost: último hijo del TaratiBackground ───────────────────
        // Al estar fuera del ModalNavigationDrawer y ser el último hijo en Z-order,
        // se renderiza por encima del sidebar y de cualquier otro overlay.
        // systemBarsPadding() garantiza que no quede oculto por la barra de
        // navegación del sistema.
        val boardColors = getBoardColors()
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding(),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = boardColors.neutralColor,
                contentColor = if (boardColors.neutralColor.luminance() > 0.35f)
                    Color(0xFF1A1A1A) else Color(0xFFF5F0EB),
                actionColor = boardColors.whiteCobColor,
            )
        }
    }
}

@Composable
fun rememberMainScreenEvents(
    drawerState: DrawerState,
    gameService: IGameService,
    difficulty: () -> Difficulty,
    playerSide: () -> CobColor,
    whiteIsAI: () -> Boolean,
    blackIsAI: () -> Boolean,
    onGamesLibrary: () -> Unit,
    onSaveGame: () -> Unit,
    onCopyMoveHistoryToClipboard: (List<Move>) -> Unit,
    animationCoordinator: AnimationCoordinator,
    clockService: IClockService,
    timeControlProvider: () -> TimeControlMode,
    aiEngine: IAIEngine = koinInject(),
    dialogService: IDialogViewModel = koinInject(),
    tutorialService: ITutorialService,
    achievementsManager: IAchievementsManager = koinInject(),
    specialEventManager: ISpecialEventManager = koinInject(),
): GameEvents {
    val scope = rememberCoroutineScope()
    val soundService = LocalSoundService.current

    return remember {
        GameEvents(
            scope = scope,
            drawerState = drawerState,
            gameService = gameService,
            difficulty = difficulty,
            playerSide = playerSide,
            whiteIsAI = whiteIsAI,
            blackIsAI = blackIsAI,
            onGamesLibrary = onGamesLibrary,
            onSaveGame = onSaveGame,
            onCopyMovesToClipboard = onCopyMoveHistoryToClipboard,
            animationCoordinator = animationCoordinator,
            aiEngine = aiEngine,
            tutorialService = tutorialService,
            soundService = soundService,
            dialogService = dialogService,
            achievementsManager = achievementsManager,
            specialEventManager = specialEventManager,
            clockService = clockService,
            timeControlProvider = timeControlProvider,
        )
    }
}

// ── Move handling helpers ─────────────────────────────────────────────────────

internal fun handleGameMove(
    events: GameEvents,
    gameManagerState: GameManagerState,
    move: Move,
    viewModel: IGameModel,
) {
    val status = gameManagerState.gameStatus
    val state = gameManagerState.gameState

    if (status != GameStatus.PLAYING && state.isInitialState(viewModel.playerSide.value)) {
        viewModel.resumeGame()
    }

    events.applyMove(move, state)
}

private fun handleTutorialMove(
    events: GameEvents,
    gameManagerState: GameManagerState,
    move: Move,
    tutorialViewModel: TutorialService,
) {
    val gameState = gameManagerState.gameState

    tutorialViewModel.onMoveAttempted(
        move = move,
        onMoveAccepted = {
            events.applyTutorialMove(move, gameState)
            tutorialViewModel.nextStep()
        },
        onMoveRejected = {
            events.applyTutorialRejectedMove(move, gameState)
            tutorialViewModel.requestUserInteraction(it)
        },
    )
}

private fun createAiThinkingDependencies(
    gameStatus: GameStatus,
    currentTurn: CobColor,
    whiteIsAI: Boolean,
    blackIsAI: Boolean,
    evalConfigWhite: EvaluationConfig,
    evalConfigBlack: EvaluationConfig,
    isEditing: Boolean,
    boardHash: String,
): AiThinkingDependencies = AiThinkingDependencies(
    gameStatus = gameStatus,
    currentTurn = currentTurn,
    whiteIsAI = whiteIsAI,
    blackIsAI = blackIsAI,
    currentTurnConfig = if (currentTurn == WHITE) evalConfigWhite else evalConfigBlack,
    isEditing = isEditing,
    boardHash = boardHash,
)

// ── Player label helper ───────────────────────────────────────────────────────

/**
 * Construye la etiqueta descriptiva de un jugador para guardar en el [MatchDto].
 *
 * Reglas:
 * - IA → `"IA (Fácil)"` / `"IA (Campeón)"` etc., usando el string localizado del nivel.
 * - Humano que es el usuario actual → nombre configurado, o `"Humano"` si está vacío.
 * - Humano oponente (partida local 2 jugadores) → `"Humano"`.
 *
 * @param isAI          True si esta banda es controlada por la IA.
 * @param isCurrentUser True si esta banda corresponde al jugador en el dispositivo.
 * @param userName      Nombre configurado en ajustes (puede estar vacío).
 */
private fun buildPlayerLabel(
    aiLabel: String,
    humanLabel: String,
    difficultyLabel: String,
    isAI: Boolean,
    isCurrentUser: Boolean,
    userName: String,
): String = when {
    isAI -> "$aiLabel ($difficultyLabel)"
    isCurrentUser -> userName.ifBlank { humanLabel }
    else -> humanLabel
}