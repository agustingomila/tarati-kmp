package com.agustin.tarati.game.ai.legacy

import com.agustin.tarati.core.domain.ai.cache.HybridEvaluationCache
import com.agustin.tarati.core.domain.ai.cache.TranspositionTable
import com.agustin.tarati.core.domain.ai.engine.BoardEvaluator
import com.agustin.tarati.core.domain.ai.engine.MoveEvaluator
import com.agustin.tarati.core.domain.ai.engine.SearchContext
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.ai.evaluator.MoveEval
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.ai.strategy.IAIStrategy
import com.agustin.tarati.core.domain.game.pieces.isMaximizingPlayer
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Snapshot of [MinimaxStrategy]
 * captured before the rok branching-factor reduction was introduced.
 *
 * Used exclusively by [LegacyTaratiAI] in regression tests that pit the
 * legacy search against the new version. No production code should reference
 * this class — it exists only so the test suite can instantiate both engines
 * from a single build.
 *
 * Snapshot state: includes the positionHistory decoupling fix (isGameOver/getWinner
 * called with explicit positionHistory instead of TaratiAI.instance) so that tournament
 * games use the correct per-engine history rather than the global singleton.
 * Further optimizations (LMR branching) are intentionally absent — that is the
 * change this snapshot exists to compare against.
 */
