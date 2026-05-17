package com.agustin.tarati.core.domain.ai.engine

import com.agustin.tarati.core.domain.ai.api.AIDiagnostics
import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.cache.HybridEvaluationCache
import com.agustin.tarati.core.domain.ai.cache.TranspositionTable
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.ai.evaluator.MoveEval
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.ai.strategy.IAIStrategy
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.random.Random

/**
 * The AI engine for Tarati. Each instance owns its full search state:
 * evaluation config, position history, caches, and search strategy.
 * This makes instances safe to run concurrently in tournament play.
 *
 * For production use, [TaratiAI.instance] provides the app-wide singleton.
 * Tests and tournaments create independent instances via [TaratiAI()].
 */
class TaratiAI : IAIEngine {
    // ✅ Migrado: AtomicReference → atomic()
    private val globalConfigRef: AtomicRef<EvaluationConfig> = atomic(EvaluationConfig())
    val evalConfig: EvaluationConfig get() = globalConfigRef.value

    // ✅ Migrado: ConcurrentHashMap → MutableMap
    // No necesita Mutex porque el motor de IA se ejecuta en un solo thread dedicado
    override val positionHistory: MutableMap<String, Int> = mutableMapOf()

    // Componentes — cada instancia posee su propio estado de búsqueda
    private val cache = HybridEvaluationCache(positionHistory = positionHistory)
    private val boardEvaluator = BoardEvaluator()
    private val moveEvaluator = MoveEvaluator(cache)
    private val transpositionTable = TranspositionTable()
    private val aiStrategy: IAIStrategy =
        MinimaxStrategy(
            boardEvaluator = boardEvaluator,
            moveEvaluator = moveEvaluator,
            transpositionTable = transpositionTable,
            cache = cache,
            positionHistory = positionHistory,
            evalConfig = ::evalConfig,
        )

    // ==================== API Pública ====================

    override val name: String get() = "Engine Standard"

    override fun getNextMove(gameState: GameState): MoveEval {
        val config = evalConfig
        if (config.randomMoveChance > 0.0 && Random.nextDouble() < config.randomMoveChance) {
            val randomMove = gameState.allMovesForTurn().randomOrNull()
            return MoveEval(move = randomMove, score = 0.0)
        }
        return getNextMove(gameState, config.difficulty)
    }

    override fun clearHistory() {
        transpositionTable.clear()
        positionHistory.clear()
        cache.clear()
        moveEvaluator.clearHeuristics()
    }

    /**
     * Updates the active EvaluationConfig. The transposition table is cleared
     * when difficulty changes because cached entries are depth-tagged: a shallow
     * entry from a lower difficulty would be incorrectly reused as a valid deep
     * result for a higher difficulty, corrupting the search.
     */
    override fun setConfig(config: EvaluationConfig) {
        // ✅ Migrado: .get() → .value, .set() → .value =
        val prevDifficulty = globalConfigRef.value.difficulty
        globalConfigRef.value = config
        if (config.difficulty != prevDifficulty) {
            transpositionTable.clear()
        }
    }

    override fun getDiagnostics(): AIDiagnostics {
        val (nodes, cuts, hits) = (aiStrategy as? MinimaxStrategy)?.getStats()
            ?: Triple(0L, 0, 0)
        return AIDiagnostics(
            cacheStats = cache.getStats(),
            positionHistorySize = positionHistory.size,
            transpositionTableSize = transpositionTable.size(),
            nodesEvaluated = nodes,
            cutoffs = cuts,
            cacheHits = hits,
        )
    }

    override fun putState(
        gameState: GameState,
        moveBy: CobColor,
    ): CobColor? {
        val count = putState(gameState)
        return if (count >= 3) moveBy else null
    }

    override fun removeState(gameState: GameState) {
        val hash = gameState.hashBoard()
        val count = positionHistory[hash] ?: 0
        when (count) {
            0 -> return
            1 -> positionHistory.remove(hash)
            else -> positionHistory[hash] = count - 1
        }
    }

    // ================ Funciones auxiliares ================

    fun putState(gameState: GameState): Int {
        val hash = gameState.hashBoard()
        val count = (positionHistory[hash] ?: 0) + 1
        positionHistory[hash] = count
        return count
    }

    fun getRepetitionCount(gameState: GameState): Int = positionHistory[gameState.hashBoard()] ?: 0

    fun getNextMove(
        gameState: GameState,
        difficulty: Difficulty,
    ): MoveEval = aiStrategy.getNextMove(gameState, difficulty)

    companion object {
        /**
         * App-wide singleton — used by production code (Koin, GameEvents, GameEffects,
         * AIViewModel, GameState). Tests and tournaments that need isolation should
         * create independent instances via [TaratiAI()].
         */
        val instance: TaratiAI = TaratiAI()
    }
}