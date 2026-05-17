package com.agustin.tarati.ui.components.game.animation

import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor

/**
 * Estado visual del tablero: piezas, turno actual y ángulos de inclinación orgánicos.
 *
 * [tiltAngles] almacena el ángulo de rotación en el plano (en grados) de cada pieza
 * poligonal. El sistema circular (sides ≤ 1) no usa tilt — las piezas circulares son
 * invariantes a rotación. Los ángulos se generan aleatoriamente al cargar una partida y
 * se actualizan en cada movimiento, dando a cada pieza una posición levemente irregular
 * que aporta organicidad visual al tablero.
 *
 * Los tilts son el estado **puramente visual**: no afectan la lógica de juego, no se
 * persisten y se recrean en cada sesión.
 */
data class VisualGameState(
    val cobs: Map<Vertex, Cob> = emptyMap(),
    val currentTurn: CobColor? = null,
    /** Ángulo de inclinación en grados (±[BoardAnimationViewModel.Companion.PIECE_MAX_TILT_DEG]) por vértice ocupado. */
    val tiltAngles: Map<Vertex, Float> = emptyMap(),
)