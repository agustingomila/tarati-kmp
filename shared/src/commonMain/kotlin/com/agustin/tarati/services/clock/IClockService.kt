package com.agustin.tarati.services.clock


import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.time.ClockState
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Servicio que encapsula el reloj de la partida.
 *
 * Mantiene el [ClockState] actualizado según el [TimeControlMode] activo y
 * emite un evento en [timeoutEvents] cuando un bando agota su tiempo. Los
 * consumidores (GameEvents, pantalla principal) interactúan con este servicio
 * para arrancar / parar el reloj en cada cambio de turno.
 *
 * ## Ciclo de vida por partida
 * ```
 * resetClock(mode)                     // al iniciar partida o cambiar modo
 *   startClockFor(primerJugador)       // al primer movimiento
 *     ... tick loop interno ...
 *   applyIncrementAfterMove(color)     // tras cada medio-movimiento
 *   startClockFor(otroColor)           // cambio de turno
 *     ...
 *   stopClock()                        // al terminar la partida (game over, pausa, edición)
 * ```
 *
 * ## Contrato
 * - Si [TimeControlMode.Unlimited] está activo, el servicio no tickea y
 *   [timeoutEvents] nunca emite.
 * - [startClockFor] es idempotente para el mismo color mientras el reloj esté
 *   corriendo: no reinicia `activeMoveStartEpochMs` si ya está activo.
 * - [applyIncrementAfterMove] se llama DESPUÉS de que un medio-movimiento se
 *   registra en el historial. Acredita Fischer/Bronstein; en Byoyomi, resetea
 *   `remaining` al valor del período solo si el jugador ya estaba en fase byoyomi.
 */
interface IClockService {
    /** Estado actual del reloj. Lo colecciona la UI para renderizar el widget de tiempo. */
    val clockState: StateFlow<ClockState>

    /**
     * Emite el [CobColor] del bando que agotó su tiempo. Se emite una vez por timeout;
     * el reloj queda detenido tras la emisión. `extraBufferCapacity = 1` garantiza que
     * el evento no se pierda durante una rotación de pantalla.
     */
    val timeoutEvents: SharedFlow<CobColor>

    /**
     * Reinicia el reloj para una partida nueva. Cancela el tick loop si estaba
     * corriendo. No arranca el reloj — espera a [startClockFor].
     */
    fun resetClock(mode: TimeControlMode)

    /**
     * Arranca el descuento de tiempo para [color]. Si el reloj ya estaba
     * corriendo para otro color, se transfiere el ownership sin parar el tick
     * loop. En [TimeControlMode.Unlimited] es no-op.
     */
    fun startClockFor(color: CobColor)

    /**
     * Detiene por completo el reloj: cancela el tick loop y pone
     * `running = false`, `activeColor = null`. Usado al terminar partida o al
     * entrar en modo edición.
     */
    fun stopClock()

    /**
     * Pausa el reloj conservando `activeColor`. A diferencia de [stopClock],
     * [resumeClock] puede restaurar la ejecución desde el mismo color. Descuenta
     * el tiempo transcurrido desde el último tick antes de pausar.
     */
    fun pauseClock()

    /**
     * Reanuda el reloj tras una pausa. El medio-movimiento en curso se considera
     * reiniciado para Bronstein: `activeMoveStartEpochMs` se setea a `now`. Esto
     * puede sobreacreditar delay si hubo una pausa prolongada dentro de un mismo
     * medio-movimiento; aceptable dado que las pausas son eventos excepcionales.
     */
    fun resumeClock()

    /**
     * Notifica al servicio que [colorWhoMoved] completó un medio-movimiento.
     * Aplica el incremento correspondiente al modo activo:
     *  - Fischer: suma `incrementMs`.
     *  - Bronstein: suma `min(elapsed, delayMs)`.
     *  - Byoyomi (solo si el jugador está en fase byoyomi): resetea a `periodMs`.
     *  - SuddenDeath / Unlimited: no-op.
     *
     * NO arranca el reloj del oponente — eso es responsabilidad del caller
     * (llamará [startClockFor] con el color opuesto después de este método).
     */
    fun applyIncrementAfterMove(colorWhoMoved: CobColor)

    /**
     * Sincroniza los tiempos con los valores autoritativos del servidor (online play).
     *
     * Si el reloj está en [TimeControlMode.Unlimited] y se provee [fallbackMode],
     * primero reinicia el reloj con ese modo — necesario cuando el usuario no tiene
     * configurado un control de tiempo local pero juega una partida online con tiempo.
     *
     * Luego escribe los milisegundos exactos del servidor y garantiza que el tick
     * loop esté corriendo para [activeTurn]. Si sigue en Unlimited tras el intento
     * de fallback, es no-op.
     *
     * @param whiteMs      Tiempo restante de blancas en ms según el servidor.
     * @param blackMs      Tiempo restante de negras en ms según el servidor.
     * @param activeTurn   Color del jugador con el turno tras el update.
     * @param fallbackMode Modo con el que reiniciar el reloj si está en Unlimited.
     */
    fun syncFromServer(
        whiteMs: Long,
        blackMs: Long,
        activeTurn: CobColor,
        fallbackMode: TimeControlMode? = null,
    )
}