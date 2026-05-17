package com.agustin.tarati.core.domain

import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.core.domain.game.play.HistoryEntry
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.StableHistoryList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Verifica el ciclo completo de serialización de los tipos de dominio usando
 * kotlinx.serialization (JSON).
 *
 * ## Migración de ParcelRoundTripTest
 * Este test reemplaza al antiguo ParcelRoundTripTest que usaba Parcelable de Android.
 * Con Kotlin Multiplatform, usamos @Serializable (kotlinx.serialization) en lugar de
 * @Parcelize, y verificamos la serialización con JSON en lugar de Parcel.
 *
 * ## Por qué este test es importante
 * - Verifica que todos los tipos de dominio son correctamente serializables
 * - Detecta problemas de tipos anidados que podrían no serializar correctamente
 * - Garantiza que el estado del juego se puede guardar/restaurar en SavedStateHandle
 *
 * ## Diferencia con Parcel
 * JSON es más estricto que Parcel en algunos casos (enums, tipos custom), lo que
 * hace este test más robusto. Si pasa este test, SavedStateHandle funcionará
 * correctamente tanto con backend JSON como con backend Parcel (Android).
 */
class SerializationRoundTripTest {

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        allowStructuredMapKeys = true
    }

    // ── GameStatus ────────────────────────────────────────────────────────────

    @Test
    fun gameStatus_allValues_surviveJsonRoundTrip() {
        GameStatus.entries.forEach { status ->
            val restored = jsonRoundTrip(status, GameStatus.serializer())
            assertEquals(
                status,
                restored,
                "GameStatus.$status no sobrevivió el round-trip de JSON"
            )
        }
    }

    // ── GameState ─────────────────────────────────────────────────────────────

    @Test
    fun gameState_initial_survivesJsonRoundTrip() {
        val original = initialGameState()
        val restored = jsonRoundTrip(original, GameState.serializer())

        assertEquals(original.currentTurn, restored.currentTurn, "GameState.currentTurn no coincide")
        assertEquals(original.cobs, restored.cobs, "GameState.cobs no coincide")
        assertEquals(original.halfMoveClock, restored.halfMoveClock, "GameState.halfMoveClock no coincide")
        assertEquals(
            original.claimedFiftyMoveDraw,
            restored.claimedFiftyMoveDraw,
            "GameState.claimedFiftyMoveDraw no coincide"
        )
    }

    @Test
    fun gameState_withCustomCobs_survivesJsonRoundTrip() {
        val original = GameState(
            cobs = mapOf(
                GameBoard.A1 to Cob(CobColor.WHITE, isUpgraded = false),
                GameBoard.B1 to Cob(CobColor.BLACK, isUpgraded = true),
                GameBoard.C1 to Cob(CobColor.WHITE, isUpgraded = true),
                GameBoard.D1 to Cob(CobColor.BLACK, isUpgraded = false),
            ),
            currentTurn = CobColor.BLACK,
            halfMoveClock = 14,
        )
        val restored = jsonRoundTrip(original, GameState.serializer())

        assertEquals(original.currentTurn, restored.currentTurn)
        assertEquals(original.cobs, restored.cobs)
        assertEquals(original.halfMoveClock, restored.halfMoveClock)
    }

    // ── StableHistoryList ─────────────────────────────────────────────────────

    @Test
    fun stableHistoryList_empty_survivesJsonRoundTrip() {
        val original = StableHistoryList(emptyList())
        val restored = jsonRoundTrip(original, StableHistoryList.serializer())

        assertEquals(0, restored.size, "StableHistoryList vacía no coincide")
    }

    @Test
    fun stableHistoryList_withEntries_survivesJsonRoundTrip() {
        val state = initialGameState()
        val move = Move(GameBoard.A1 to GameBoard.B1)
        val original = StableHistoryList(
            listOf(
                HistoryEntry(move, state),
                HistoryEntry(Move(GameBoard.B1 to GameBoard.C1), state),
            ),
        )

        val restored = jsonRoundTrip(original, StableHistoryList.serializer())

        assertEquals(original.size, restored.size, "Tamaño del historial no coincide")
        assertEquals(original[0].move, restored[0].move, "Primer movimiento no coincide")
        assertEquals(original[1].move, restored[1].move, "Segundo movimiento no coincide")
    }

    // ── Cob ───────────────────────────────────────────────────────────────────

    @Test
    fun cob_allCombinations_surviveJsonRoundTrip() {
        listOf(
            Cob(CobColor.WHITE, isUpgraded = false),
            Cob(CobColor.WHITE, isUpgraded = true),
            Cob(CobColor.BLACK, isUpgraded = false),
            Cob(CobColor.BLACK, isUpgraded = true),
        ).forEach { original ->
            val restored = jsonRoundTrip(original, Cob.serializer())
            assertEquals(original.color, restored.color, "Cob.color no coincide")
            assertEquals(original.isUpgraded, restored.isUpgraded, "Cob.isUpgraded no coincide")
        }
    }

    // ── Move ──────────────────────────────────────────────────────────────────

    @Test
    fun move_surviveJsonRoundTrip() {
        val original = Move(GameBoard.A1 to GameBoard.B1)
        val restored = jsonRoundTrip(original, Move.serializer())

        assertEquals(original.from, restored.from, "Move.from no coincide")
        assertEquals(original.to, restored.to, "Move.to no coincide")
    }

    // ── HistoryEntry ──────────────────────────────────────────────────────────

    @Test
    fun historyEntry_survivesJsonRoundTrip() {
        val move = Move(GameBoard.A1 to GameBoard.B1)
        val state = initialGameState()
        val original = HistoryEntry(move, state)

        val restored = jsonRoundTrip(original, HistoryEntry.serializer())

        assertEquals(original.move, restored.move, "HistoryEntry.move no coincide")
        assertEquals(original.gameState, restored.gameState, "HistoryEntry.gameState no coincide")
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Serializa [value] a JSON y lo deserializa de vuelta.
     * Verifica que el ciclo completo funciona sin pérdida de datos.
     */
    private inline fun <reified T> jsonRoundTrip(
        value: T,
        serializer: KSerializer<T>
    ): T {
        val jsonString = json.encodeToString(serializer, value)
        val restored = json.decodeFromString(serializer, jsonString)

        assertNotNull(restored, "Deserialización retornó null para ${T::class.simpleName}")
        return restored
    }
}