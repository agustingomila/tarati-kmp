package com.agustin.tarati.core.domain.ai.engine

import kotlin.time.Clock

class SearchContext(
    val killerMoves: MutableMap<Int, MutableSet<String>> = mutableMapOf(),
    val historyTable: MutableMap<String, Int> = mutableMapOf(),
    val startTimeMs: Long = Clock.System.now().toEpochMilliseconds(),
    val maxTimeMs: Long = 30_000,
) {
    var nodesEvaluated: Long = 0
}
