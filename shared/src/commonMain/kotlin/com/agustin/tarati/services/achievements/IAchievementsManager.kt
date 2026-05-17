package com.agustin.tarati.services.achievements

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move

/**
 * Gestor de logros del juego.
 *
 * La implementación Android integra con Google Play Games Services para
 * desbloquear logros basados en las acciones del jugador.
 *
 * La implementación Desktop es no-op porque no hay sistema de logros nativo.
 */
interface IAchievementsManager {

    /**
     * Notifica que se aplicó un movimiento en el tablero.
     *
     * @param move El movimiento realizado
     * @param previousState Estado del tablero antes del movimiento
     * @param newState Estado del tablero después del movimiento
     */
    suspend fun onMoveApplied(move: Move, previousState: GameState, newState: GameState)

    /**
     * Notifica que terminó una partida.
     *
     * @param matchState Estado final de la partida (con ganador, movimientos, etc)
     * @param playerSide Bando del jugador humano (WHITE o BLACK)
     * @param difficulty Dificultad de la IA contra la que jugó
     */
    suspend fun onGameOver(matchState: MatchState, playerSide: CobColor, difficulty: Difficulty)

    /**
     * Notifica que el jugador completó el tutorial.
     */
    suspend fun onTutorialCompleted()

    /**
     * Muestra la UI de logros de la plataforma.
     *
     * En Android: Abre la pantalla de Google Play Games.
     * En Desktop: No hace nada (no hay sistema de logros).
     */
    fun showAchievementsUI()
}