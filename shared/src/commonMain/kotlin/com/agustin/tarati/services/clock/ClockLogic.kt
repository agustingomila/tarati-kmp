package com.agustin.tarati.services.clock

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.time.ClockState
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.services.clock.ClockLogic.tick

/**
 * Lógica pura del reloj, sin dependencias de `ViewModel`, `CoroutineScope` ni
 * `Clock` del sistema. Todas las operaciones aceptan el timestamp `now` como
 * parámetro, lo que permite tests deterministas sin `advanceTimeBy` ni
 * `StandardTestDispatcher`.
 *
 * El `ClockViewModel` es un thin wrapper sobre este object: maneja el tick loop
 * con [kotlinx.coroutines.delay] y delega todas las decisiones aquí.
 *
 * ## Transiciones importantes
 * - **Fase base → byoyomi (exceso inicial):** al llegar `remaining` a 0 en fase
 *   base, el estado transita a byoyomi sin consumir periodo (`inByoyomi = true`,
 *   `periods` intacto, `remaining = periodMs`). El primer turno de byoyomi es
 *   "gratis" en el sentido de que no cuesta un período — solo los EXCESOS
 *   posteriores lo hacen.
 * - **Byoyomi exceso (consumo de periodo):** si `remaining` llega a 0 en fase
 *   byoyomi con `periods > 1`, se decrementa `periods` y se reinicia `remaining`
 *   al `periodMs`.
 * - **Byoyomi último periodo:** si `remaining` llega a 0 con `periods == 1`,
 *   se decrementa `periods` a 0 y se emite `timeoutColor` en el `TickOutcome`.
 */
object ClockLogic {

    /**
     * Resultado de un [tick]. [state] es el nuevo estado a publicar; si
     * [timeoutColor] es no-nulo, el reloj quedó detenido por haber agotado el
     * tiempo del bando indicado — el caller debe emitir ese color en el
     * `timeoutEvents` SharedFlow.
     */
    data class TickOutcome(
        val state: ClockState,
        val timeoutColor: CobColor? = null,
    )

    /**
     * Arranca el reloj para [color] a partir de [now]. Si ya estaba corriendo
     * para otro color, transfiere el ownership sin interrumpir el tick loop
     * (se reinicia `lastTickEpochMs` y `activeMoveStartEpochMs`).
     */
    fun start(state: ClockState, color: CobColor, now: Long): ClockState =
        state.copy(
            running = true,
            activeColor = color,
            lastTickEpochMs = now,
            activeMoveStartEpochMs = now,
        )

    /**
     * Aplica un tick al [state] usando el timestamp [now]. Descuenta el delta
     * `now - state.lastTickEpochMs` del [ClockState.activeColor] y decide si
     * transita a byoyomi, consume un periodo o emite timeout.
     *
     * Es idempotente para `delta == 0`: solo actualiza `lastTickEpochMs`.
     */
    fun tick(state: ClockState, now: Long): TickOutcome {
        val color = state.activeColor
        if (!state.running || color == null) return TickOutcome(state)
        if (state.mode is TimeControlMode.Unlimited) return TickOutcome(state)

        val delta = (now - state.lastTickEpochMs).coerceAtLeast(0L)
        if (delta == 0L) return TickOutcome(state.copy(lastTickEpochMs = now))

        val remainingBefore = state.remainingOf(color)
        val remainingAfter = (remainingBefore - delta).coerceAtLeast(0L)

        val ticked = state
            .withRemaining(color, remainingAfter)
            .copy(lastTickEpochMs = now)

        if (remainingAfter > 0L) return TickOutcome(ticked)

        // remainingAfter == 0 — aplicar semántica del modo activo
        return when (val mode = state.mode) {
            is TimeControlMode.Byoyomi -> handleByoyomiZero(ticked, color, mode)
            // SuddenDeath, Fischer, Bronstein: cero implica timeout inmediato
            else -> TickOutcome(
                state = ticked.copy(running = false),
                timeoutColor = color,
            )
        }
    }

