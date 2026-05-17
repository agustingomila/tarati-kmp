package com.agustin.tarati.ui.screens.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.data.database.dto.GameDto
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.PGNHeader
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.cleanGameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.HistoryEntry
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import com.agustin.tarati.features.game.GameViewModel
import com.agustin.tarati.features.game.IGameModel
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.ui.components.game.KEY_AI_ENABLED
import com.agustin.tarati.ui.components.game.KEY_BLACK_IS_AI
import com.agustin.tarati.ui.components.game.KEY_BOARD_ORIENTATION
import com.agustin.tarati.ui.components.game.KEY_DIFFICULTY_BLACK
import com.agustin.tarati.ui.components.game.KEY_DIFFICULTY_WHITE
import com.agustin.tarati.ui.components.game.KEY_GAME_HISTORY
import com.agustin.tarati.ui.components.game.KEY_GAME_STATE
import com.agustin.tarati.ui.components.game.KEY_GAME_STATUS
import com.agustin.tarati.ui.components.game.KEY_IS_EDITING
import com.agustin.tarati.ui.components.game.KEY_MANUAL_ROTATION
import com.agustin.tarati.ui.components.game.KEY_MOVE_INDEX
import com.agustin.tarati.ui.components.game.KEY_PLAYER_SIDE
import com.agustin.tarati.ui.components.game.KEY_WHITE_IS_AI
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: IGameModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var mockSettingsRepository: SettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = mockk()
        mockSettingsRepository = mockk()

        // SavedStateHandle getters
        every { savedStateHandle.get<String>(KEY_BOARD_ORIENTATION) } returns null
        every { savedStateHandle.get<Boolean>(KEY_IS_EDITING) } returns false
        every { savedStateHandle.get<Boolean>(KEY_MANUAL_ROTATION) } returns false
        every { savedStateHandle.get<String>(KEY_PLAYER_SIDE) } returns CobColor.WHITE.name
        every { savedStateHandle.get<Boolean>(KEY_AI_ENABLED) } returns true
        every { savedStateHandle.get<Boolean>(KEY_WHITE_IS_AI) } returns null  // → DataStore
        every { savedStateHandle.get<Boolean>(KEY_BLACK_IS_AI) } returns null  // → DataStore
        every { savedStateHandle.get<String>(KEY_DIFFICULTY_WHITE) } returns null // → DataStore
        every { savedStateHandle.get<String>(KEY_DIFFICULTY_BLACK) } returns null // → DataStore
        every { savedStateHandle.get<GameState>(KEY_GAME_STATE) } returns null
        every { savedStateHandle.get<List<Pair<Move, GameState>>>(KEY_GAME_HISTORY) } returns null
        every { savedStateHandle.get<Int>(KEY_MOVE_INDEX) } returns -1
        every { savedStateHandle.get<GameStatus>(KEY_GAME_STATUS) } returns GameStatus.NO_PLAYING

        // SavedStateHandle setters
        every { savedStateHandle[any<String>()] = ofType<Boolean>() } just Runs
        every { savedStateHandle[any<String>()] = ofType<String>() } just Runs
        every { savedStateHandle[any<String>()] = ofType<Int>() } just Runs
        every { savedStateHandle[any<String>()] = ofType<GameState>() } just Runs
        every { savedStateHandle[any<String>()] = ofType<StableHistoryList>() } just Runs
        every { savedStateHandle[any<String>()] = ofType<GameStatus>() } just Runs

        // SettingsRepository flows — used as DataStore fallback when SavedStateHandle returns null
        coEvery { mockSettingsRepository.whiteIsAI } returns MutableStateFlow(false)
        coEvery { mockSettingsRepository.blackIsAI } returns MutableStateFlow(true)
        coEvery { mockSettingsRepository.boardOrientation } returns
                MutableStateFlow(BoardOrientation.PORTRAIT_WHITE.name)
        coEvery { mockSettingsRepository.isManuallyRotated } returns MutableStateFlow(false)
        coEvery { mockSettingsRepository.difficultyWhite } returns MutableStateFlow(Difficulty.DEFAULT)
        coEvery { mockSettingsRepository.difficultyBlack } returns MutableStateFlow(Difficulty.DEFAULT)

        // SettingsRepository suspend setters
        coEvery { mockSettingsRepository.setWhiteIsAI(any()) } just Runs
        coEvery { mockSettingsRepository.setBlackIsAI(any()) } just Runs
        coEvery { mockSettingsRepository.setBoardOrientation(any()) } just Runs
        coEvery { mockSettingsRepository.setManuallyRotated(any()) } just Runs
        coEvery { mockSettingsRepository.setDifficultyWhite(any()) } just Runs
        coEvery { mockSettingsRepository.setDifficultyBlack(any()) } just Runs

        startKoin {
            modules(
                module {
                    single { savedStateHandle }
                    single { mockSettingsRepository }
                },
            )
        }
        viewModel = GameViewModel(
            savedStateHandle,
            mockSettingsRepository,
            mockk<IAIEngine>(relaxed = true),
        )
    }

    @After
    fun tearDown() {
        // Cancel viewModelScope before resetMain so active collectors
        // (combine/collect in init) don't dispatch on the reset main
        // dispatcher and leak exceptions into subsequent tests.
        (viewModel as ViewModel).viewModelScope.cancel()
        stopKoin()
        Dispatchers.resetMain()
    }

    // ── Game state factory tests ───────────────────────────────────────────────

    @Test
    fun initialGameState_hasCorrectSetup() {
        val gameState = initialGameState()

        assertEquals("Initial turn should be WHITE", CobColor.WHITE, gameState.currentTurn)
        assertEquals("Should have 8 cobs total", 8, gameState.cobs.size)
        assertEquals(
            "Should have 4 white cobs",
            4,
            gameState.cobs.values.count { it.color == CobColor.WHITE },
        )
        assertEquals(
            "Should have 4 black cobs",
            4,
            gameState.cobs.values.count { it.color == CobColor.BLACK },
        )
        assertTrue("C1 should have white cob", gameState.cobs[C1]?.color == CobColor.WHITE)
        assertTrue("C7 should have black cob", gameState.cobs[C7]?.color == CobColor.BLACK)
        assertTrue("D1 should have white cob", gameState.cobs[D1]?.color == CobColor.WHITE)
        assertTrue("D3 should have black cob", gameState.cobs[D3]?.color == CobColor.BLACK)
    }

    @Test
    fun cleanGameState_hasNoCobs() {
        val gameState = cleanGameState()

        assertTrue("Clean state should have no cobs", gameState.cobs.isEmpty())
        assertEquals("Turn should be WHITE by default", CobColor.WHITE, gameState.currentTurn)
    }

    @Test
    fun cleanGameState_withCustomTurn() {
        val gameState = cleanGameState(CobColor.BLACK)

        assertTrue("Clean state should have no cobs", gameState.cobs.isEmpty())
        assertEquals("Turn should be BLACK", CobColor.BLACK, gameState.currentTurn)
    }

    // ── ViewModel state tests ─────────────────────────────────────────────────

    @Test
    fun updateGameState_changesState() {
        val newState = GameState(
            cobs = mapOf(A1 to Cob(CobColor.WHITE, true)),
            currentTurn = CobColor.BLACK,
        )

        viewModel.setGame(newState)

        assertEquals("Game state should be updated", newState, viewModel.gameState.value)
    }

    @Test
    fun updateHistory_changesHistory() {
        val move = Move(C1 to B1)
        val gameState = initialGameState()
        val history = listOf(HistoryEntry(move, gameState))

        viewModel.addMove(move, gameState, onMoveRecord = {})

        assertEquals(
            "History should be updated",
            history,
            viewModel.history.value.toList(),
        )
    }

    @Test
    fun updateAIEnabled_changesAIState() {
        viewModel.updateAIEnabled(false)

        assertFalse("AI should be disabled", viewModel.aIEnabled.value)
    }

    @Test
    fun initialState_hasDefaultValues() {
        assertEquals(
            "Initial game state should match factory",
            initialGameState(),
            viewModel.gameState.value,
        )
        assertTrue(
            "Initial history should be empty",
            viewModel.history.value.toList().isEmpty(),
        )
        assertEquals("Initial move index should be -1", -1, viewModel.moveIndex.value)
        assertTrue("Initial AI should be enabled", viewModel.aIEnabled.value)
        assertEquals(
            "Initial player side should be WHITE",
            CobColor.WHITE,
            viewModel.playerSide.value,
        )
    }

    // ── Player type (Human / AI per band) tests ───────────────────────────────

    @Test
    fun initialPlayerTypes_loadFromDataStoreWhenSavedStateIsNull() {
        // SavedStateHandle returns null → falls back to DataStore defaults (white=false, black=true)
        assertFalse("White should be human (DataStore default)", viewModel.whiteIsAI.value)
        assertTrue("Black should be AI (DataStore default)", viewModel.blackIsAI.value)
    }

    @Test
    fun updatePlayerType_white_toAI() {
        viewModel.updatePlayerType(CobColor.WHITE, isAI = true)

        assertTrue("White should be AI after update", viewModel.whiteIsAI.value)
    }

    @Test
    fun updatePlayerType_black_toHuman() {
        viewModel.updatePlayerType(CobColor.BLACK, isAI = false)

        assertFalse("Black should be human after update", viewModel.blackIsAI.value)
    }

    @Test
    fun updatePlayerType_aiEnabled_reflectsBothBands() {
        // Both human → AI disabled
        viewModel.updatePlayerType(CobColor.WHITE, isAI = false)
        viewModel.updatePlayerType(CobColor.BLACK, isAI = false)
        assertFalse("aIEnabled should be false when both bands are human", viewModel.aIEnabled.value)

        // One AI → AI enabled
        viewModel.updatePlayerType(CobColor.BLACK, isAI = true)
        assertTrue("aIEnabled should be true when at least one band is AI", viewModel.aIEnabled.value)
    }

    // ── Difficulty per band tests ─────────────────────────────────────────────

    @Test
    fun initialDifficultyWhite_isDefaultFromDataStore() {
        assertEquals(
            "Initial white difficulty should be DEFAULT (from DataStore)",
            Difficulty.DEFAULT,
            viewModel.difficultyWhite.value,
        )
    }

    @Test
    fun initialDifficultyBlack_isDefaultFromDataStore() {
        assertEquals(
            "Initial black difficulty should be DEFAULT (from DataStore)",
            Difficulty.DEFAULT,
            viewModel.difficultyBlack.value,
        )
    }

    @Test
    fun updateDifficulty_white_changesFlow() {
        viewModel.updateDifficulty(CobColor.WHITE, Difficulty.HARD)

        assertEquals(
            "White difficulty should be HARD after update",
            Difficulty.HARD,
            viewModel.difficultyWhite.value,
        )
    }

    @Test
    fun updateDifficulty_black_changesFlow() {
        viewModel.updateDifficulty(CobColor.BLACK, Difficulty.CHAMPION)

        assertEquals(
            "Black difficulty should be CHAMPION after update",
            Difficulty.CHAMPION,
            viewModel.difficultyBlack.value,
        )
    }

    @Test
    fun updateDifficulty_bandsAreIndependent() {
        viewModel.updateDifficulty(CobColor.WHITE, Difficulty.EASY)
        viewModel.updateDifficulty(CobColor.BLACK, Difficulty.CHAMPION)

        assertEquals(Difficulty.EASY, viewModel.difficultyWhite.value)
        assertEquals(Difficulty.CHAMPION, viewModel.difficultyBlack.value)
    }

    // ── Board rotation tests ───────────────────────────────────────────────────

    @Test
    fun initialBoardOrientation_loadsFromDataStore() {
        assertEquals(
            "Orientation should default to PORTRAIT_WHITE from DataStore",
            BoardOrientation.PORTRAIT_WHITE,
            viewModel.boardOrientation.value,
        )
    }

    @Test
    fun isManuallyRotated_isFalseByDefault() {
        assertFalse("Manual rotation should be false on init", viewModel.isManuallyRotated.value)
    }

    @Test
    fun rotateBoardManually_setsManualRotationFlag() {
        viewModel.rotateBoardManually()

        assertTrue("Manual rotation should be true after rotate", viewModel.isManuallyRotated.value)
    }

    @Test
    fun startGame_resetsManualRotationFlag() {
        viewModel.rotateBoardManually()
        viewModel.startGame(CobColor.WHITE)

        assertFalse("Manual rotation should be reset on new game", viewModel.isManuallyRotated.value)
    }

    // ── showLogoTransition ────────────────────────────────────────────────────

    @Test
    fun showLogoTransition_isTrueByDefault() {
        assertTrue("showLogoTransition should be true on init", viewModel.showLogoTransition.value)
    }

    @Test
    fun suppressLogoTransition_setsFlagToFalse() {
        viewModel.suppressLogoTransition()

        assertFalse(
            "showLogoTransition should be false after suppress",
            viewModel.showLogoTransition.value
        )
    }

    @Test
    fun startGame_resetsLogoTransitionToTrue() {
        viewModel.suppressLogoTransition()
        viewModel.startGame(CobColor.WHITE)

        assertTrue(
            "showLogoTransition should be true after startGame",
            viewModel.showLogoTransition.value
        )
    }

    @Test
    fun importGameFromMatchDto_suppressesLogoTransition() {
        val matchDto = MatchDto(
            header = PGNHeader(),
            game = GameDto(
                initialBoardPosition = initialGameState().toPositionNotation(),
                boardPosition = initialGameState().toPositionNotation(),
                moveHistory = emptyList(),
            ),
        )

        viewModel.importGameFromMatchDto(matchDto)

        assertFalse(
            "showLogoTransition should be false after import",
            viewModel.showLogoTransition.value
        )
    }

    @Test
    fun suppressLogoTransition_thenStartGame_restoresFlag() {
        viewModel.suppressLogoTransition()
        assertFalse("Suppressed", viewModel.showLogoTransition.value)

        viewModel.startGame(CobColor.BLACK)
        assertTrue("Restored after startGame", viewModel.showLogoTransition.value)
    }
}