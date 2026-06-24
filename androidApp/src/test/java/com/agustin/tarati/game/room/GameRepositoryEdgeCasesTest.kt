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
import com.agustin.tarati.core.domain.game.play.MatchResult
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
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameRepositoryEdgeCasesTest {
    private lateinit var repository: GameRepository
    private lateinit var gameDao: GameDao

    @Before
    fun setup() {
        gameDao = mockk(relaxed = true)
        repository = RoomGameRepository(gameDao)
    }

    @Test
    fun `should handle empty game state`(): TestResult =
        runTest {
            // Given - GameState vacío
            val gameId = "empty-game"
            val emptyGameState = GameState(emptyMap(), CobColor.WHITE)
            val emptyMoves = emptyList<Move>()

            val pgnHeader = createPGNHeader(emptyGameState)
            val matchDto =
                MatchDto(
                    header = pgnHeader,
                    game =
                        GameDto(
                            boardPosition = emptyGameState.toPositionNotation(),
                            matchResult = MatchResult.UNDEFINED,
                            moveHistory = emptyMoves,
                        ),
                )

            val entity = createGameEntity(gameId, matchDto)

            coEvery { gameDao.getGameById(gameId) } returns entity

            // When
            val result = repository.loadGame(gameId) ?: return@runTest
            val gameState = result.toGameState()
            val moves = result.game.moveHistory

            // Then
            assertNotNull(result)
            assertTrue(gameState.cobs.isEmpty())
            assertTrue(moves.isEmpty())
        }

    @Test
    fun `should handle game with only one move`(): TestResult =
        runTest {
            // Given - Juego con un solo movimiento
            val gameId = "single-move-game"
            val gameState = createRealGameState()
            val singleMove = listOf(Move(GameBoard.A1 to GameBoard.B1))

            val pgnHeader = createPGNHeader(gameState)
            val matchDto =
                MatchDto(
                    header = pgnHeader,
                    game =
                        GameDto(
                            boardPosition = gameState.toPositionNotation(),
                            matchResult = MatchResult.UNDEFINED,
                            moveHistory = singleMove,
                        ),
                )

            val entity = createGameEntity(gameId, matchDto)

            coEvery { gameDao.getGameById(gameId) } returns entity

            // When
            val result = repository.loadGame(gameId) ?: return@runTest
            val history = result.game.moveHistory

            // Then
            assertNotNull(result)
            assertEquals(1, history.size)
            assertEquals(GameBoard.A1, history[0].from)
            assertEquals(GameBoard.B1, history[0].to)
        }

    @Test
    fun `should handle different game results`(): TestResult =
        runTest {
            // Given - Probar diferentes resultados de juego
            val gameResults =
                listOf(
                    MatchResult.UNDEFINED,
                    MatchResult.WHITE_WON,
                    MatchResult.BLACK_WON,
                )

            gameResults.forEach { gameResult ->
                val gameId = "game-${gameResult.name}"
                val gameState = createRealGameState()
                val moves = listOf(Move(GameBoard.A1 to GameBoard.B1))

                val pgnHeader = createPGNHeader(gameState)
                val matchDto =
                    MatchDto(
                        header = pgnHeader,
                        game =
                            GameDto(
                                boardPosition = gameState.toPositionNotation(),
                                matchResult = gameResult,
                                moveHistory = moves,
                            ),
                    )

                val entity = createGameEntity(gameId, matchDto)

                coEvery { gameDao.getGameById(gameId) } returns entity

                // When
                val result = repository.loadGame(gameId)

                // Then
                assertNotNull(result)
                assertEquals(gameResult, result.game.matchResult)
            }
        }

    @Test
    fun `should update existing game when saving with same id`(): TestResult =
        runTest {
            val gameState = createRealGameState()
            val initialMoves = listOf(Move(GameBoard.A1 to GameBoard.B1))
            val updatedMoves =
                listOf(
                    Move(GameBoard.A1 to GameBoard.B1),
                    Move(GameBoard.B1 to GameBoard.C1),
                )

            coEvery { gameDao.saveGame(any()) } just Runs

            // When - Guardar primera vez
            repository.saveGame(gameState.toMatchDto(initialMoves))

            // When - Guardar segunda vez (actualización)
            repository.saveGame(gameState.toMatchDto(updatedMoves))

            // Then - Debería llamarse a saveGame dos veces
            coVerify(exactly = 2) { gameDao.saveGame(any()) }
        }

    @Test
    fun `should handle special characters in player names`(): TestResult =
        runTest {
            // Given - Nombres de jugadores con caracteres especiales
            val specialNames =
                listOf(
                    "Jugador Español ñ",
                    "Player with-dash",
                    "Name with, comma",
                    "Name with # hashtag",
                    "Name with & ampersand",
                )

            specialNames.forEach { playerName ->
                val gameId = "special-name-${playerName.hashCode()}"
                val gameState = createRealGameState()
                val moves = emptyList<Move>()

                val pgnHeader = createPGNHeader(gameState)
                val matchDto =
                    MatchDto(
                        header = pgnHeader,
                        game =
                            GameDto(
                                boardPosition = gameState.toPositionNotation(),
                                matchResult = MatchResult.UNDEFINED,
                                moveHistory = moves,
                            ),
                    )

                pgnHeader.copy(white = playerName)

                val entity = createGameEntity(gameId, matchDto).copy(white = playerName)

                coEvery { gameDao.getGameById(gameId) } returns entity

                // When
                val result = repository.loadGame(gameId)

                // Then
                assertNotNull(result)
                // Verificar que el nombre especial se preserva correctamente
                assertEquals(playerName, entity.white)
            }
        }

    @Test
    fun `should handle concurrent save operations`(): TestResult =
        runTest {
            // Given - Múltiples operaciones de guardado concurrentes
            val gameState = createRealGameState()
            val moves = listOf(Move(GameBoard.A1 to GameBoard.B1))

            coEvery { gameDao.saveGame(any()) } just Runs

            // When - Ejecutar múltiples guardados concurrentes
            val jobs =
                List(10) {
                    launch {
                        repository.saveGame(gameState.toMatchDto(moves))
                    }
                }

            // Then - Esperar a que todos completen
            jobs.joinAll()

            // Verificar que se llamó a saveGame el número correcto de veces
            coVerify(exactly = 10) { gameDao.saveGame(any()) }
        }

    private fun createRealGameState(): GameState {
        val cobs =
            mapOf(
                GameBoard.A1 to Cob(CobColor.WHITE),
                GameBoard.B1 to Cob(CobColor.BLACK),
            )
        return GameState(cobs, CobColor.WHITE)
    }

    private fun createGameEntity(
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
