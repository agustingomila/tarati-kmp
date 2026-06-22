package com.agustin.tarati.ui.components.game.highlights.sequences

import com.agustin.tarati.core.domain.game.board.Edge
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.B5
import com.agustin.tarati.core.domain.game.board.GameBoard.B6
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D2
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.board.GameBoard.circumferenceVertices
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import com.agustin.tarati.ui.components.game.highlights.createElectricEdgeAnimation
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.CENTRAL_RAY_SEQUENCE
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.CIRCUMFERENCE_PAIR_SEQUENCE
import com.agustin.tarati.ui.components.game.highlights.sequences.HighlightDurations.DOMESTIC_SEQUENCE

/**
 * Secuencia de animación para tablas (regla de 50 movimientos u otros tipos de empate).
 *
 * Tres fases:
 * 1. **Circunferencia horaria**: un rayo eléctrico recorre C1 → C2 → … → C12 → C1,
 *    un edge por frame, para dar efecto de "víbora eléctrica".
 * 2. **Bridge anti-horario**: el rayo recorre B1 → B6 → B5 → B4 → B3 → B2 → B1,
 *    en dirección inversa a la numeración natural.
 * 3. **Domestic anti-horario**: ambos triángulos domésticos (blanco y negro)
 *    se iluminan en paralelo, entrando desde la circunferencia y saliendo hacia ella
 *    en sentido anti-horario: C2→D2→D1→C1 (blanco) y C8→D4→D3→C7 (negro).
 *
 * No requiere ganador — válido para cualquier resultado con `winner == null`.
 *
 * @see GameOverSequenceProvider
 */
fun createDrawSequence(matchState: MatchState): List<List<HighlightAnimation>> {
    val sequences = mutableListOf<List<HighlightAnimation>>()
    sequences.addAll(createCircumferenceClockwiseSequence())
    sequences.addAll(createBridgeAntiClockwiseSequence())
    sequences.addAll(createDomesticAntiClockwiseSequence())
    return sequences
}

// ── 1. Circunferencia horaria ─────────────────────────────────────────────────
//
// C1 → C2 → C3 → … → C12 → C1  (12 steps × CIRCUMFERENCE_PAIR_SEQUENCE = ~720 ms)
// Cada frame = un solo edge, efecto de rayo que avanza vértice a vértice.

private fun createCircumferenceClockwiseSequence(): List<List<HighlightAnimation>> =
    (0 until 12).map { i ->
        val from = circumferenceVertices[i]
        val to = circumferenceVertices[(i + 1) % 12]
        listOf(
            createElectricEdgeAnimation(
                edge = Edge(from to to),
                duration = CIRCUMFERENCE_PAIR_SEQUENCE,
                pulse = true,
            ),
        )
    }

// ── 2. Bridge anti-horario ────────────────────────────────────────────────────
//
// Orden natural (horario): B1 → B2 → B3 → B4 → B5 → B6 → B1
// Anti-horario           : B1 → B6 → B5 → B4 → B3 → B2 → B1
// 6 steps × CENTRAL_RAY_SEQUENCE = ~450 ms

private val bridgeAntiClockwisePath = listOf(B1, B6, B5, B4, B3, B2, B1)

private fun createBridgeAntiClockwiseSequence(): List<List<HighlightAnimation>> =
    (0 until bridgeAntiClockwisePath.size - 1).map { i ->
        listOf(
            createElectricEdgeAnimation(
                edge = Edge(bridgeAntiClockwisePath[i] to bridgeAntiClockwisePath[i + 1]),
                duration = CENTRAL_RAY_SEQUENCE,
                pulse = true,
            ),
        )
    }

// ── 3. Domestic anti-horario ──────────────────────────────────────────────────
//
// Los dos triángulos domésticos se iluminan en paralelo (blanco y negro),
// recorriendo cada uno en sentido anti-horario respecto a la externalBoundary.
//
// Blanco (anti-horario): C2 → D2 → D1 → C1
// Negro  (anti-horario): C8 → D4 → D3 → C7
//
// 3 steps × DOMESTIC_SEQUENCE = ~450 ms

private val whiteDomesticPath = listOf(C2, D2, D1, C1)
private val blackDomesticPath = listOf(C8, D4, D3, C7)

private fun createDomesticAntiClockwiseSequence(): List<List<HighlightAnimation>> =
    (0 until whiteDomesticPath.size - 1).map { i ->
        listOf(
            createElectricEdgeAnimation(
                edge = Edge(whiteDomesticPath[i] to whiteDomesticPath[i + 1]),
                duration = DOMESTIC_SEQUENCE,
                pulse = true,
            ),
            createElectricEdgeAnimation(
                edge = Edge(blackDomesticPath[i] to blackDomesticPath[i + 1]),
                duration = DOMESTIC_SEQUENCE,
                pulse = true,
            ),
        )
    }