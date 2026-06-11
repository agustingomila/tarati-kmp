package com.agustin.tarati.services.achievements

import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchState
import com.agustin.tarati.core.domain.game.play.Move
import kotlinx.coroutines.flow.StateFlow

/**
 * Gestor de logros del juego.
 *
 * La implementación Android integra con Google Play Games Services y el servidor.
 * Desktop y Web sincronizan únicamente con el servidor de Tarati cuando el usuario
 * tiene sesión activa.
 *
 * ## Estado observable de paletas desbloqueadas
 * [unlockedPaletteAchievements] expone qué logros de paleta están desbloqueados en
 * la sesión actual. Los SettingsViewModels de Desktop y Web lo usan para filtrar
 * [allPalettesForSelector]. En Android, [AndroidSettingsViewModel] lee directamente
 * de [AchievementsRepository] (DataStore).
 *
 * ## Sincronización inicial
 * [syncFromServer] carga el estado del servidor al iniciar la sesión. Es no-op en
 * plataformas que no usan el servidor (implementación por defecto).
 */
interface IAchievementsManager {

    /**
     * Conjunto de logros de paleta actualmente desbloqueados.
     * Incluye únicamente los cuatro logros que desbloquean paletas permanentes:
     * HALLOWEEN_THEME, CHRISTMAS_THEME, THE_FIRST_LIGHT y THE_DARK_SIDE.
     *
     * Se popula al iniciar la sesión (vía [syncFromServer]) y se actualiza
     * en tiempo real cuando un logro se desbloquea durante la sesión.
     */
    val unlockedPaletteAchievements: StateFlow<Set<AchievementId>>

    /**
     * Carga el estado de logros desde el servidor y actualiza
     * [unlockedPaletteAchievements] y los contadores incrementales.
     *
     * No-op por defecto; sobreescrito por [ServerAchievementsManager].
     * Debe llamarse una vez por sesión desde el ViewModel o composable raíz.
     */
    suspend fun syncFromServer() {}

    /**
     * Notifica que se aplicó un movimiento en el tablero.
     *
     * @param move          El movimiento realizado
     * @param previousState Estado antes del movimiento
     * @param newState      Estado después del movimiento
     */
    suspend fun onMoveApplied(move: Move, previousState: GameState, newState: GameState)

    /**
     * Notifica que terminó una partida.
     *
     * @param matchState  Estado final (ganador, razón de fin)
     * @param playerSide  Bando del jugador humano
     * @param difficulty  Dificultad de la IA contra la que jugó
     */
    suspend fun onGameOver(matchState: MatchState, playerSide: CobColor, difficulty: Difficulty)

    /** Notifica que el jugador completó el tutorial. */
    suspend fun onTutorialCompleted()

    /**
     * Muestra la UI de logros de la plataforma.
     *
     * En Android: Abre la pantalla de Google Play Games.
     * En Desktop/Web: No-op por defecto.
     */
    fun showAchievementsUI()
}
