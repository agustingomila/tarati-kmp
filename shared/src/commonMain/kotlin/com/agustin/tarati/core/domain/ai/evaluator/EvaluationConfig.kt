package com.agustin.tarati.core.domain.ai.evaluator

import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig.Companion.withPersonality
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfigBuilder.defensive
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfigBuilder.gambit
import com.agustin.tarati.core.domain.ai.services.Difficulty

/**
 * Full configuration for the AI engine.
 *
 * Parameters are grouped into five cohesive sub-objects:
 *  - [material]   — piece values and capture bonuses
 *  - [positional] — board control, mobility, territory, promotion
 *  - [tactical]   — quick threat and attack weights used in move ordering
 *  - [search]     — move-ordering heuristics within the search tree
 *  - [behavior]   — win thresholds, draw avoidance, stalling, difficulty handicaps
 *
 * Flat accessors are provided for all sub-object fields so that existing
 * consumers ([BoardEvaluator],
 * [MoveEvaluator],
 * [MinimaxStrategy], etc.)
 * continue to compile without modification.
 */
data class EvaluationConfig(
    val difficulty: Difficulty = Difficulty.DEFAULT,
    val name: String = difficulty.name,
    val material: MaterialWeights = MaterialWeights(),
    val positional: PositionalWeights = PositionalWeights(),
    val tactical: TacticalWeights = TacticalWeights(),
    val search: SearchWeights = SearchWeights(),
    val behavior: BehaviorConfig = BehaviorConfig(),
) {
    // ── MaterialWeights accessors ────────────────────────────────────────────
    val cobScore get() = material.cobScore
    val rocScore get() = material.rocScore
    val flipCobBonus get() = material.flipCobBonus
    val flipRocBonus get() = material.flipRocBonus
    val cobFlipMultiplier get() = material.cobFlipMultiplier
    val rocFlipMultiplier get() = material.rocFlipMultiplier

    // ── PositionalWeights accessors ──────────────────────────────────────────
    val controlCenterScore get() = positional.controlCenterScore
    val controlCenterMultiplier get() = positional.controlCenterMultiplier
    val mobilityScore get() = positional.mobilityScore
    val mobilityImprovementMultiplier get() = positional.mobilityImprovementMultiplier
    val domesticControlScore get() = positional.domesticControlScore
    val opponentDomesticPressureScore get() = positional.opponentDomesticPressureScore
    val upgradeScore get() = positional.upgradeScore
    val upgradeBonusMultiplier get() = positional.upgradeBonusMultiplier
    val forcedPromotionScore get() = positional.forcedPromotionScore
    val isolationPenalty get() = positional.isolationPenalty

    // ── SearchWeights accessors ──────────────────────────────────────────────
    val killerMoveBaseBonus get() = search.killerMoveBaseBonus
    val historyHeuristicMultiplier get() = search.historyHeuristicMultiplier
    val lateMoveReductionPenalty get() = search.lateMoveReductionPenalty
    val lateMoveReductionDepth get() = search.lateMoveReductionDepth
    val lmrBranchingThreshold get() = search.lmrBranchingThreshold
    val lmrDepthReduction get() = search.lmrDepthReduction
    val lmrMoveIndexThreshold get() = search.lmrMoveIndexThreshold

    // ── TacticalWeights accessors ────────────────────────────────────────────
    val quickThreatWeight get() = tactical.quickThreatWeight
    val quickAttackWeight get() = tactical.quickAttackWeight

    // ── BehaviorConfig accessors ─────────────────────────────────────────────
    val winningScore get() = behavior.winningScore
    val winningThreshold get() = behavior.winningThreshold
    val winningPositionThreshold get() = behavior.winningPositionThreshold
    val repetitionPenaltyMultiplier get() = behavior.repetitionPenaltyMultiplier
    val immediateWinBonusMultiplier get() = behavior.immediateWinBonusMultiplier
    val stallingPenalty get() = behavior.stallingPenalty
    val stallingThreshold get() = behavior.stallingThreshold
    val randomMoveChance get() = behavior.randomMoveChance
    val evalNoise get() = behavior.evalNoise

    companion object {
        val DEFAULT = EvaluationConfig()

        // EASY: entiende solo conteo de piezas. Ignora flips, centro, movilidad y
        // territorio. Pierde porque no comprende el tablero, no por azar.
        val EASY = EvaluationConfig(
            difficulty = Difficulty.EASY,
            material = MaterialWeights(
                cobScore = 100.0,
                rocScore = 150.0,
                flipCobBonus = 0.0,
                flipRocBonus = 0.0,
            ),
            positional = PositionalWeights(
                controlCenterScore = 0.0,
                mobilityScore = 0.0,
                domesticControlScore = 0.0,
                opponentDomesticPressureScore = 0.0,
                upgradeScore = 0.0,
                forcedPromotionScore = 0.0,
                isolationPenalty = 0.0,
            ),
            behavior = BehaviorConfig(
                evalNoise = 8.0,
            ),
        )

        // MEDIUM: comprende material completo y el valor de los flips.
        // No valora territorio ni amenazas posicionales avanzadas.
        val MEDIUM = EvaluationConfig(
            difficulty = Difficulty.MEDIUM,
            material = MaterialWeights(
                cobScore = 190.0,
                rocScore = 360.0,
                flipCobBonus = 77.0,
                flipRocBonus = 100.0,
            ),
            positional = PositionalWeights(
                controlCenterScore = 20.0,
                mobilityScore = 8.0,
            ),
            behavior = BehaviorConfig(
                evalNoise = 4.0,
            ),
        )

        // HARD: comprende material, flips, centro y presión territorial.
        // No explota piezas aisladas ni oportunidades de promoción forzada.
        val HARD = EvaluationConfig(
            difficulty = Difficulty.HARD,
            material = MaterialWeights(
                cobScore = 206.0,
                rocScore = 414.0,
                flipCobBonus = 77.0,
                flipRocBonus = 220.0,
            ),
            positional = PositionalWeights(
                controlCenterScore = 36.0,
                mobilityScore = 11.0,
                domesticControlScore = 35.0,
                opponentDomesticPressureScore = 45.0,
                upgradeScore = 160.0,
            ),
        )

        // CHAMPION: hereda la base calibrada de HARD y la extiende con conocimiento
        // táctico más profundo. Los pesos posicionales se escalan moderadamente sobre
        // HARD en lugar de introducir valores desconectados que distorsionen la búsqueda.
        val CHAMPION = EvaluationConfig(
            difficulty = Difficulty.CHAMPION,
            material = MaterialWeights(
                cobScore = 216.0,
                rocScore = 454.0,
                flipCobBonus = 85.0,
                flipRocBonus = 240.0,
            ),
            positional = PositionalWeights(
                controlCenterScore = 42.0,
                mobilityScore = 14.0,
                domesticControlScore = 45.0,
                opponentDomesticPressureScore = 55.0,
                upgradeScore = 175.0,
                forcedPromotionScore = 175.0,
                isolationPenalty = 100.0,
            ),
            behavior = BehaviorConfig(
                stallingPenalty = 8.0,
                stallingThreshold = 65,
            ),
        )

        /**
         * Applies [personality] on top of an existing [EvaluationConfig].
         *
         * The [personality] function receives this config and returns a modified version,
         * preserving the original [Difficulty] while layering the personality's weight
         * adjustments. Useful in tests and tournament runners to override the default
         * personality for a given difficulty:
         * ```
         * Difficulty.MEDIUM.withPersonality(::gambit)
         * EvaluationConfig.HARD.withPersonality(::aggressive)
         * ```
         */
        fun Difficulty.withPersonality(
            personality: (EvaluationConfig) -> EvaluationConfig,
        ): EvaluationConfig = personality(baseConfigFor(this))

        /**
         * Returns the raw base [EvaluationConfig] for [difficulty] without any personality
         * overlay. Intended for use by [withPersonality] and for callers that need the
         * unmodified weights (e.g. tournament runners applying a custom personality).
         */
        private fun baseConfigFor(difficulty: Difficulty): EvaluationConfig =
            when (difficulty) {
                Difficulty.EASY -> EASY
                Difficulty.MEDIUM -> MEDIUM
                Difficulty.HARD -> HARD
                Difficulty.CHAMPION -> CHAMPION
            }

        /**
         * Returns the [EvaluationConfig] for [difficulty] with its empirically the strongest
         * personality pre-applied.
         *
         * Personality selection is based on round-robin tournament results across the five
         * key personalities (baseline, defensive, gambit, balanced, positional):
         * - EASY    → no overlay — noise alone defines the skill floor.
         * - MEDIUM  → [defensive]: steady piece safety outperforms riskier styles at depth 3.
         * - HARD    → [gambit]: high-flip aggression exploits the deeper search at depth 5.
         * - CHAMPION → [gambit]: at depth 7 high-flip aggression produces the highest win rate.
         */
        fun getByDifficulty(difficulty: Difficulty): EvaluationConfig =
            when (difficulty) {
                Difficulty.EASY -> EASY
                Difficulty.MEDIUM -> defensive(MEDIUM)
                Difficulty.HARD -> gambit(HARD)
                Difficulty.CHAMPION -> gambit(CHAMPION)
            }
    }
}