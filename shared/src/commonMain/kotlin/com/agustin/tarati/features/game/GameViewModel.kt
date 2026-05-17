package com.agustin.tarati.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.data.database.dto.GameDto
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.PGNHeader
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.board.rotateCW
import com.agustin.tarati.core.domain.game.manager.GameManager
import com.agustin.tarati.core.domain.game.manager.GameManagerState
import com.agustin.tarati.core.domain.game.manager.gameManagerState
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.getMatchResult
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.core.domain.game.play.getValue
import com.agustin.tarati.core.utils.putSerializable
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.ui.components.editor.EditBoardManager
import com.agustin.tarati.ui.components.game.KEY_GAME_HISTORY
import com.agustin.tarati.ui.components.game.KEY_GAME_STATE
import com.agustin.tarati.ui.components.game.KEY_GAME_STATUS
import com.agustin.tarati.ui.components.game.KEY_IS_EDITING
import com.agustin.tarati.ui.components.game.KEY_MOVE_INDEX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val sr: SettingsRepository,
    private val aiEngine: IAIEngine,
) : ViewModel(),
    IGameModel {

    // ── GameManager ───────────────────────────────────────────────────────────

    private val gameManager: GameManager by lazy {
        GameManager(GameManagerState.createInitialUiState())
    }

    // ── IGameService: delegated state flows ───────────────────────────────

    override val gameState: StateFlow<GameState>
        get() = gameManager.gameState

    override val history: StateFlow<StableHistoryList>
        get() = gameManager.history

    override val moveIndex: StateFlow<Int>
        get() = gameManager.moveIndex

    override val gameStatus: StateFlow<GameStatus>
        get() = gameManager.gameStatus

    // ── IGameModel: composable state ──────────────────────────────────────

    override val gameManagerState: State<GameManagerState>
        @Composable get() = gameManager.gameManagerState

    // ── Board orientation ─────────────────────────────────────────────────────

    /**
     * Board orientation loaded exclusively from DataStore, which survives both
     * configuration changes and app restarts.
     * [SavedStateHandle] is intentionally NOT used for this field: its bundle value
     * always shadows the DataStore value, breaking cross-session persistence.
     *
     * Starts with the compile-time default and updates asynchronously from DataStore
     * in [init] — avoids blocking the ViewModel construction thread.
     */
    private val _boardOrientation = MutableStateFlow(BoardOrientation.PORTRAIT_WHITE)
    override val boardOrientation: StateFlow<BoardOrientation> = _boardOrientation.asStateFlow()

    /**
     * True when the user has manually rotated the board via the sidebar button.
     * While true, [GameEffects] skips the automatic orientation recalculation on
     * screen rotation, preserving the user's chosen perspective.
     *
     * Loaded from DataStore (survives app restarts). [SavedStateHandle] is NOT used
     * for this field — the same reason as [_boardOrientation]: its bundle value would
     * shadow the DataStore value, breaking cross-session persistence.
     */
    private val _isManuallyRotated = MutableStateFlow(false)
    override val isManuallyRotated: StateFlow<Boolean> = _isManuallyRotated.asStateFlow()

    override fun rotateBoardManually() {
        val next = _boardOrientation.value.rotateCW()
        _boardOrientation.update { next }
        _isManuallyRotated.update { true }
        viewModelScope.launch {
            sr.setBoardOrientation(next)
            sr.setManuallyRotated(true)
        }
    }

    override fun resetManualRotation() {
        _isManuallyRotated.update { false }
        viewModelScope.launch { sr.setManuallyRotated(false) }
    }

    // ── Clipboard / paste ─────────────────────────────────────────────────────

    private val _pasteRequested = MutableStateFlow(false)
    override val pasteRequested: StateFlow<Boolean> = _pasteRequested.asStateFlow()

    private val _boardPosition = MutableStateFlow("")
    override val boardPosition: StateFlow<String> = _boardPosition.asStateFlow()

    private val _showLogoTransition = MutableStateFlow(true)
    override val showLogoTransition: StateFlow<Boolean> = _showLogoTransition.asStateFlow()

    // ── User name ─────────────────────────────────────────────────────────────

    private val _userName = MutableStateFlow("")
    override val userName: StateFlow<String> = _userName.asStateFlow()

    // ── IGameService ──────────────────────────────────────────────────────────

    override fun updateAIEnabled(newAIEnabled: Boolean) =
        playerSettings.updateAIEnabled(newAIEnabled)

    // ── Player settings ───────────────────────────────────────────────────────

    private val playerSettings by lazy {
        PlayerSettingsHolder(savedStateHandle, sr, viewModelScope)
    }

    init {
        viewModelScope.launch {
            _boardOrientation.value = BoardOrientation.valueOf(sr.boardOrientation.first())
            _isManuallyRotated.value = sr.isManuallyRotated.first()
        }
        playerSettings.loadFromDataStore()
    }

    override fun saveGameState() {
        val currentState = gameManager.getCurrentState()

        // putSerializable serializa a JSON automáticamente
        savedStateHandle.putSerializable(KEY_GAME_STATE, currentState.gameState)
        savedStateHandle.putSerializable(KEY_GAME_HISTORY, currentState.history)
        savedStateHandle.putSerializable(KEY_GAME_STATUS, currentState.gameStatus)

        // Tipos primitivos van directo
        savedStateHandle[KEY_MOVE_INDEX] = currentState.moveIndex
    }

    override fun boardPositionCopied() {
        _boardPosition.update { "" }
    }

    /**
     * Updates the board orientation in memory only.
     * Called automatically by [GameEffects] on screen rotation — must NOT write to
     * DataStore or [SavedStateHandle] to avoid overwriting the user's manual preference.
     * Only [rotateBoardManually] persists the orientation.
     */
    override fun updateBoardOrientation(newOrientation: BoardOrientation) {
        _boardOrientation.update { newOrientation }
    }

    override fun updateUserName(name: String) {
        _userName.update { name }
    }


    /**
     * Sets the Human/AI assignment for [color] without restarting the game.
     * Updates the per-band flow, [aIEnabled], and persists to both
     * [SavedStateHandle] (session) and DataStore (cross-session).
     */
    override fun updatePlayerType(color: CobColor, isAI: Boolean) =
        playerSettings.updatePlayerType(color, isAI)


    override fun updateDifficulty(color: CobColor, difficulty: Difficulty) =
        playerSettings.updateDifficulty(color, difficulty)

    override fun addMove(
        move: Move,
        nextState: GameState,
        onMoveRecord: () -> Unit,
    ) = gameManager.addMove(move, nextState, onMoveRecord)

    override fun undoMove() = gameManager.undoMove()

    override fun redoMove() = gameManager.redoMove()

    override fun moveToCurrentState() = gameManager.moveToCurrentState()

    override fun moveToIndex(index: Int) = gameManager.moveToIndex(index)

    // ── IEditBoard ────────────────────────────────────────────────────────────

    private val editBoardManager by lazy { EditBoardManager() }

    private fun clearHistory() = gameManager.clearHistory()

    private fun updateHistory(history: List<Move>, initialState: GameState = GameState.initialGameState()) =
        gameManager.updateHistory(history, initialState)

    override fun setGame(gameState: GameState) {
        clearHistory()
        gameManager.setInitialGameState(gameState)
        gameManager.updateGameState(gameState)
    }

    override fun toggleEditing() {
        editBoardManager.toggleEditing(
            currentTurn = gameManager.gameState.value.currentTurn,
            boardOrientation = _boardOrientation.value,
        )
        savedStateHandle[KEY_IS_EDITING] = editBoardManager.isEditing.value
    }

    override fun toggleEditColor() = editBoardManager.toggleEditColor()

    override fun toggleEditTurn() = editBoardManager.toggleEditTurn()

    override fun rotateEditBoard() = editBoardManager.rotateEditBoard()

    override fun togglePlayerSide() = playerSettings.togglePlayerSide()

    override fun clearEditBoard() {
        val cleanState = editBoardManager.clearEditBoard()
        gameManager.updateGameState(cleanState)
    }

    override fun editPiece(vertex: Vertex) {
        val currentState = gameManager.gameState.value
        val newState = editBoardManager.editPiece(vertex, currentState)
        gameManager.updateGameState(newState)
    }

    override fun startGameFromEditedState() {
        val currentState = gameManager.gameState.value
        if (!editBoardManager.validateDistributionForGameStart(currentState)) return
        endEditing()
        val gameStateWithTurn = currentState.copy(currentTurn = editBoardManager.editTurn.value)
        setGame(gameStateWithTurn)
        _startedFromEditedBoard.update { true }
        resumeGame()
    }

    override fun copyBoardToClipboard() {
        _boardPosition.update { gameManager.gameState.value.toPositionNotation() }
    }

    override fun pasteBoardFromClipboard(isRequested: Boolean) {
        _pasteRequested.update { isRequested }
    }

    // ── IGameActions ──────────────────────────────────────────────────────────

    override fun suppressLogoTransition() {
        _showLogoTransition.update { false }
    }

    override fun gameOver() = gameManager.updateGameStatus(GameStatus.GAME_OVER)

    override fun stopGame() {
        if (gameManager.gameStatus.value != GameStatus.GAME_OVER) {
            gameManager.updateGameStatus(GameStatus.NO_PLAYING)
        }
    }

    override fun resumeGame() = gameManager.updateGameStatus(GameStatus.PLAYING)

    override fun endEditing() {
        editBoardManager.endEditing()
        savedStateHandle[KEY_IS_EDITING] = false
    }

    override fun updateGameState(gameState: GameState) = gameManager.updateGameState(gameState)

    override fun startGame(playerSide: CobColor) {
        _showLogoTransition.update { true }
        endEditing()
        playerSettings.updatePlayerSide(playerSide)
        setGame(GameState.initialGameState())
        _startedFromEditedBoard.update { false }
        _startedFromImportedGame.update { false }
        resetManualRotation()
        resumeGame()
        saveGameState()
    }

    // ── IEditBoardManager ─────────────────────────────────────────────────────

    override val isEditing: StateFlow<Boolean> get() = editBoardManager.isEditing
    override val editColor: StateFlow<CobColor> get() = editBoardManager.editColor
    override val editTurn: StateFlow<CobColor> get() = editBoardManager.editTurn
    override val editBoardOrientation: StateFlow<BoardOrientation> get() = editBoardManager.editBoardOrientation

    // ── IPlayerManager ────────────────────────────────────────────────────────

    override val playerSide: StateFlow<CobColor> get() = playerSettings.playerSide
    override val aIEnabled: StateFlow<Boolean> get() = playerSettings.aIEnabled
    override val whiteIsAI: StateFlow<Boolean> get() = playerSettings.whiteIsAI
    override val blackIsAI: StateFlow<Boolean> get() = playerSettings.blackIsAI
    override val difficultyWhite: StateFlow<Difficulty> get() = playerSettings.difficultyWhite
    override val difficultyBlack: StateFlow<Difficulty> get() = playerSettings.difficultyBlack

    // ── IGamesLibraryManager ──────────────────────────────────────────────────

    override fun importGameFromMatchDto(matchDto: MatchDto) {
        _showLogoTransition.update { false }
        endEditing()

        val initialState = runCatching { GameState.parseBoardNotation(matchDto.game.initialBoardPosition) }
            .getOrElse { GameState.initialGameState() }

        // 1. Fix the initial game state BEFORE rebuilding history.
        //    setGame() was wrong here: it overwrites initialGameState with boardPosition
        //    (the mid-game save point), breaking undo past the first move.
        //    We only need to set the true initial state and clear history.
        gameManager.clearHistory(initialState)
        gameManager.setInitialGameState(initialState)

        // 2. Rebuild the full move history from the true initial position.
        //    updateHistory() always sets moveIndex = lastIndex, so the list
        //    shows the last move — regardless of where the game was saved.
        updateHistory(matchDto.game.moveHistory, initialState)

        // 3. Restore the save point: find the history entry whose board state
        //    matches boardPosition, then seek to it so both the board display
        //    and the move list agree on the same position.
        //    Falls back to the last entry if no match is found (safety net for
        //    games saved at the final move or with legacy boardPosition data).
        val savedBoardPosition = matchDto.game.boardPosition
        val targetIndex = gameManager.history.value.toList()
            .indexOfLast { it.gameState.toPositionNotation() == savedBoardPosition }
        gameManager.moveToIndex(targetIndex)

        _startedFromImportedGame.update { true }
        resumeGame()
    }

    /**
     * Exporta el estado actual a un [MatchDto] usando los labels de jugador
     * provistos por el caller.
     *
     * El cálculo de [whiteLabel] y [blackLabel] es responsabilidad de [GameScreen],
     * que tiene acceso al contexto de localización y al estado de configuración
     * de cada banda (IA/Humano, dificultad, nombre de usuario).
     *
     * @param whiteLabel Etiqueta del jugador blanco (ej. `"Agustín"`, `"IA (Fácil)"`).
     * @param blackLabel Etiqueta del jugador negro (ej. `"Humano"`, `"IA (Campeón)"`).
     */
    override fun exportGameToMatchDto(whiteLabel: String, blackLabel: String): MatchDto {
        val gameState = gameManager.gameState.value
        val moveHistory = gameManager.history.value

        val header = PGNHeader(
            white = whiteLabel,
            black = blackLabel,
            result = gameState.getWinner(aiEngine.positionHistory)?.getMatchResult()?.getValue()
                ?: MatchResult.UNDEFINED.getValue(),
        )

        // Estado antes del primer movimiento, fijado en setGame().
        // Para partidas normales coincide con initialGameState();
        // para partidas desde tablero editado refleja esa posición personalizada.
        val initialState = gameManager.initialGameState

        return MatchDto(
            header = header,
            game = GameDto(
                initialBoardPosition = initialState.toPositionNotation(),
                boardPosition = gameState.toPositionNotation(),
                matchResult = gameState.getWinner(aiEngine.positionHistory)?.getMatchResult()
                    ?: MatchResult.UNDEFINED,
                moveHistory = moveHistory.getMoves(),
            ),
        )
    }

    // ── startedFromEditedBoard ────────────────────────────────────────────────

    private val _startedFromEditedBoard = MutableStateFlow(false)

    /** True when this game started from a custom board position set in the editor. */
    override val startedFromEditedBoard: StateFlow<Boolean> = _startedFromEditedBoard.asStateFlow()

    private val _startedFromImportedGame = MutableStateFlow(false)

    /** True when this game was loaded from a previously saved game. */
    override val startedFromImportedGame: StateFlow<Boolean> = _startedFromImportedGame.asStateFlow()
}