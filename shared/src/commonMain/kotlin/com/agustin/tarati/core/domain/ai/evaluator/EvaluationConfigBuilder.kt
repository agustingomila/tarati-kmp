package com.agustin.tarati.core.domain.ai.evaluator

object EvaluationConfigBuilder {
    fun baseline() = EvaluationConfig(name = "Default")

    /**
     * Maximizes capture value and immediate pressure.
     * Mobility is kept intact — in Tarati it correlates directly with flip opportunities.
     */
    fun aggressive(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Aggressive",
            material = base.material.scaleFlips(cob = 1.3, rok = 1.35),
            positional = base.positional
                .scaleDomestic(pressure = 1.3)
                .scaleMobility(score = 1.1),
            tactical = base.tactical.scaleThreats(1.4),
            behavior = base.behavior.scaleRepetition(0.7),
        )

    /**
     * Solid and counter-attacking — prioritizes piece safety and home control
     * without surrendering the initiative entirely.
     * flipCobBonus is not reduced: capturing is always correct in Tarati.
     */
    fun defensive(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Defensive",
            material = base.material.scalePieces(cob = 1.2, rok = 1.15),
            positional = base.positional
                .scaleCenter(score = 1.2)
                .scaleDomestic(home = 1.5)
                .scaleUpgrade(score = 1.3)
                .scaleIsolation(1.5),
        )

    /**
     * Pure materialism — maximizes piece count and capture value.
     * Low mobility keeps the focus on piece counts over connectivity.
     */
    fun material(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Material",
            material = base.material
                .scalePieces(cob = 1.3, rok = 1.2)
                .scaleFlips(cob = 1.15, rok = 1.1),
            positional = base.positional.scaleMobility(score = 0.8),
        )

    /**
     * Controls key squares and outmaneuvers opponents structurally.
     * Positional advantage is expressed by rewarding good squares more,
     * not by devaluing captures.
     */
    fun positional(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Positional",
            positional = base.positional
                .scaleCenter(score = 1.5)
                .scaleDomestic(home = 1.4, pressure = 1.15)
                .scaleMobility(score = 1.25)
                .scaleIsolation(1.3),
        )

    /**
     * Small uniform boosts across all factors — close to baseline but
     * slightly more dynamic.
     */
    fun balanced(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Balanced",
            material = base.material
                .scalePieces(cob = 0.95)
                .scaleFlips(cob = 1.05),
            positional = base.positional
                .scaleCenter(score = 1.1)
                .scaleMobility(score = 1.15)
                .scaleUpgrade(score = 0.9),
        )

    /**
     * Swarms the opponent with mobile, connected pieces.
     * Piece count is not penalized — mobility without material is unsustainable.
     */
    fun swarming(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Swarming",
            material = base.material.scaleFlips(cob = 1.2),
            positional = base.positional
                .scaleMobility(score = 1.5)
                .scaleDomestic(pressure = 1.3),
            tactical = base.tactical.scaleThreats(1.4),
            search = base.search.scaleLmr(depth = base.search.lateMoveReductionDepth + 1),
        )

    /**
     * Long-game planner — values promotion, territory control, and structural
     * dominance. quickAttackWeight keeps it responsive to immediate tactics
     * so it does not fall behind in shorter games.
     */
    fun strategist(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Strategist",
            positional = base.positional
                .scaleCenter(score = 1.3)
                .scaleDomestic(home = 1.3, pressure = 1.25)
                .scaleUpgrade(score = 1.4)
                .scaleForced(1.4),
            tactical = base.tactical.scaleAttack(1.2),
            behavior = base.behavior.scaleWinning(positionThreshold = 0.9),
        )

    /**
     * Accepts slight material risk in exchange for high tactical return.
     * Avoids draws by penalizing repetition.
     */
    fun gambit(base: EvaluationConfig = baseline()) =
        base.copy(
            name = "Gambit",
            material = base.material
                .scalePieces(cob = 0.90)
                .scaleFlips(cob = 1.4, rok = 1.25),
            tactical = base.tactical.scale(threat = 1.35, attack = 1.3),
            search = base.search.scaleKiller(1.2),
            behavior = base.behavior
                .scaleWinning(threshold = 1.06)
                .scaleRepetition(1.5),
        )

    @Suppress("unused")
    fun custom(block: EvaluationConfig.() -> EvaluationConfig): EvaluationConfig = baseline().block()

    @Suppress("unused")
    fun applyPersonality(
        baseConfig: EvaluationConfig,
        personality: (EvaluationConfig) -> EvaluationConfig,
    ): EvaluationConfig = personality(baseConfig)
}