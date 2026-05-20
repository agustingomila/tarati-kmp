package com.agustin.tarati.core.domain.ai.engine

import kotlin.time.Clock

class SearchContext(
    val killerMoves: MutableMap<Int, MutableSet<String>> = mutableMapOf(),
    val historyTable: MutableMap<String, Int> = mutableMapOf(),
    val startTimeMs: Long = Clock.System.now().toEpochMilliseconds(),
    val maxTimeMs: Long = 30_000,
) {
    var nodesEvaluated: Long = 0

    // Tracks when the last animation yield occurred. Updated by MinimaxStrategy
    // so that yieldForAnimation() is called at most once per YIELD_INTERVAL_MS.
    var lastYieldTimeMs: Long = startTimeMs
}
