package com.agustin.tarati.ui.components.game.animation

import com.agustin.tarati.core.domain.game.board.Vertex
import kotlin.random.Random

/**
 * Mapa autoritativo de ángulos de inclinación para piezas poligonales.
 *
 * El tilt es estado puramente visual — no afecta la lógica de juego.
 * Se mantiene separado de [VisualGameState.tiltAngles] porque [syncState]
 * puede ser llamado antes de que [animateMovement] lea [get], y borrar
 * el tilt del origen en ese punto causaría un salto visual.
 *
 * [VisualGameState.tiltAngles] sigue siendo el canal de propagación hacia
 * el renderer; siempre se deriva llamando a [snapshot].
 */
internal class TiltStateMap {

    private val tilts = HashMap<Vertex, Float>()

    /** Devuelve el tilt del [vertex], o `0f` si no existe. */
    fun get(vertex: Vertex): Float = tilts[vertex] ?: 0f

    /**
     * Asigna un tilt aleatorio a los vértices de [vertices] que aún no tienen uno.
     * Los tilts existentes nunca se sobreescriben.
     */
    fun initMissing(vertices: Set<Vertex>) {
        for (vertex in vertices) {
            if (!tilts.containsKey(vertex)) tilts[vertex] = randomTilt()
        }
    }

    /**
     * Elimina los tilts de vértices que ya no están en [vertices].
     * Solo llamar cuando no hay animaciones activas.
     */
    fun retainAll(vertices: Set<Vertex>) {
        tilts.keys.retainAll(vertices)
    }

    /**
     * Finaliza un movimiento: elimina el tilt del origen y registra [toTilt] en el destino.
     */
    fun transfer(from: Vertex, to: Vertex, toTilt: Float) {
        tilts.remove(from)
        tilts[to] = toTilt
    }

    /** Devuelve una copia inmutable del estado actual para [VisualGameState.tiltAngles]. */
    fun snapshot(): Map<Vertex, Float> = HashMap(tilts)

    companion object {
        const val MAX_TILT_DEG = 10f

        fun randomTilt(): Float = (Random.nextFloat() * 2f - 1f) * MAX_TILT_DEG
    }
}