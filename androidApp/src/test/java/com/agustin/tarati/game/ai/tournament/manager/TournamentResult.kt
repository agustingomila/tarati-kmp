package com.agustin.tarati.game.ai.tournament.manager

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.game.ai.tournament.helpers.PerformanceMetrics
import kotlin.math.log10
import kotlin.math.roundToInt

data class TournamentResult(
    val engineA: IAIEngine,
    val engineB: IAIEngine,
    val configA: EvaluationConfig?,
    val configB: EvaluationConfig?,
    val winsA: Int,
    val winsB: Int,
    val draws: Int,
    val totalGames: Int,
    val averageMoves: Double,
    val averageMovesA: Double,
    val averageMovesB: Double,
    val timeoutsA: Int,
    val timeoutsB: Int,
    val performanceMetrics: PerformanceMetrics? = null,
    val logInfo: (String) -> Unit,
) {
    val winRateA: Double get() = winsA.toDouble() / totalGames
    val winRateB: Double get() = winsB.toDouble() / totalGames
    val scoreA: Double get() = winsA + (draws * 0.5)
    val scoreB: Double get() = winsB + (draws * 0.5)
    val eloDifference: Int get() = calculateEloDifference(winRateA)

    fun printSummary() {
        logInfo("\n" + "=".repeat(70))
        logInfo("TOURNAMENT RESULTS")
        logInfo("=".repeat(70))
        logInfo("${engineA.name} vs ${engineB.name}")
        logInfo("-".repeat(70))
        logInfo("Games played: $totalGames")
        logInfo("")

        logInfo("${engineA.name}:")
        logInfo("  Wins: $winsA (${(winRateA * 100).roundToInt()}%)")
        logInfo("  Losses: $winsB")
        logInfo("  Draws: $draws")
        logInfo("  Score: ${"%.1f".format(scoreA)} / $totalGames")
        logInfo("  Avg moves (wins): ${"%.1f".format(averageMovesA)}")
        logInfo("  Timeouts: $timeoutsA")
        logInfo("  Elo Δ: +$eloDifference")
        logInfo("")

        logInfo("${engineB.name}:")
        logInfo("  Wins: $winsB (${(winRateB * 100).roundToInt()}%)")
        logInfo("  Losses: $winsA")
        logInfo("  Draws: $draws")
        logInfo("  Score: ${"%.1f".format(scoreB)} / $totalGames")
        logInfo("  Avg moves (wins): ${"%.1f".format(averageMovesB)}")
        logInfo("  Timeouts: $timeoutsB")
        logInfo("  Elo Δ: -$eloDifference")
        logInfo("")

        logInfo("Overall avg moves: ${"%.1f".format(averageMoves)}")

        performanceMetrics?.let { metrics ->
            logInfo("")
            logInfo("Performance Metrics:")
            logInfo("  Cache hit rate: ${"%.1f%%".format(metrics.averageCacheHitRate * 100)}")
            logInfo("  Average nodes per move: ${metrics.averageNodesPerMove.toInt()}")
            logInfo("  Average cutoffs per move: ${metrics.averageCutoffsPerMove.toInt()}")
        }

        logInfo("=".repeat(70))
    }

    private fun calculateEloDifference(winRate: Double): Int {
        if (winRate >= 0.999) return 800
        if (winRate <= 0.001) return -800
        return (-400 * log10(1 / winRate - 1)).roundToInt()
    }
}
