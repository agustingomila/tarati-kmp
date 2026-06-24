package com.agustin.tarati.features.game.previews

import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.bridgeVertices
import com.agustin.tarati.core.domain.game.board.GameBoard.circumferenceVertices
import com.agustin.tarati.core.domain.game.board.GameBoard.domesticVertices
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState
import kotlin.random.Random

// ── Posición de preview aleatoria ─────────────────────────────────────────────
//
// Tablero Tarati:
//
//             D3    D4
//
//             C7    C8
//        C6              C9
//                B4
//     C5    B3        B5    C10
//                A1
//     C4    B2        B6    C11
//                B1
//        C3              C12
//             C2    C1
//
//             D2    D1
//
// Board topology reference:
//   WHITE half : D1, D2, C1, C2, C3, C12, B1
//   BLACK half : D3, D4, C6, C7, C8, C9, B4
//   Neutral    : A1, B2, B3, B5, B6, C4, C5, C10, C11

// `get()` en lugar de inicialización directa: las listas de vértices se evalúan
// en cada acceso (dentro de la llamada a la función), no en carga de clase.
// Esto evita problemas de orden de inicialización con GameBoard.

/** Vertices on WHITE's side — reachable in ≤2 moves from D1 / D2. */
private val whiteHalf
    get() = listOf(
        domesticVertices[0], domesticVertices[1],            // D1, D2
        circumferenceVertices[0], circumferenceVertices[1],  // C1, C2
        circumferenceVertices[2],                            // C3
        circumferenceVertices[11],                           // C12
        bridgeVertices[0],                                   // B1
    )

/** Vertices on BLACK's side — reachable in ≤2 moves from D3 / D4. */
private val blackHalf
    get() = listOf(
        domesticVertices[2], domesticVertices[3],            // D3, D4
        circumferenceVertices[5],                            // C6
        circumferenceVertices[6], circumferenceVertices[7],  // C7, C8
        circumferenceVertices[8],                            // C9
        bridgeVertices[3],                                   // B4
    )

/** Neutral vertices — more than 2 moves from either home base. */
private val neutral
    get() = listOf(
        A1,
        bridgeVertices[1], bridgeVertices[2],                // B2, B3
        bridgeVertices[4], bridgeVertices[5],                // B5, B6
        circumferenceVertices[3], circumferenceVertices[4],  // C4, C5
        circumferenceVertices[9], circumferenceVertices[10], // C10, C11
    )

val previewRandomMidGameState: GameState get() = previewRandomMidGameState()
val previewRandomFinalGameState: GameState get() = previewRandomFinalGameState()

/**
 * Posición aleatoria de media partida: 8 piezas (4 blancas + 4 negras),
 * distribuidas mayoritariamente en los semitableros propios con 1 pieza por
 * lado en territory neutral. 0–1 upgrades por lado.
 *
 * Definida como **función** (no `val`) para que las listas de vértices se
 * evalúen dentro de la llamada, cuando `GameBoard` ya está inicializado.
 * El fix real que garantiza estabilidad visual es `remember(gameState)` en
 * [previews.GameScreenPreviewContent].
 */
private fun previewRandomMidGameState(): GameState {
    val whiteVertices = whiteHalf.shuffled().take(3)
    val blackVertices = blackHalf.shuffled().take(3)
    val neutralVertices = neutral.shuffled().take(2)
    val whiteUpgradeCount = Random.nextInt(0, 2)
    val blackUpgradeCount = Random.nextInt(0, 2)
    val cobs = buildMap {
        whiteVertices.forEachIndexed { i, v -> put(v, Cob(WHITE, i < whiteUpgradeCount)) }
        blackVertices.forEachIndexed { i, v -> put(v, Cob(BLACK, i < blackUpgradeCount)) }
        put(neutralVertices[0], Cob(WHITE))
        put(neutralVertices[1], Cob(BLACK))
    }
    return GameState(cobs = cobs, currentTurn = if (Random.nextBoolean()) WHITE else BLACK)
}

/**
 * Posición aleatoria de final de partida: 8 piezas muy dispersas, mayoría
 * upgrades, simulando una partida avanzada.
 *
 * Definida como **función** por la misma razón que [previewRandomMidGameState].
 */
private fun previewRandomFinalGameState(): GameState {
    val allZones = (whiteHalf + blackHalf + neutral).shuffled()
    val whiteVertices = allZones.take(4)
    val blackVertices = allZones.drop(4).take(4)
    val whiteUpgradeCount = Random.nextInt(3, 5)
    val blackUpgradeCount = Random.nextInt(3, 5)
    val cobs = buildMap {
        whiteVertices.forEachIndexed { i, v -> put(v, Cob(WHITE, i < whiteUpgradeCount)) }
        blackVertices.forEachIndexed { i, v -> put(v, Cob(BLACK, i < blackUpgradeCount)) }
    }
    return GameState(cobs = cobs, currentTurn = if (Random.nextBoolean()) WHITE else BLACK)
}