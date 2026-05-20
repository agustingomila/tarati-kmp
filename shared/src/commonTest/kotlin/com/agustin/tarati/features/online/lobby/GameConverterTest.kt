package com.agustin.tarati.features.online.lobby

import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.play.GameEndReason
import com.agustin.tarati.core.domain.game.play.GameResult
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.getValue
import com.agustin.tarati.core.domain.game.time.TimeControl
import com.agustin.tarati.network.models.Game
import com.agustin.tarati.network.models.GamePlayerInfo
import com.agustin.tarati.network.models.GameTimeControl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests para [Game.toMatchDto] y la lógica interna de extracción de movimientos desde PGN.
 *
 * [extractMovesFromPgn] es privada — se testea indirectamente vía [toMatchDto].
 *
 * Vértices usados:
 *  - A1: centro del tablero
 *  - B2: bridge 2
 *  - C3: circumference 3
 *  - C12: circumference 12
 *  - D4: domestic base 4
 */
class GameConverterTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    private val now = kotlin.time.Instant.fromEpochMilliseconds(1_700_000_000_000L)
    private val earlier = kotlin.time.Instant.fromEpochMilliseconds(1_699_999_000_000L)

    private fun makeGame(
        pgn: String = "",
        result: GameResult = GameResult.WHITE_WIN,
        whiteName: String = "Alice",
        blackName: String = "Bob",
        endMethod: GameEndReason = GameEndReason.MIT,
        timeControl: GameTimeControl = GameTimeControl(TimeControl.BLITZ, 180, 2),
        id: String = "game-123",
    ): Game = Game(
        id = id,
        whitePlayer = GamePlayerInfo(
            userId = "w-id",
            username = whiteName,
            ratingBefore = 1200,
            ratingAfter = 1215,
            ratingChange = 15,
            timeUsed = 60_000L,
        ),
        blackPlayer = GamePlayerInfo(
            userId = "b-id",
            username = blackName,
            ratingBefore = 1200,
            ratingAfter = 1185,
            ratingChange = -15,
            timeUsed = 80_000L,
        ),
        timeControl = timeControl,
        result = result,
        endMethod = endMethod,
        pgn = pgn,
        moves = 0,
        startedAt = earlier,
        endedAt = now,
        isRated = true,
    )

    /** PGN con 3 movimientos normales y token de resultado. */
    private val pgn3Moves = """
        [Event "Tarati Online"]
        [White "Alice"]
        [Black "Bob"]
        [Result "1-0"]

        1. C3→B2 D4→C3 2. B2→A1 1-0
    """.trimIndent()

    /** PGN con 2 movimientos normales y 1 promoción. */
    private val pgnWithPromotion = """
        [Event "Tarati Online"]

        1. C3→B2 C12=R 1-0
    """.trimIndent()

    /** PGN sin sección de movimientos (solo cabeceras). */
    private val pgnOnlyHeaders = """
        [Event "Tarati Online"]
        [White "Alice"]
        [Black "Bob"]
    """.trimIndent()

    // ── toMatchDto: mapeo de resultado ────────────────────────────────────────

    @Test
    fun `WHITE_WIN maps to WHITE_WON and result string 1-0`() {
        val dto = makeGame(result = GameResult.WHITE_WIN).toMatchDto()
        assertEquals(MatchResult.WHITE_WON, dto.game.matchResult)
        assertEquals(MatchResult.WHITE_WON.getValue(), dto.header.result)
    }

    @Test
    fun `BLACK_WIN maps to BLACK_WON and result string 0-1`() {
        val dto = makeGame(result = GameResult.BLACK_WIN).toMatchDto()
        assertEquals(MatchResult.BLACK_WON, dto.game.matchResult)
        assertEquals(MatchResult.BLACK_WON.getValue(), dto.header.result)
    }

    @Test
    fun `DRAW maps to UNDEFINED and result string asterisk`() {
        val dto = makeGame(result = GameResult.DRAW).toMatchDto()
        assertEquals(MatchResult.UNDEFINED, dto.game.matchResult)
        assertEquals(MatchResult.UNDEFINED.getValue(), dto.header.result)
    }

    // ── toMatchDto: cabeceras y metadatos ─────────────────────────────────────

    @Test
    fun `id is propagated to MatchDto`() {
        val dto = makeGame(id = "game-abc-123").toMatchDto()
        assertEquals("game-abc-123", dto.id)
    }

    @Test
    fun `white and black usernames appear in PGN header`() {
        val dto = makeGame(whiteName = "Magnus", blackName = "Kasparov").toMatchDto()
        assertEquals("Magnus", dto.header.white)
        assertEquals("Kasparov", dto.header.black)
    }

    @Test
    fun `createdAt equals endedAt epoch millis`() {
        val dto = makeGame().toMatchDto()
        assertEquals(now.toEpochMilliseconds(), dto.createdAt)
    }

    @Test
    fun `timeControl display string is in header`() {
        val tc = GameTimeControl(TimeControl.RAPID, 600, 5)
        val dto = makeGame(timeControl = tc).toMatchDto()
        assertEquals(tc.toDisplayString(), dto.header.timeControl)
    }

    @Test
    fun `endMethod key is in header termination`() {
        val dto = makeGame(endMethod = GameEndReason.MIT).toMatchDto()
        assertEquals(GameEndReason.MIT.key, dto.header.termination)
    }

    @Test
    fun `date field is non-blank`() {
        val dto = makeGame().toMatchDto()
        assertTrue(dto.header.date.isNotBlank())
    }

    // ── toMatchDto: extracción de movimientos desde PGN ───────────────────────

    @Test
    fun `empty pgn produces empty move history`() {
        val dto = makeGame(pgn = "").toMatchDto()
        assertTrue(dto.game.moveHistory.isEmpty())
    }

    @Test
    fun `blank pgn produces empty move history`() {
        val dto = makeGame(pgn = "   \n  ").toMatchDto()
        assertTrue(dto.game.moveHistory.isEmpty())
    }

    @Test
    fun `pgn with only headers produces empty move history`() {
        val dto = makeGame(pgn = pgnOnlyHeaders).toMatchDto()
        assertTrue(dto.game.moveHistory.isEmpty())
    }

    @Test
    fun `pgn with 3 normal moves extracts exactly 3 moves`() {
        val dto = makeGame(pgn = pgn3Moves).toMatchDto()
        assertEquals(3, dto.game.moveHistory.size)
    }

    @Test
    fun `first move in pgn is C3 to B2`() {
        val dto = makeGame(pgn = pgn3Moves).toMatchDto()
        val first = dto.game.moveHistory.first()
        assertEquals(GameBoard.C3, first.from)
        assertEquals(GameBoard.B2, first.to)
    }

    @Test
    fun `second move in pgn is D4 to C3`() {
        val dto = makeGame(pgn = pgn3Moves).toMatchDto()
        val second = dto.game.moveHistory[1]
        assertEquals(GameBoard.D4, second.from)
        assertEquals(GameBoard.C3, second.to)
    }

    @Test
    fun `result token 1-0 is not included as move`() {
        val dto = makeGame(pgn = pgn3Moves).toMatchDto()
        assertTrue(dto.game.moveHistory.none { it.name == "1-0" })
    }

    @Test
    fun `move numbers like 1 and 2 are not included as moves`() {
        val dto = makeGame(pgn = pgn3Moves).toMatchDto()
        assertEquals(3, dto.game.moveHistory.size)
    }

    @Test
    fun `pgn with promotion produces 2 moves and promotion is marked`() {
        val dto = makeGame(pgn = pgnWithPromotion).toMatchDto()
        assertEquals(2, dto.game.moveHistory.size)
        val promotion = dto.game.moveHistory[1]
        assertTrue(promotion.isPromotion(), "Second move should be a promotion (from == to)")
        assertEquals(GameBoard.C12, promotion.from)
    }

    @Test
    fun `malformed pgn produces empty move history without throwing`() {
        val dto = makeGame(pgn = "not a valid pgn at all!!!").toMatchDto()
        assertTrue(dto.game.moveHistory.isEmpty())
    }

    @Test
    fun `pgn with only result token has no moves`() {
        val dto = makeGame(pgn = "1-0").toMatchDto()
        assertTrue(dto.game.moveHistory.isEmpty())
    }

    // ── toMatchDto: posición del tablero ──────────────────────────────────────

    @Test
    fun `initialBoardPosition and boardPosition are non-blank`() {
        val dto = makeGame().toMatchDto()
        assertTrue(dto.game.initialBoardPosition.isNotBlank())
        assertTrue(dto.game.boardPosition.isNotBlank())
    }

    @Test
    fun `initialBoardPosition equals boardPosition - no final position extracted from pgn`() {
        val dto = makeGame(pgn = pgn3Moves).toMatchDto()
        assertEquals(dto.game.initialBoardPosition, dto.game.boardPosition)
    }
}
