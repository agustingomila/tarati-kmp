package com.agustin.tarati.services.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.time.ClockState
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel del reloj. Delega la lógica a [ClockLogic] y gestiona el tick loop
 * en [viewModelScope] (100 ms = 10 fps, suficiente para mostrar décimas en zeitnot).
 *
 * [applyIncrementAfterMove] solo acredita al bando que movió; el caller debe
 * invocar [startClockFor] con el color del nuevo turno para transferir el reloj.
 */
class ClockViewModel : ViewModel(), IClockService {

    private val logger = getLogger("ClockViewModel")

    private val _clockState = MutableStateFlow(ClockState.initial(TimeControlMode.Unlimited))
    override val clockState: StateFlow<ClockState> = _clockState.asStateFlow()

    private val _timeoutEvents = MutableSharedFlow<CobColor>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val timeoutEvents: SharedFlow<CobColor> = _timeoutEvents.asSharedFlow()

    private var tickJob: Job? = null

    // ── API ───────────────────────────────────────────────────────────────────

    override fun resetClock(mode: TimeControlMode) {
        tickJob?.cancel()
        tickJob = null
        _clockState.value = ClockState.initial(mode)
        logger.debug("Clock reset for mode: $mode")
    }

    override fun startClockFor(color: CobColor) {
        val current = _clockState.value
        if (current.mode is TimeControlMode.Unlimited) return

        val now = Clock.System.now().toEpochMilliseconds()
        _clockState.value = ClockLogic.start(current, color, now)
        ensureTickLoop()
        logger.debug("Clock started for $color")
    }

    override fun stopClock() {
        val current = _clockState.value
        if (!current.running && current.activeColor == null) return

        tickJob?.cancel()
        tickJob = null
        _clockState.value = current.copy(
            running = false,
            activeColor = null,
            lastTickEpochMs = 0L,
            activeMoveStartEpochMs = 0L,
        )
        logger.debug("Clock stopped")
    }

    override fun pauseClock() {
        val current = _clockState.value
        if (!current.running) return

        // Descontar el tiempo transcurrido hasta el momento de pausa antes de
        // detener el loop, para que el tiempo visible coincida con el real al
        // pausar durante un tick.
        val now = Clock.System.now().toEpochMilliseconds()
        val outcome = ClockLogic.tick(current, now)
        _clockState.value = outcome.state.copy(running = false)

        tickJob?.cancel()
        tickJob = null

        // Si el pausado coincide con timeout, emitirlo — edge case raro pero posible.
        outcome.timeoutColor?.let { color ->
            viewModelScope.launch { _timeoutEvents.emit(color) }
        }

        logger.debug("Clock paused")
    }

    override fun resumeClock() {
        val current = _clockState.value
        if (current.running) return
        val color = current.activeColor ?: return
        if (current.mode is TimeControlMode.Unlimited) return

        // Reset timestamps al reanudar. Esto causa que, si pause/resume ocurre
        // en medio de un medio-movimiento en Bronstein, el delay completo esté
        // disponible otra vez — aceptable para pausas (evento excepcional).
        val now = Clock.System.now().toEpochMilliseconds()
        _clockState.value = current.copy(
            running = true,
            lastTickEpochMs = now,
            activeMoveStartEpochMs = now,
        )
        ensureTickLoop()
        logger.debug("Clock resumed for $color")
    }

    override fun applyIncrementAfterMove(colorWhoMoved: CobColor) {
        val now = Clock.System.now().toEpochMilliseconds()
        val before = _clockState.value
        _clockState.value = ClockLogic.applyIncrement(before, colorWhoMoved, now)
        logger.debug(
            "Increment applied for $colorWhoMoved: " +
                    "${before.whiteRemainingMs}/${before.blackRemainingMs} → " +
                    "${_clockState.value.whiteRemainingMs}/${_clockState.value.blackRemainingMs}"
        )
    }

    // ── Tick loop ─────────────────────────────────────────────────────────────

    private fun ensureTickLoop() {
        if (tickJob?.isActive == true) return

        tickJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_INTERVAL_MS.milliseconds)

                val now = Clock.System.now().toEpochMilliseconds()
                val outcome = ClockLogic.tick(_clockState.value, now)
                _clockState.value = outcome.state

                val timeoutColor = outcome.timeoutColor
                if (timeoutColor != null) {
                    _timeoutEvents.emit(timeoutColor)
                    logger.debug("Timeout for $timeoutColor")
                    break
                }

                // Parada externa (stopClock / pauseClock) — salir limpiamente.
                if (!outcome.state.running) break
            }
            tickJob = null
        }
    }

    companion object {
        /** Intervalo entre ticks del reloj. 100 ms = 10 fps, suficiente para UI digital. */
        private const val TICK_INTERVAL_MS = 100L
    }
}