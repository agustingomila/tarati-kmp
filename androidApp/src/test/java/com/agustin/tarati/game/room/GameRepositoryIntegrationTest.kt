package com.agustin.tarati.game.room

import com.agustin.tarati.core.data.database.dao.GameDao
import com.agustin.tarati.core.data.repositories.RoomGameRepository
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.Move.Companion.MOVE_SEPARATOR
import com.agustin.tarati.core.domain.game.play.getValue
import com.agustin.tarati.core.domain.repository.GameRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GameRepositoryIntegrationTest {
    private lateinit var repository: GameRepository
    private lateinit var gameDao: GameDao

    @Before
    fun setup() {
        gameDao = mockk(relaxed = true)
        repository = RoomGameRepository(gameDao)
    }

    @Test
    fun `should maintain data integrity through full lifecycle`() =
        runTest {
            // Given - Datos complejos de prueba
            val complexGameState = createComplexGameState()
            val complexMoves = createComplexMoveHistory()

            val matchDto = complexGameState.toMatchDto(complexMoves)

            // Mockear el guardado
            coEvery { gameDao.saveGame(any()) } just Runs

            // When - Guardar el juego complejo
            repository.saveGame(matchDto)

            // Then - Verificar que se guardó con los datos correctos
            coVerify {
                gameDao.saveGame(
                    match { entity ->
                        entity.boardPosition == matchDto.game.boardPosition &&
                                entity.matchResult == matchDto.game.matchResult.getValue() &&
                                entity.moveHistory ==
                                complexMoves.joinToString(",") {
                                    "${it.from.name}${MOVE_SEPARATOR}${it.to.name}"
                                } &&
                                entity.white == "Human" && // Valores por defecto del repositorio
                                entity.black == "Human"
                    },
                )
            }
        }

    private fun createComplexGameState(): GameState {
        // Crear un estado de juego más complejo y realista
        val cobs =
            mapOf(
                GameBoard.A1 to Cob(CobColor.WHITE, false),
                GameBoard.B1 to Cob(CobColor.WHITE, true),
                GameBoard.B2 to Cob(CobColor.BLACK, false),
                GameBoard.C1 to Cob(CobColor.WHITE, false),
                GameBoard.C5 to Cob(CobColor.BLACK, true),
                GameBoard.D1 to Cob(CobColor.WHITE, true),
                GameBoard.D4 to Cob(CobColor.BLACK, false),
            )
        return GameState(cobs, CobColor.BLACK)
    }

    private fun createComplexMoveHistory(): List<Move> {
        // Crear un historial de movimientos complejo
        return listOf(
            Move(GameBoard.A1 to GameBoard.B1),
            Move(GameBoard.B1 to GameBoard.C1),
            Move(GameBoard.C1 to GameBoard.D1),
            Move(GameBoard.D1 to GameBoard.C5),
            Move(GameBoard.C5 to GameBoard.B2),
            Move(GameBoard.B2 to GameBoard.B1),
        )
    }
}
