package com.agustin.tarati.desktop.features.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import com.agustin.tarati.features.game.IGameModel
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.ui.components.editor.EditBoardManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Desktop implementation of [IGameModel] without SavedStateHandle.
 * 
 * Unlike the Android version, this ViewModel doesn't need to serialize game state
 * to survive configuration changes (screen rotation) or process death, because
 * Desktop apps don't have those lifecycle events. When the user closes the window,
 * the app terminates and all in-memory state is lost — this is normal Desktop behavior.
 * 
 * ## Key differences from Android GameViewModel:
 * 
 * - **No SavedStateHandle**: All state lives in [MutableStateFlow] in memory.
 * - **saveGameState()**: No-op — Desktop doesn't need session state restoration.
 * - **Board orientation**: Loaded from [SettingsRepository] on init, updated in-memory only.
 * - **Player settings**: Managed by [DesktopPlayerSettingsHolder] without SavedStateHandle.
 * - **Edit mode**: Tracked in-memory only (no persistence across app restarts).
 * 
 * ## What persists across app restarts:
 * 
 * Only settings stored in [SettingsRepository]:
 * - Board orientation (last manual rotation)
 * - Player AI flags (whiteIsAI, blackIsAI)
 * - Difficulty levels per side
 * - App theme, palette, etc.
 * 
 * ## What doesn't persist:
 * 
 * - Current game state (board position, move history)
 * - Edit mode state
 * - Turn indicator state
 * - User's in-progress game
 * 
 * This matches standard Desktop app UX: closing the window loses unsaved work.
 * Future enhancement: add File > Save Game / Load Game for persistent storage.
 */
class DesktopGameViewModel(
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
     * Board orientation loaded from SettingsRepository on init.
     * Updates are stored in-memory and persisted to SettingsRepository only
     * when the user manually rotates via [rotateBoardManually].
     */
    private val _boardOrientation = MutableStateFlow(BoardOrientation.PORTRAIT_WHITE)
    override val boardOrientation: StateFlow<BoardOrientation> = _boardOrientation.asStateFlow()

    /**
     * True when the user has manually rotated the board.
     * While true, automatic orientation updates are ignored.
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
        DesktopPlayerSettingsHolder(sr, viewModelScope)
    }

    init {
        viewModelScope.launch {
            _boardOrientation.value = BoardOrientation.valueOf(sr.boardOrientation.first())
            _isManuallyRotated.value = sr.isManuallyRotated.first()
        }
        playerSettings.loadFromDataStore()
    }

    /**
     * No-op in Desktop: there's no SavedStateHandle to persist to.
     * Desktop apps don't need session state restoration because:
     * 1. No screen rotation (no Activity recreation)
     * 2. No process death (user closes window → app terminates cleanly)
     */
    override fun saveGameState() = Unit

    override fun boardPositionCopied() {
        _boardPosition.update { "" }
    }

    /**
     * Updates the board orientation in memory only.
     * Called automatically by GameEffects on screen rotation — must NOT write to
     * SettingsRepository to avoid overwriting the user's manual preference.
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
     * Updates the per-band flow, [aIEnabled], and persists to SettingsRepository.
     */
    override fun updatePlayerType(color: CobColor, isAI: Boolean) {
        playerSettings.updatePlayerType(color, isAI)
    }

    override fun updateDifficulty(color: CobColor, difficulty: Difficulty) {
        playerSettings.updateDifficulty(color, difficulty)
    }

    // ── IGameActions ──────────────────────────────────────────────────────────

    override fun addMove(
        move: Move,
        nextState: GameState,
        onMoveRecord: () -> Unit,
    ) = gameManager.addMove(move, nextState, onMoveRecord)

    override fun undoMove() = gameManager.undoMove()

    override fun redoMove() = gameManager.redoMove()

    override fun moveToCurrentState() = gameManager.moveToCurrentState()

    override fun moveToIndex(index: Int) = gameManager.moveToIndex(index)

    // Private helpers for history management

    private fun clearHistory() = gameManager.clearHistory()

    private fun updateHistory(history: List<Move>, initialState: GameState = GameState.initialGameState()) =
        gameManager.updateHistory(history, initialState)

    override fun setGame(gameState: GameState) {
        clearHistory()
        gameManager.setInitialGameState(gameState)
        gameManager.updateGameState(gameState)
    }

    // ── Edit Board ────────────────────────────────────────────────────────────

    private val editBoardManager by lazy { EditBoardManager() }

    override fun toggleEditing() {
        editBoardManager.toggleEditing(
            currentTurn = gameManager.gameState.value.currentTurn,
            boardOrientation = _boardOrientation.value,
        )
        // No SavedStateHandle to persist to in Desktop
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
        // No SavedStateHandle to persist to in Desktop
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
        // No saveGameState() call in Desktop — no persistence needed
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
        gameManager.clearHistory(initialState)
        gameManager.setInitialGameState(initialState)

        // 2. Rebuild the full move history from the true initial position.
        updateHistory(matchDto.game.moveHistory, initialState)

        // 3. Restore the save point: find the history entry whose board state
        //    matches boardPosition, then seek to it.
        val savedBoardPosition = matchDto.game.boardPosition
        val targetIndex = gameManager.history.value.toList()
            .indexOfLast { it.gameState.toPositionNotation() == savedBoardPosition }
        gameManager.moveToIndex(targetIndex)

        _startedFromImportedGame.update { true }
        resumeGame()
    }

    /**
     * Exports the current game state to a [MatchDto] using the provided player labels.
     * 
     * @param whiteLabel White player label (e.g., "Agustín", "AI (Easy)")
     * @param blackLabel Black player label (e.g., "Human", "AI (Champion)")
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

        // State before the first move, set in setGame().
        // For normal games this matches initialGameState();
        // for games started from edited board, reflects that custom position.
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
