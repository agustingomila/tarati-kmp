package com.agustin.tarati.game.room

import com.agustin.tarati.core.data.database.dao.GameDao
import com.agustin.tarati.core.data.database.dto.GameDto
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.PGNHeader.Companion.createPGNHeader
import com.agustin.tarati.core.data.database.entities.GameEntity
import com.agustin.tarati.core.data.repositories.RoomGameRepository
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.MatchResult.UNDEFINED
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.Move.Companion.MOVE_SEPARATOR
import com.agustin.tarati.core.domain.game.play.getValue
import com.agustin.tarati.core.domain.repository.GameRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GameRepositoryTest {
    private lateinit var repository: GameRepository
    private lateinit var gameDao: GameDao

    @Before
    fun setup() {
        gameDao = mockk(relaxed = true)
        repository = RoomGameRepository(gameDao)
    }

    @Test
    fun `should save game successfully`(): TestResult =
        runTest {
            // Given
            val gameState = mockk<GameState>(relaxed = true)

            // When
            repository.saveGame(gameState.toMatchDto())

            // Then - No exception should be thrown
            // Verificamos que se llamó a saveGame en el DAO
            coVerify { gameDao.saveGame(any()) }
        }

    @Test
    fun `should delete game by id`(): TestResult =
        runTest {
            // Given
            val gameId = "test-id"

            // When
            repository.deleteGame(gameId)

            // Then
            coVerify { gameDao.deleteGameById(gameId) }
        }

    @Test
    fun `should return null when loading non-existent game`(): TestResult =
        runTest {
            // Given
            val gameId = "non-existent-id"
            coEvery { gameDao.getGameById(gameId) } returns null

            // When
            val result = repository.loadGame(gameId)

            // Then
            assertNull(result)
        }

    @Test
    fun `should load game successfully`(): TestResult =
        runTest {
            // Given
            val gameId = "should_load_game_successfully"
            val entity = createSimpleGameEntity(gameId)

            coEvery { gameDao.getGameById(gameId) } returns entity

            // When
            val result = repository.loadGame(gameId)

            // Then
            assertNotNull(result)
        }

    @Test
    fun `should load game successfully with match dto`(): TestResult =
        runTest {
            // Given
            val gameId = "test-id"

            // Crear un GameState real
            val expectedGameState = createRealGameState()
            val expectedMoves =
                listOf(
                    Move(GameBoard.A1 to GameBoard.B1),
                    Move(GameBoard.B1 to GameBoard.C1),
                )

            // Crear el MatchDto que se almacenará en la base de datos
            val pgnHeader = createPGNHeader(expectedGameState)
            val matchDto =
                MatchDto(
                    header = pgnHeader,
                    game =
                        GameDto(
                            boardPosition = expectedGameState.toPositionNotation(),
                            matchResult = UNDEFINED,
                            moveHistory = expectedMoves,
                        ),
                )

            val entity = createGameEntityFromMatchDto(gameId, matchDto)

            coEvery { gameDao.getGameById(gameId) } returns entity

            // When
            val result = repository.loadGame(gameId)

            // Then
            assertNotNull(result)
            assertEquals(expectedGameState.currentTurn, result.toGameState().currentTurn)
            assertEquals(expectedMoves.size, result.game.moveHistory.size)
        }

    @Test
    fun `should save game with match dto`(): TestResult =
        runTest {
            // Given
            val gameState = createRealGameState()
            val moves =
                listOf(
                    Move(GameBoard.A1 to GameBoard.B1),
                    Move(GameBoard.B1 to GameBoard.C1),
                )
            val matchDto = gameState.toMatchDto(moves)

            coEvery { gameDao.saveGame(any()) } just Runs

            // When
            repository.saveGame(matchDto)

            // Then
            coVerify {
                gameDao.saveGame(
                    match { entity ->
                        // Verificar que se guarda con la estructura correcta
                        entity.boardPosition == matchDto.game.boardPosition &&
                                entity.matchResult == matchDto.game.matchResult.getValue() &&
                                entity.moveHistory ==
                                moves.joinToString(",") {
                                    "${it.from.name}${MOVE_SEPARATOR}${it.to.name}"
                                }
                    },
                )
            }
        }

    @Test
    fun `should handle parsing errors in match dto gracefully`(): TestResult =
        runTest {
            // Given
            val gameId = "should_handle_parsing_errors_gracefully"
            // Crear entidad con datos inválidos en moveHistory
            val entity =
                createSimpleGameEntity(gameId).copy(
                    moveHistory = "invalid-move-format",
                )

            coEvery { gameDao.getGameById(gameId) } returns entity

            // When
            val result = repository.loadGame(gameId)

            // Then
            assertNull(result)
        }

    @Test
    fun `should convert game state to and from position notation`(): TestResult =
        runTest {
            // Given - Crear un GameState con datos reales
            val originalGameState = createRealGameState()
            val moves = listOf(Move(GameBoard.A1 to GameBoard.B1))

            // When - Convertir a MatchDto y luego de vuelta a GameState
            val matchDto = originalGameState.toMatchDto(moves)
            val restoredGameState = matchDto.toGameState()

            // Then - Verificar que la conversión es correcta
            assertEquals(originalGameState.currentTurn, restoredGameState.currentTurn)
        }

    @Test
    fun `should complete save and load cycle`(): TestResult =
        runTest {
            // Given
            val gameId = "cycle-test"
            val originalGameState = createRealGameState()
            val originalMoves =
                listOf(
                    Move(GameBoard.A1 to GameBoard.B1),
                    Move(GameBoard.B1 to GameBoard.C1),
                )

            val matchDto = originalGameState.toMatchDto(originalMoves)

            // Mockear el guardado
            coEvery { gameDao.saveGame(any()) } just Runs

            // Mockear la carga
            val entity = createGameEntityFromMatchDto(gameId, matchDto)
            coEvery { gameDao.getGameById(gameId) } returns entity

            // When - Guardar
            repository.saveGame(matchDto)

            // When - Cargar
            val loadedResult = repository.loadGame(gameId)

            // Then
            assertNotNull(loadedResult)
            val loadedGameState = loadedResult.toGameState()
            val loadedMoves = loadedResult.game.moveHistory

            assertEquals(originalGameState.currentTurn, loadedGameState.currentTurn)
            assertEquals(originalMoves.size, loadedMoves.size)
        }

    private fun createRealGameState(): GameState {
        // Crear un estado de juego real con datos concretos
        val cobs =
            mapOf(
                GameBoard.A1 to Cob(CobColor.WHITE),
                GameBoard.B1 to Cob(CobColor.BLACK),
                GameBoard.C1 to Cob(CobColor.WHITE, true),
            )

        return GameState(
            currentTurn = CobColor.WHITE,
            cobs = cobs,
        )
    }

    private fun createSimpleGameEntity(id: String): GameEntity =
        createGameEntityFromMatchDto(id, initialGameState().toMatchDto())

    private fun createGameEntityFromMatchDto(
        id: String,
        matchDto: MatchDto,
    ): GameEntity =
        GameEntity(
            id = id,
            event = matchDto.header.event,
            site = matchDto.header.site,
            date = matchDto.header.date,
            round = matchDto.header.round,
            white = matchDto.header.white,
            black = matchDto.header.black,
            result = matchDto.header.result,
            gameType = matchDto.header.gameType,
            rules = matchDto.header.rules,
            timeControl = matchDto.header.timeControl,
            termination = matchDto.header.termination,
            observations = matchDto.header.observations,
            initialBoardPosition = matchDto.game.initialBoardPosition,
            boardPosition = matchDto.game.boardPosition,
            matchResult = matchDto.game.matchResult.getValue(),
            moveHistory =
                matchDto.game.moveHistory.joinToString(",") {
                    "${it.from.name}${MOVE_SEPARATOR}${it.to.name}"
                },
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
}
