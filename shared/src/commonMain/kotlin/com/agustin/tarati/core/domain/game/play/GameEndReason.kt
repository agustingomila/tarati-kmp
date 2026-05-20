package com.agustin.tarati.core.domain.game.play

import kotlinx.serialization.Serializable

/**
 * GameEndReason - Cómo terminó la partida
 */
@Serializable
enum class GameEndReason {
    MIT,
    STALEMIT,
    TRIPLE,
    FIFTY_MOVES,
    TIMEOUT,
    UNDETERMINED,
    PLAYING,
    DRAW_AGREEMENT,
    RESIGNATION;

    /**
     * Convierte a string para BD
     */
    val key: String
        get() = name.lowercase()

    companion object {
        /**
         * Convierte desde string de BD
         */
        fun fromKey(value: String): GameEndReason =
            valueOf(value.uppercase())
    }
}