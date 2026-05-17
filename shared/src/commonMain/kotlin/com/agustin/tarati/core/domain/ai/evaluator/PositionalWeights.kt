package com.agustin.tarati.core.domain.ai.evaluator

/**
 * Weights related to positional evaluation: board control, mobility,
 * territory, promotion opportunities, and piece isolation.
 *
 * Used by [BoardEvaluator] and [MoveEvaluator] (positional scoring),
 * and by [PatternPrecomputer] (strategic vertex precomputation).
 */
data class PositionalWeights(
    val controlCenterScore: Double = 25.0,
    val controlCenterMultiplier: Double = 1.0,
    val mobilityScore: Double = 10.0,
    val mobilityImprovementMultiplier: Double = 1.0,
    val domesticControlScore: Double = 30.0,
    val opponentDomesticPressureScore: Double = 40.0,
    val upgradeScore: Double = 150.0,
    val upgradeBonusMultiplier: Double = 1.2,
    val forcedPromotionScore: Double = 150.0,
    val isolationPenalty: Double = 80.0,
) {
    fun scaleCenter(score: Double = 1.0, multiplier: Double = 1.0) = copy(
        controlCenterScore = controlCenterScore * score,
        controlCenterMultiplier = controlCenterMultiplier * multiplier,
    )

    fun scaleMobility(score: Double = 1.0, multiplier: Double = 1.0) = copy(
        mobilityScore = mobilityScore * score,
        mobilityImprovementMultiplier = mobilityImprovementMultiplier * multiplier,
    )

    fun scaleDomestic(home: Double = 1.0, pressure: Double = 1.0) = copy(
        domesticControlScore = domesticControlScore * home,
        opponentDomesticPressureScore = opponentDomesticPressureScore * pressure,
    )

    fun scaleUpgrade(score: Double = 1.0, multiplier: Double = 1.0) = copy(
        upgradeScore = upgradeScore * score,
        upgradeBonusMultiplier = upgradeBonusMultiplier * multiplier,
    )

    fun scaleForced(factor: Double) = copy(
        forcedPromotionScore = forcedPromotionScore * factor,
    )

    fun scaleIsolation(factor: Double) = copy(
        isolationPenalty = isolationPenalty * factor,
    )
}