package com.agustin.tarati.core.domain.game.time

import com.agustin.tarati.core.domain.game.pieces.CobColor
import kotlinx.serialization.Serializable

/**
 * Estado runtime del reloj, paralelo al [GameState]
 * lógico. Se mantiene separado por dos motivos: los milisegundos no deben entrar
 * en el `equals` de `GameState` (rompería el guard anti-duplicados y la TT de la IA),
 * y el reloj tiene ciclo de vida propio (pausa, reinicio, reanudación sin cambio de posición).
 *
 * @param mode                      Modo activo para esta partida.
 * @param whiteRemainingMs          Tiempo restante del bando blanco en ms.
 * @param blackRemainingMs          Tiempo restante del bando negro en ms.
 * @param whiteByoyomiPeriodsLeft   Períodos de byoyomi restantes para White.
 * @param blackByoyomiPeriodsLeft   Períodos de byoyomi restantes para Black.
 * @param whiteInByoyomi            `true` si White agotó su base y juega con períodos.
 * @param blackInByoyomi            `true` si Black agotó su base y juega con períodos.
 * @param running                   `true` si el reloj está descontando tiempo de [activeColor].
 * @param activeColor               Bando cuyo tiempo corre, o `null` en pausa / fin.
 * @param lastTickEpochMs           Timestamp del último tick; `0L` = reloj no iniciado.
 * @param activeMoveStartEpochMs    Inicio del medio-movimiento actual (Bronstein); `0L` en pausa.
 */
@Serializable
data class ClockState(
    val mode: TimeControlMode,
    val whiteRemainingMs: Long,
    val blackRemainingMs: Long,
    val whiteByoyomiPeriodsLeft: Int,
    val blackByoyomiPeriodsLeft: Int,
    val whiteInByoyomi: Boolean,
    val blackInByoyomi: Boolean,
    val running: Boolean,
    val activeColor: CobColor?,
    val lastTickEpochMs: Long,
    val activeMoveStartEpochMs: Long,
) {

    companion object {
        /**
         * Estado inicial del reloj para [mode]: ambos bandos con el mismo tiempo base,
         * flags `inByoyomi` en `false`, reloj detenido hasta que arranque la partida.
         */
        fun initial(mode: TimeControlMode): ClockState {
            val base = baseTimeMs(mode)
            val periods = byoyomiPeriods(mode)
            return ClockState(
                mode = mode,
                whiteRemainingMs = base,
                blackRemainingMs = base,
                whiteByoyomiPeriodsLeft = periods,
                blackByoyomiPeriodsLeft = periods,
                whiteInByoyomi = false,
                blackInByoyomi = false,
                running = false,
                activeColor = null,
                lastTickEpochMs = 0L,
                activeMoveStartEpochMs = 0L,
            )
        }

        /** Tiempo base para el reloj inicial según el [mode]. `0` para [TimeControlMode.Unlimited]. */
        private fun baseTimeMs(mode: TimeControlMode): Long = when (mode) {
            is TimeControlMode.Unlimited -> 0L
            is TimeControlMode.SuddenDeath -> mode.totalMs
            is TimeControlMode.Fischer -> mode.baseMs
            is TimeControlMode.Bronstein -> mode.baseMs
            is TimeControlMode.Byoyomi -> mode.baseMs
        }

        /** Períodos de byoyomi iniciales. `0` para todos los modos que no son Byoyomi. */
        private fun byoyomiPeriods(mode: TimeControlMode): Int = when (mode) {
            is TimeControlMode.Byoyomi -> mode.periods
            else -> 0
        }
    }
}