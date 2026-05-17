package com.agustin.tarati.core.domain.ai.evaluator

import com.agustin.tarati.core.domain.game.play.Move

data class MoveEval(
    val score: Double,
    val move: Move?,
)
