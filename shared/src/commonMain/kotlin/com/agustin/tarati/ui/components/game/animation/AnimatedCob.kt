package com.agustin.tarati.ui.components.game.animation

import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.ui.components.game.draw.pieces.ConversionAnimationType

data class AnimatedCob(
    val vertex: Vertex,
    val cob: Cob,
    val currentPos: Vertex,
    val targetPos: Vertex,
    val targetColor: CobColor? = null,
    val animationProgress: Float = 1f,
    val upgradeProgress: Float = 1f,
    val conversionProgress: Float = 1f,
    val isConverting: Boolean = false,
    val conversionType: ConversionAnimationType = ConversionAnimationType.FROM_CENTER,
    /** Ángulo de tilt (grados) al inicio del movimiento — tilt del vértice origen. */
    val fromTiltDeg: Float = 0f,
    /** Ángulo de tilt (grados) al finalizar el movimiento — nuevo tilt asignado al destino. */
    val toTiltDeg: Float = 0f,
)