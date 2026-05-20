package com.agustin.tarati.features.online.game


import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.description
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.time.TimeControl
import com.agustin.tarati.network.client.OnlineGameClient
import com.agustin.tarati.network.models.MatchmakingState
import com.agustin.tarati.network.models.MatchmakingTicket
import com.agustin.tarati.network.models.OnlineGame
import com.agustin.tarati.network.models.OnlineGameStatus
import com.agustin.tarati.network.protocol.PlayerInfo
import com.agustin.tarati.network.protocol.TimeControlInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

/**
 * Tests unitarios para OnlineGameViewModel.
 *
 * Verifica:
 * - Matchmaking (start/cancel)
 * - Gameplay: movimientos, resign, tablas — con y sin partida activa
 * - Comportamiento silencioso cuando no hay partida activa
 * - Estado de la partida y helpers de conveniencia
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OnlineGameViewModelTest {

    private lateinit var mockOnlineClient: OnlineGameClient
    private lateinit var viewModel: OnlineGameViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val currentGameFlow = MutableStateFlow<OnlineGame?>(null)
    private val matchmakingStateFlow = MutableStateFlow<MatchmakingState>(MatchmakingState.Idle)

    private val testTicket = MatchmakingTicket(
        ticketId = "ticket_123",
        timeControl = TimeControl.BLITZ.key,
        rated = true,
        estimatedWaitTime = 30,
        joinedAt = Clock.System.now().toEpochMilliseconds()
    )

    private val testOpponent = PlayerInfo(
        userId = "opponent_1",
        username = "opponent_player",
        rating = 1600
    )

    private val testTimeControl = TimeControlInfo(
        label = TimeControl.BLITZ.key,
        initial = 300,
        increment = 3
    )

    private fun makeGame(gameId: String = "game_123") = OnlineGame(
        gameId = gameId,
        opponentInfo = testOpponent,
        yourColor = WHITE.description,
        gameState = GameState.initialGameState(),
        status = OnlineGameStatus.InProgress,
        timeControl = testTimeControl,
        isRated = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        currentGameFlow.value = null
        matchmakingStateFlow.value = MatchmakingState.Idle

        mockOnlineClient = mockk(relaxed = true) {
            every { currentGame } returns currentGameFlow
            every { matchmakingState } returns matchmakingStateFlow
        }

        viewModel = OnlineGameViewModel(mockOnlineClient)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    fun `initial state has no active game`() {
        assertNull(viewModel.currentGame.value)
        assertFalse(viewModel.hasActiveOnlineGame)
        assertEquals(MatchmakingState.Idle, viewModel.matchmakingState.value)
        assertFalse(viewModel.isSearchingMatch)
    }

    // ── Matchmaking ───────────────────────────────────────────────────────────

    @Test
    fun `startMatchmaking calls client and returns success`() = runTest {
        coEvery { mockOnlineClient.joinMatchmaking(any(), any()) } answers {
            matchmakingStateFlow.value = MatchmakingState.Searching(testTicket)
        }

        val result = viewModel.startMatchmaking(TimeControl.BLITZ.key, rated = true)
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals("ticket_123", result.getOrNull())
        coVerify { mockOnlineClient.joinMatchmaking(TimeControl.BLITZ.key, true) }
    }

    @Test
    fun `startMatchmaking with error returns failure`() = runTest {
        coEvery { mockOnlineClient.joinMatchmaking(any(), any()) } throws Exception("Matchmaking failed")

        val result = viewModel.startMatchmaking(TimeControl.BLITZ.key, rated = true)
        advanceUntilIdle()

        assertTrue(result.isFailure)
        assertEquals("Matchmaking failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `cancelMatchmaking calls client when searching`() = runTest {
        matchmakingStateFlow.value = MatchmakingState.Searching(testTicket)

        viewModel.cancelMatchmaking()
        advanceUntilIdle()

        coVerify { mockOnlineClient.cancelMatchmaking() }
    }

    @Test
    fun `cancelMatchmaking does nothing when not searching`() = runTest {
        assertEquals(MatchmakingState.Idle, matchmakingStateFlow.value)

        viewModel.cancelMatchmaking()
        advanceUntilIdle()

        coVerify(exactly = 0) { mockOnlineClient.cancelMatchmaking() }
    }

    // ── makeOnlineMove ────────────────────────────────────────────────────────

    @Test
    fun `makeOnlineMove calls client when game is active`() = runTest {
        currentGameFlow.value = makeGame()
        val move = Move(from = A1, to = B2)

        viewModel.makeOnlineMove(move)
        advanceUntilIdle()

        coVerify { mockOnlineClient.makeMove(move) }
    }

    @Test
    fun `makeOnlineMove does nothing when no active game`() = runTest {
        assertNull(currentGameFlow.value)
        val move = Move(from = A1, to = B2)

        viewModel.makeOnlineMove(move)
        advanceUntilIdle()

        coVerify(exactly = 0) { mockOnlineClient.makeMove(any()) }
    }

    // ── resign ────────────────────────────────────────────────────────────────

    @Test
    fun `resign calls client when game is active`() = runTest {
        currentGameFlow.value = makeGame()

        viewModel.resign()
        advanceUntilIdle()

        coVerify { mockOnlineClient.resign() }
    }

    @Test
    fun `resign does nothing when no active game`() = runTest {
        assertNull(currentGameFlow.value)

        viewModel.resign()
        advanceUntilIdle()

        coVerify(exactly = 0) { mockOnlineClient.resign() }
    }

    // ── offerDraw ─────────────────────────────────────────────────────────────

    @Test
    fun `offerDraw calls client when game is active`() = runTest {
        currentGameFlow.value = makeGame()

        viewModel.offerDraw()
        advanceUntilIdle()

        coVerify { mockOnlineClient.offerDraw() }
    }

    @Test
    fun `offerDraw does nothing when no active game`() = runTest {
        assertNull(currentGameFlow.value)

        viewModel.offerDraw()
        advanceUntilIdle()

        coVerify(exactly = 0) { mockOnlineClient.offerDraw() }
    }

    // ── respondToDraw ─────────────────────────────────────────────────────────

    @Test
    fun `respondToDraw calls client when game is active`() = runTest {
        currentGameFlow.value = makeGame()

        viewModel.respondToDraw(accept = true)
        advanceUntilIdle()

        coVerify { mockOnlineClient.respondToDraw(true) }
    }

    @Test
    fun `respondToDraw does nothing when no active game`() = runTest {
        assertNull(currentGameFlow.value)

        viewModel.respondToDraw(accept = true)
        advanceUntilIdle()

        coVerify(exactly = 0) { mockOnlineClient.respondToDraw(any()) }
    }

    // ── syncOnlineStateToLocal ────────────────────────────────────────────────

    @Test
    fun `syncOnlineStateToLocal is a no-op — board sync is delegated to GameScreen`() {
        // La sincronización del tablero se maneja en GameScreen via LaunchedEffect.
        // Verificamos que no lanza excepciones.
        viewModel.syncOnlineStateToLocal(GameState.initialGameState())
    }

    // ── clearOnlineGame ───────────────────────────────────────────────────────

    @Test
    fun `clearOnlineGame calls client`() {
        currentGameFlow.value = makeGame()

        viewModel.clearOnlineGame("game_123")

        verify { mockOnlineClient.clearCurrentGame("game_123") }
    }

    // ── Helpers de conveniencia ───────────────────────────────────────────────

    @Test
    fun `hasActiveOnlineGame returns true when game exists`() {
        currentGameFlow.value = makeGame()
        assertTrue(viewModel.hasActiveOnlineGame)
    }

    @Test
    fun `hasActiveOnlineGame returns false when no game`() {
        assertNull(currentGameFlow.value)
        assertFalse(viewModel.hasActiveOnlineGame)
    }

    @Test
    fun `isSearchingMatch returns true when in Searching state`() {
        matchmakingStateFlow.value = MatchmakingState.Searching(testTicket)
        assertTrue(viewModel.isSearchingMatch)
    }

    @Test
    fun `isSearchingMatch returns false when in Idle state`() {
        assertEquals(MatchmakingState.Idle, matchmakingStateFlow.value)
        assertFalse(viewModel.isSearchingMatch)
    }
}
