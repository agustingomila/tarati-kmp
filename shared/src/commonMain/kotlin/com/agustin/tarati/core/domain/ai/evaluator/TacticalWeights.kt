package com.agustin.tarati.core.domain.ai.evaluator

/**
 * Weights used in [MoveEvaluator.quickEvaluate] to score immediate tactical
 * threats and attack potential without a full board evaluation.
 *
 * - [quickThreatWeight]  — penalty per threat the opponent can make on the current piece.
 * - [quickAttackWeight]  — multiplier on the total flip potential reachable from this position.
 */
data class TacticalWeights(
    val quickThreatWeight: Double = 15.0,
    val quickAttackWeight: Double = 25.0,
) {
    fun scaleThreats(factor: Double) = copy(quickThreatWeight = quickThreatWeight * factor)
    fun scaleAttack(factor: Double) = copy(quickAttackWeight = quickAttackWeight * factor)
    fun scale(threat: Double = 1.0, attack: Double = 1.0) = copy(
        quickThreatWeight = quickThreatWeight * threat,
        quickAttackWeight = quickAttackWeight * attack,
    )
}