package com.agustin.tarati.core.domain.ai.strategy

import com.agustin.tarati.core.domain.ai.evaluator.MoveEval
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.play.GameState

interface IAIStrategy {
    fun getNextMove(
        gameState: GameState,
        difficulty: Difficulty,
    ): MoveEval
}
