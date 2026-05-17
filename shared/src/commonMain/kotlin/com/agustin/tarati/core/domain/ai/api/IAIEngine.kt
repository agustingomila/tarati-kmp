package com.agustin.tarati.core.domain.ai.api

import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.ai.evaluator.MoveEval
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState

interface IAIEngine {
    val name: String

    val positionHistory: MutableMap<String, Int>

    fun getNextMove(gameState: GameState): MoveEval

    fun clearHistory()

    fun putState(
        gameState: GameState,
        moveBy: CobColor,
    ): CobColor?

    fun removeState(gameState: GameState)

    fun setConfig(config: EvaluationConfig)

    fun getDiagnostics(): AIDiagnostics?
}