package com.agustin.tarati.core.domain.ai.engine

import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.game.board.GameBoard.centerVertices
import com.agustin.tarati.core.domain.game.board.GameBoard.homeBases
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.core.domain.game.play.GameState

class BoardEvaluator {

    fun evaluate(gameState: GameState, config: EvaluationConfig): Double {
        val m = evaluateMetrics(gameState, config)
        return with(config) {
            m.material.difference +
                    m.centerControl.difference * controlCenterScore +
                    m.mobility.difference * mobilityScore +
                    m.homeControl.difference * domesticControlScore +
                    m.opponentPressure.difference * opponentDomesticPressureScore +
                    m.upgradeOpportunities.difference * upgradeScore +
                    m.forcedPromotionOpportunities.difference * forcedPromotionScore
        }
    }

    fun evaluateMetrics(gameState: GameState, config: EvaluationConfig): BoardMetrics =
        calculateBoardMetrics(gameState, config)

    private data class MutableMetrics(
        var material: Double = 0.0,
        var centerControl: Int = 0,
        var mobility: Int = 0,
        var homeControl: Int = 0,
        var opponentPressure: Int = 0,
        var upgradeOpportunities: Int = 0,
        var forcedPromotionOpportunities: Int = 0,
    )

    private fun calculateBoardMetrics(gameState: GameState, config: EvaluationConfig): BoardMetrics {
        val white = MutableMetrics()
        val black = MutableMetrics()

        for ((vertex, cob) in gameState.cobs) {
            val m = if (cob.color == WHITE) white else black
            val materialValue = if (cob.isUpgraded) config.rocScore else config.cobScore

            m.material += materialValue
            if (vertex in centerVertices) m.centerControl++
            m.mobility += cob.calculateMobility(gameState, vertex)
            if (vertex in homeBases[cob.color]!!) m.homeControl++
            if (vertex in homeBases[cob.color.opponent]!!) m.opponentPressure++
            if (cob.canUpgrade(vertex)) m.upgradeOpportunities++
            if (!cob.isUpgraded && gameState.isDeadCob(vertex, cob)) m.forcedPromotionOpportunities++
        }

        return BoardMetrics(
            material = ColorStat(white.material, black.material),
            centerControl = ColorStat(white.centerControl, black.centerControl),
            mobility = ColorStat(white.mobility, black.mobility),
            homeControl = ColorStat(white.homeControl, black.homeControl),
            opponentPressure = ColorStat(white.opponentPressure, black.opponentPressure),
            upgradeOpportunities = ColorStat(white.upgradeOpportunities, black.upgradeOpportunities),
            forcedPromotionOpportunities = ColorStat(
                white.forcedPromotionOpportunities,
                black.forcedPromotionOpportunities,
            ),
        )
    }

    /**
     * Holds a white and black measurement for a single board metric.
     * [difference] is always white minus black, matching the engine's
     * maximizing-from-white convention.
     */
    data class ColorStat<T : Number>(val white: T, val black: T) {
        val difference: Double get() = white.toDouble() - black.toDouble()
    }

    data class BoardMetrics(
        val material: ColorStat<Double>,
        val centerControl: ColorStat<Int>,
        val mobility: ColorStat<Int>,
        val homeControl: ColorStat<Int>,
        val opponentPressure: ColorStat<Int>,
        val upgradeOpportunities: ColorStat<Int>,
        val forcedPromotionOpportunities: ColorStat<Int>,
    )
}