    /**
     * Aplica incremento/reset al [state] cuando [colorWhoMoved] completa un
     * medio-movimiento en [now].
     *
     * - [TimeControlMode.Unlimited], [TimeControlMode.SuddenDeath]: no-op.
     * - [TimeControlMode.Fischer]: suma `incrementMs` al remaining.
     * - [TimeControlMode.Bronstein]: suma `min(elapsed, delayMs)` al remaining,
     *   donde `elapsed = now - activeMoveStartEpochMs`. Esto equivale a haber
     *   consumido solo `max(0, elapsed - delayMs)` del reloj.
     * - [TimeControlMode.Byoyomi]: si el jugador ya está `inByoyomi`, resetea
     *   remaining a `periodMs` para el próximo turno. Si aún está en fase base,
     *   no hace nada (el tiempo que quedó, queda).
     */
    fun applyIncrement(state: ClockState, colorWhoMoved: CobColor, now: Long): ClockState =
        when (val mode = state.mode) {
            is TimeControlMode.Unlimited -> state
            is TimeControlMode.SuddenDeath -> state

            is TimeControlMode.Fischer -> {
                val newRemaining = state.remainingOf(colorWhoMoved) + mode.incrementMs
                state.withRemaining(colorWhoMoved, newRemaining)
            }

            is TimeControlMode.Bronstein -> {
                val elapsed = (now - state.activeMoveStartEpochMs).coerceAtLeast(0L)
                val credit = minOf(elapsed, mode.delayMs)
                val newRemaining = state.remainingOf(colorWhoMoved) + credit
                state.withRemaining(colorWhoMoved, newRemaining)
            }

            is TimeControlMode.Byoyomi -> {
                if (state.inByoyomiOf(colorWhoMoved)) {
                    state.withRemaining(colorWhoMoved, mode.periodMs)
                } else {
                    state
                }
            }
        }

    // ── Byoyomi zero handling ─────────────────────────────────────────────────

    private fun handleByoyomiZero(
        ticked: ClockState,
        color: CobColor,
        mode: TimeControlMode.Byoyomi,
    ): TickOutcome {
        val inByoyomi = ticked.inByoyomiOf(color)
        val periods = ticked.periodsOf(color)

        return when {
            // Transición base → byoyomi: primer periodo arranca sin consumir contador.
            !inByoyomi -> TickOutcome(
                ticked
                    .withRemaining(color, mode.periodMs)
                    .withInByoyomi(color, true),
            )

            // Exceso en byoyomi con periodos disponibles: consume uno y reinicia.
            periods > 1 -> TickOutcome(
                ticked
                    .withRemaining(color, mode.periodMs)
                    .withPeriodsDecrement(color),
            )

            // Último periodo agotado — timeout.
            else -> TickOutcome(
                state = ticked
                    .copy(running = false)
                    .withPeriodsDecrement(color),
                timeoutColor = color,
            )
        }
    }

    // ── ClockState accessors / transformers (extension functions) ─────────────

    private fun ClockState.remainingOf(color: CobColor): Long = when (color) {
        CobColor.WHITE -> whiteRemainingMs
        CobColor.BLACK -> blackRemainingMs
    }

    private fun ClockState.periodsOf(color: CobColor): Int = when (color) {
        CobColor.WHITE -> whiteByoyomiPeriodsLeft
        CobColor.BLACK -> blackByoyomiPeriodsLeft
    }

    private fun ClockState.inByoyomiOf(color: CobColor): Boolean = when (color) {
        CobColor.WHITE -> whiteInByoyomi
        CobColor.BLACK -> blackInByoyomi
    }

    private fun ClockState.withRemaining(color: CobColor, ms: Long): ClockState = when (color) {
        CobColor.WHITE -> copy(whiteRemainingMs = ms)
        CobColor.BLACK -> copy(blackRemainingMs = ms)
    }

    private fun ClockState.withInByoyomi(color: CobColor, value: Boolean): ClockState = when (color) {
        CobColor.WHITE -> copy(whiteInByoyomi = value)
        CobColor.BLACK -> copy(blackInByoyomi = value)
    }

    private fun ClockState.withPeriodsDecrement(color: CobColor): ClockState = when (color) {
        CobColor.WHITE -> copy(
            whiteByoyomiPeriodsLeft = (whiteByoyomiPeriodsLeft - 1).coerceAtLeast(0),
        )

        CobColor.BLACK -> copy(
            blackByoyomiPeriodsLeft = (blackByoyomiPeriodsLeft - 1).coerceAtLeast(0),
        )
    }
}