internal class LegacyMinimaxStrategy(
    private val boardEvaluator: BoardEvaluator,
    private val moveEvaluator: MoveEvaluator,
    private val transpositionTable: TranspositionTable,
    private val cache: HybridEvaluationCache,
    private val positionHistory: Map<String, Int>,
    private val evalConfig: () -> EvaluationConfig,
) : IAIStrategy {
    private var nodesEvaluated = 0
    private var cacheHits = 0
    private var cutoffs = 0
    private val timeLimitMs = 10_000L

    override fun getNextMove(
        gameState: GameState,
        difficulty: Difficulty,
    ): MoveEval {
        resetStats()
        return iterativeDeepening(gameState, difficulty)
    }

    private fun iterativeDeepening(
        gameState: GameState,
        difficulty: Difficulty,
    ): MoveEval {
        val config = evalConfig()
        val maxDepth = min(difficulty.depth, Difficulty.MAX.depth)
        var bestResult = MoveEval(0.0, null)

        val context = SearchContext(maxTimeMs = timeLimitMs)
        lastContext = context

        for (depth in 1..maxDepth) {
            val currentResult =
                minimax(
                    gameState = gameState,
                    depth = depth,
                    alpha = Double.NEGATIVE_INFINITY,
                    beta = Double.POSITIVE_INFINITY,
                    config = config,
                    context = context,
                )

            bestResult = currentResult

            if (isWinningScore(bestResult.score, config)) break
            if (shouldStopSearch(context)) break
        }

        return bestResult
    }

    private fun minimax(
        gameState: GameState,
        depth: Int,
        alpha: Double,
        beta: Double,
        config: EvaluationConfig,
        context: SearchContext,
    ): MoveEval {
        context.nodesEvaluated++

        val terminalResult = checkTerminalState(gameState, depth, config)
        if (terminalResult != null) return terminalResult

        val boardHash = gameState.hashBoard()
        transpositionTable.get(boardHash, depth)?.let {
            cacheHits++
            return it
        }

        val moves = gameState.allMovesForTurn()
        if (moves.isEmpty()) {
            val score = getCachedEvaluation(gameState, config)
            return MoveEval(score, null)
        }

        val sortedMoves =
            moveEvaluator.sortMoves(moves, gameState, gameState.currentTurn, config, depth, context)

        return searchBestMove(gameState, sortedMoves, depth, alpha, beta, config, context)
            .also { result ->
                transpositionTable.put(boardHash, depth, result)
            }
    }

    private fun searchBestMove(
        gameState: GameState,
        moves: List<Move>,
        depth: Int,
        alphaInit: Double,
        betaInit: Double,
        config: EvaluationConfig,
        context: SearchContext,
    ): MoveEval {
        val isMaximizing = gameState.currentTurn.isMaximizingPlayer()
        var bestMove: Move? = null
        var bestScore = if (isMaximizing) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        var alpha = alphaInit
        var beta = betaInit
        var tiedCount = 0

        for ((index, move) in moves.withIndex()) {
            val newState = gameState.applyMove(move)

            if (newState.isGameOver(positionHistory) && newState.getWinner(positionHistory) == gameState.currentTurn) {
                val score = if (isMaximizing) config.winningScore else -config.winningScore
                return MoveEval(score, move)
            }

            val newHash = newState.hashBoard()
            val futureCount = (positionHistory[newHash] ?: 0) + 1

            if (futureCount >= 3) {
                val penalty = -config.winningScore * config.repetitionPenaltyMultiplier
                val score = if (isMaximizing) penalty else -penalty

                if ((isMaximizing && score > bestScore) || (!isMaximizing && score < bestScore)) {
                    bestScore = score
                    bestMove = move
                }
                continue
            }

            val result = minimax(newState, depth - 1, alpha, beta, config, context)
            val score = result.score

            val causesCutoff = shouldPrune(score, alpha, beta, isMaximizing, config)
            if (causesCutoff && index < 3) {
                moveEvaluator.recordKillerMove(move, depth, context)
            }

            val isNewBest = if (isMaximizing) score > bestScore else score < bestScore
            val isTied = score == bestScore
            if (isNewBest) {
                bestScore = score
                bestMove = move
                tiedCount = 1
                if (!causesCutoff) {
                    moveEvaluator.recordHistoryMove(move, depth, context)
                }
            } else if (isTied) {
                tiedCount++
                if (Random.nextDouble() < 1.0 / tiedCount) {
                    bestMove = move
                }
            }

            when {
                isMaximizing -> alpha = max(alpha, bestScore)
                else -> beta = min(beta, bestScore)
            }

            if (causesCutoff) {
                cutoffs++
                break
            }
        }

        return MoveEval(bestScore, bestMove ?: moves.firstOrNull())
    }

    private fun getCachedEvaluation(gameState: GameState, config: EvaluationConfig): Double {
        val base = cache.getFullEvaluation(gameState) ?: boardEvaluator.evaluate(gameState, config).also {
            cache.putFullEvaluation(gameState, it)
        }
        val noise = config.evalNoise
        return if (noise > 0.0) base + (Random.nextDouble() - 0.5) * noise else base
    }

    private fun checkTerminalState(
        gameState: GameState,
        depth: Int,
        config: EvaluationConfig,
    ): MoveEval? {
        if (depth != 0 && !gameState.isGameOver(positionHistory)) return null
        val score =
            if (gameState.isGameOver(positionHistory)) {
                when (gameState.getWinner(positionHistory)) {
                    gameState.currentTurn -> config.winningScore
                    gameState.currentTurn.opponent -> -config.winningScore
                    else -> getCachedEvaluation(gameState, config)
                }
            } else {
                getCachedEvaluation(gameState, config)
            }
        return MoveEval(score, null)
    }

    private fun shouldPrune(
        bestScore: Double,
        alpha: Double,
        beta: Double,
        isMaximizing: Boolean,
        config: EvaluationConfig,
    ): Boolean {
        val winThreshold = config.winningScore * config.winningThreshold
        return (isMaximizing && bestScore >= winThreshold) ||
                (!isMaximizing && bestScore <= -winThreshold) ||
                beta <= alpha
    }

    private fun isWinningScore(score: Double, config: EvaluationConfig): Boolean {
        val winThreshold = config.winningScore * config.winningThreshold
        return score >= winThreshold || score <= -winThreshold
    }

    private fun shouldStopSearch(context: SearchContext): Boolean {
        val elapsed = System.currentTimeMillis() - context.startTimeMs
        return elapsed > context.maxTimeMs * 0.9
    }

    private fun resetStats() {
        nodesEvaluated = 0
        cacheHits = 0
        cutoffs = 0
    }

    fun getStats(): Triple<Long, Int, Int> = Triple(
        nodesEvaluated.toLong() + lastContext.nodesEvaluated,
        cutoffs,
        cacheHits,
    )

    private var lastContext: SearchContext = SearchContext()
}