package com.agustin.tarati.core.domain.game.time

import com.agustin.tarati.core.domain.game.time.TimeControlPresets.Unlimited
import com.agustin.tarati.core.domain.game.time.TimeControlPresets.all


/**
 * Presets de [TimeControlMode] ofrecidos por la UI de settings.
 *
 * Los nombres y valores siguen las convenciones de ajedrez online:
 * - **Blitz**: 3–5 minutos por bando.
 * - **Rapid**: 10–15 minutos.
 * - **Classical**: 30+ minutos.
 * - **Bronstein**: un clásico con delay compensado.
 * - **Byoyomi**: estilo go/shōgi con períodos.
 *
 * La lista [all] define el orden en que aparecen en el selector. [Unlimited] va
 * primero por ser el default y compatible con todas las partidas existentes.
 */
object TimeControlPresets {

    val Unlimited: TimeControlMode = TimeControlMode.Unlimited

    val Blitz3Plus2: TimeControlMode = TimeControlMode.Fischer(
        baseMs = 3 * 60_000L,
        incrementMs = 2_000L,
    )

    val Blitz5Plus0: TimeControlMode = TimeControlMode.SuddenDeath(
        totalMs = 5 * 60_000L,
    )

    val Rapid10Plus5: TimeControlMode = TimeControlMode.Fischer(
        baseMs = 10 * 60_000L,
        incrementMs = 5_000L,
    )

    val Classical30Plus30: TimeControlMode = TimeControlMode.Fischer(
        baseMs = 30 * 60_000L,
        incrementMs = 30_000L,
    )

    val Bronstein5Plus3: TimeControlMode = TimeControlMode.Bronstein(
        baseMs = 5 * 60_000L,
        delayMs = 3_000L,
    )

    val Byoyomi5Plus3x30: TimeControlMode = TimeControlMode.Byoyomi(
        baseMs = 5 * 60_000L,
        periodMs = 30_000L,
        periods = 3,
    )

    /** Lista ordenada de presets mostrados en el selector de settings. */
    val all: List<TimeControlMode> = listOf(
        Unlimited,
        Blitz3Plus2,
        Blitz5Plus0,
        Rapid10Plus5,
        Classical30Plus30,
        Bronstein5Plus3,
        Byoyomi5Plus3x30,
    )

    /** Preset por defecto para partidas nuevas. */
    val default: TimeControlMode get() = Unlimited
}