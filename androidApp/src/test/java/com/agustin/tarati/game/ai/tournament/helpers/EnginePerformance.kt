package com.agustin.tarati.game.ai.tournament.helpers

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig

data class EnginePerformance(
    val engine: IAIEngine,
    val config: EvaluationConfig? = null,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val totalGames: Int = 0,
    val totalMoves: Int = 0,
    val timeouts: Int = 0,
    val performanceMetrics: MutableList<PerformanceMetrics> = mutableListOf(),
) {
    val winRate: Double get() = if (totalGames > 0) wins.toDouble() / totalGames else 0.0
    val score: Double get() = wins + (draws * 0.5)
    val averageMoves: Double get() = if (totalGames > 0) totalMoves.toDouble() / totalGames else 0.0
    val averagePerformance: PerformanceMetrics?
        get() =
            if (performanceMetrics.isEmpty()) {
                null
            } else {
                PerformanceMetrics(
                    averageCacheHitRate = performanceMetrics.map { it.averageCacheHitRate }.average(),
                    averageNodesPerMove = performanceMetrics.map { it.averageNodesPerMove }.average(),
                    averageCutoffsPerMove = performanceMetrics.map { it.averageCutoffsPerMove }.average(),
                    averageMoveTimeMs = performanceMetrics.map { it.averageMoveTimeMs }.average(),
                )
            }

    fun withResults(
        additionalWins: Int = 0,
        additionalLosses: Int = 0,
        additionalDraws: Int = 0,
        additionalMoves: Int = 0,
        additionalTimeouts: Int = 0,
        additionalMetrics: PerformanceMetrics? = null,
    ): EnginePerformance {
        val newMetrics = performanceMetrics.toMutableList()
        additionalMetrics?.let { newMetrics.add(it) }

        return copy(
            wins = wins + additionalWins,
            losses = losses + additionalLosses,
            draws = draws + additionalDraws,
            totalGames = totalGames + additionalWins + additionalLosses + additionalDraws,
            totalMoves = totalMoves + additionalMoves,
            timeouts = timeouts + additionalTimeouts,
            performanceMetrics = newMetrics,
        )
    }
}
