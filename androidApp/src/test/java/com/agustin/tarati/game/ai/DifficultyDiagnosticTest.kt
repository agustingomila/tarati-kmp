package com.agustin.tarati.game.ai

import com.agustin.tarati.core.domain.ai.cache.HybridEvaluationCache
import com.agustin.tarati.core.domain.ai.engine.BoardEvaluator
import com.agustin.tarati.core.domain.ai.engine.MoveEvaluator
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.ai.evaluator.MoveEval
import com.agustin.tarati.core.domain.ai.services.Difficulty
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.B5
import com.agustin.tarati.core.domain.game.board.GameBoard.B6
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C10
import com.agustin.tarati.core.domain.game.board.GameBoard.C11
import com.agustin.tarati.core.domain.game.board.GameBoard.C12
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C4
import com.agustin.tarati.core.domain.game.board.GameBoard.C5
import com.agustin.tarati.core.domain.game.board.GameBoard.C6
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.board.GameBoard.C9
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D2
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.board.GameBoard.adjacencyMap
import com.agustin.tarati.core.domain.game.board.GameBoard.centerVertices
import com.agustin.tarati.core.domain.game.board.GameBoard.homeBases
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.game.ai.tournament.engine.base.standardEngine
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DifficultyDiagnosticTest {
    private val evalConfig: EvaluationConfig by lazy { EvaluationConfig(Difficulty.DEFAULT) }
    private val boardEvaluator: BoardEvaluator by lazy { BoardEvaluator() }

    private val engine by lazy { standardEngine }
    private val evaluator by lazy { MoveEvaluator(HybridEvaluationCache()) }

    @Before
    fun setup() {
        clearAIHistory()
    }

    fun setEvaluationConfig(config: EvaluationConfig) {
        engine.setConfig(config)
    }

    fun clearAIHistory() {
        engine.clearHistory()
    }

    fun recordRealMove(
        gameState: GameState,
        currentPlayer: CobColor,
    ) {
        engine.putState(gameState, currentPlayer)
    }

    fun getNextBestMove(
        gameState: GameState,
        difficulty: Difficulty,
    ): MoveEval {
        engine.setConfig(EvaluationConfig.getByDifficulty(difficulty))
        return runBlocking { engine.getNextMove(gameState) }
    }

    /**
     * Test principal: reproduce la secuencia completa y detecta dónde la IA toma malas decisiones
     */
    @Test
    fun diagnoseChampionLosingSequence() {
        setEvaluationConfig(EvaluationConfig.CHAMPION)

        println("=" * 80)
        println("DIAGNOSTIC: CHAMPION Losing Sequence Analysis")
        println("=" * 80)

        val championLosingMoves =
            listOf(
                Move(C2 to C3), // 1. Blancas
                Move(C7 to C6), // 1. Negras
                Move(C1 to B1), // 2. Blancas
                Move(C8 to B4), // 2. Negras - CRITICAL MOVE
                Move(D2 to C2), // 3. Blancas
                Move(D3 to C7), // 3. Negras
                Move(D1 to C1), // 4. Blancas
                Move(B4 to A1), // 4. Negras - CRITICAL MOVE
                Move(C3 to B2), // 5. Blancas
                Move(C6 to B3), // 5. Negras
                Move(C2 to C3), // 6. Blancas
                Move(A1 to B6), // 6. Negras
                Move(B2 to A1), // 7. Blancas - CAPTURE CENTER
                Move(D4 to C8), // 7. Negras
                Move(A1 to B4), // 8. Blancas - MIT
            )

        var gameState = initialGameState()
        var moveNumber = 1
        var criticalMovesFound = 0

        for (i in championLosingMoves.indices) {
            val move = championLosingMoves[i]
            val currentPlayer = gameState.currentTurn
            val isAIMove = currentPlayer == BLACK

            println("\n" + "-" * 80)
            println("Move $moveNumber: ${currentPlayer.name} (${move.from} -> ${move.to})")
            println("-" * 80)

            if (!GameBoard.isValidMove(gameState, move)) {
                println("ERROR: Invalid move in sequence!")
                printBoardState(gameState)
                return
            }

            if (isAIMove) {
                val divergence = analyzeAIDecision(gameState, move)
                if (divergence) criticalMovesFound++
            }

            // Apply move
            gameState = gameState.applyMove(move)
            gameState = gameState.copy(currentTurn = currentPlayer.opponent)
            recordRealMove(gameState, currentPlayer)

            printBoardState(gameState)
            printEvaluation(gameState)

            if (gameState.isGameOver(engine.positionHistory)) {
                println("\nGAME OVER")
                val winner = gameState.getWinner(engine.positionHistory)
                println("Winner: ${winner?.name ?: "DRAW"}")
                break
            }

            if (currentPlayer == BLACK) moveNumber++
        }

        println("\n" + "=" * 80)
        println("DIAGNOSIS COMPLETE")
        println("Critical divergences found: $criticalMovesFound")
        println("=" * 80)
    }

    /**
     * Análisis profundo del movimiento crítico #2 de Negras: C8 -> B4
     */
    @Test
    fun analyzeCriticalMove2() {
        println("=" * 80)
        println("DEEP ANALYSIS: Move 2 Black (C8 -> B4)")
        println("=" * 80)

        val gameState = buildStateBeforeMove2Black()

        println("\nBoard state before C8 -> B4:")
        printDetailedState(gameState)

        // Analizar con diferentes profundidades
        println("\n" + "-" * 80)
        println("DEPTH ANALYSIS:")
        println("-" * 80)

        for (depth in 2..8 step 2) {
            val tempDifficulty = Difficulty.entries.firstOrNull { it.depth == depth } ?: Difficulty.CHAMPION
            val result = getNextBestMove(gameState, tempDifficulty)

            println("\nDepth $depth:")
            println("  Best move: ${result.move?.from} -> ${result.move?.to}")
            println("  Score: ${result.score}")

            if (result.move?.from == C8 && result.move?.to == B4) {
                println("  Status: AI chose the losing move")
            } else {
                println("  Status: AI found different move")
            }
        }

        // Evaluar todas las alternativas
        println("\n" + "-" * 80)
        println("ALL POSSIBLE MOVES EVALUATION:")
        println("-" * 80)

        val allMoves = gameState.allMovesForTurn()
        val evaluations =
            allMoves
                .map { move ->
                    val newState =
                        gameState
                            .applyMove(move)
                            .copy(currentTurn = gameState.currentTurn.opponent)

                    val eval = boardEvaluator.evaluate(newState, evalConfig)
                    val whiteResponse = getNextBestMove(newState, Difficulty.CHAMPION)

                    MoveEvaluation(
                        move = move,
                        immediateEval = eval,
                        whiteResponseMove = whiteResponse.move,
                        whiteResponseScore = whiteResponse.score,
                    )
                }.sortedByDescending { it.immediateEval }

        evaluations.forEachIndexed { index, eval ->
            val marker = if (eval.move.from == C8 && eval.move.to == B4) ">>>" else "   "
            println("$marker ${index + 1}. ${eval.move.from} -> ${eval.move.to}")
            println("       Immediate eval: ${eval.immediateEval}")
            println("       White response: ${eval.whiteResponseMove?.from} -> ${eval.whiteResponseMove?.to}")
            println("       After response: ${eval.whiteResponseScore}")
        }
    }

    /**
     * Análisis profundo del movimiento crítico #4 de Negras: B4 -> A1
     */
    @Test
    fun analyzeCriticalMove4() {
        println("=" * 80)
        println("DEEP ANALYSIS: Move 4 Black (B4 -> A1)")
        println("=" * 80)

        val gameState = buildStateBeforeMove4Black()

        println("\nBoard state before B4 -> A1:")
        printDetailedState(gameState)

        // Evaluar todas las alternativas con simulación de respuesta
        println("\n" + "-" * 80)
        println("MOVE COMPARISON WITH WHITE RESPONSES:")
        println("-" * 80)

        val allMoves = gameState.allMovesForTurn()

        allMoves.forEach { move ->
            println("\n${move.from} -> ${move.to}:")

            val afterMove =
                gameState
                    .applyMove(move)
                    .copy(currentTurn = gameState.currentTurn.opponent)

            printMoveDetails(gameState, move)

            val evalAfterMove = boardEvaluator.evaluate(afterMove, evalConfig)
            println("  Eval after move: $evalAfterMove")

            // Simular respuesta de blancas
            val whiteResponse = getNextBestMove(afterMove, Difficulty.CHAMPION)
            if (whiteResponse.move != null) {
                println("  White responds: ${whiteResponse.move?.from} -> ${whiteResponse.move?.to}")

                val afterWhiteResponse =
                    afterMove
                        .applyMove(
                            whiteResponse.move!!,
                        ).copy(currentTurn = afterMove.currentTurn.opponent)

                val finalEval = boardEvaluator.evaluate(afterWhiteResponse, evalConfig)
                println("  Eval after response: $finalEval")
                println("  Net change: ${finalEval - evalAfterMove}")

                if (afterWhiteResponse.isGameOver(engine.positionHistory)) {
                    val winner = afterWhiteResponse.getWinner(engine.positionHistory)
                    println("  WARNING: Game ends! Winner: ${winner?.name}")
                }
            }
        }
    }

    /**
     * Compara los componentes de evaluación para diferentes movimientos
     */
    @Test
    fun compareEvaluationComponents() {
        println("=" * 80)
        println("EVALUATION COMPONENTS BREAKDOWN")
        println("=" * 80)

        val gameState = buildStateBeforeMove2Black()

        val testMoves =
            listOf(
                Move(C8 to B4), // El movimiento que hace
                Move(C8 to C7), // Alternativa 1
                Move(D4 to C8), // Alternativa 2
                Move(C6 to B3), // Alternativa 3
            )

        testMoves.forEach { move ->
            println("\n" + "=" * 60)
            println("Move: ${move.from} -> ${move.to}")
            println("=" * 60)

            val newState =
                gameState
                    .applyMove(move)
                    .copy(currentTurn = gameState.currentTurn.opponent)

            val config = EvaluationConfig.CHAMPION
            val metrics = boardEvaluator.evaluateMetrics(newState, config)

            println("\nMaterial:")
            println("  White: ${metrics.material.white} | Black: ${metrics.material.black}")
            println("  Difference: ${metrics.material.difference}")

            println("\nCenter Control:")
            println("  White: ${metrics.centerControl.white} | Black: ${metrics.centerControl.black}")
            val centerScore = metrics.centerControl.difference * config.controlCenterScore
            println("  Score contribution: $centerScore")

            println("\nMobility:")
            println("  White: ${metrics.mobility.white} | Black: ${metrics.mobility.black}")
            val mobilityScore = metrics.mobility.difference * config.mobilityScore
            println("  Score contribution: $mobilityScore")

            println("\nOpponent Base Pressure:")
            println("  White: ${metrics.opponentPressure.white} | Black: ${metrics.opponentPressure.black}")
            val pressureScore = metrics.opponentPressure.difference * config.opponentDomesticPressureScore
            println("  Score contribution: $pressureScore")

            println("\nHome Base Control:")
            println("  White: ${metrics.homeControl.white} | Black: ${metrics.homeControl.black}")
            val homeScore = metrics.homeControl.difference * config.domesticControlScore
            println("  Score contribution: $homeScore")

            println("\nUpgrade Opportunities:")
            println("  White: ${metrics.upgradeOpportunities.white} | Black: ${metrics.upgradeOpportunities.black}")
            val upgradeScore = metrics.upgradeOpportunities.difference * config.upgradeScore
            println("  Score contribution: $upgradeScore")

            val totalEval = boardEvaluator.evaluate(newState, config)
            println("\nTOTAL EVALUATION: $totalEval")
        }
    }

    // ==================== Helper Functions ====================

    private fun analyzeAIDecision(
        gameState: GameState,
        actualMove: Move,
    ): Boolean {
        println("\nAI DECISION ANALYSIS:")

        val allMoves = gameState.allMovesForTurn()
        println("  Possible moves: ${allMoves.size}")

        val result = getNextBestMove(gameState, Difficulty.CHAMPION)
        val aiBestMove = result.move
        val aiScore = result.score

        println("  AI chose: ${aiBestMove?.from} -> ${aiBestMove?.to} (score: $aiScore)")
        println("  Actual was: ${actualMove.from} -> ${actualMove.to}")

        val divergence = aiBestMove?.from != actualMove.from || aiBestMove.to != actualMove.to

        if (divergence) {
            println("  STATUS: DIVERGENCE DETECTED")

            var aiEval = 0.0
            if (aiBestMove != null) {
                val aiNewState =
                    gameState
                        .applyMove(aiBestMove)
                        .copy(currentTurn = gameState.currentTurn.opponent)
                aiEval = boardEvaluator.evaluate(aiNewState, evalConfig)
                println("\n  AI preferred move evaluation:")
                println("    Move: ${aiBestMove.from} -> ${aiBestMove.to}")
                println("    Eval: $aiEval")
                printMoveDetails(gameState, aiBestMove)
            }

            val actualNewState =
                gameState
                    .applyMove(actualMove)
                    .copy(currentTurn = gameState.currentTurn.opponent)
            val actualEval = boardEvaluator.evaluate(actualNewState, evalConfig)
            println("\n  Actual move evaluation:")
            println("    Move: ${actualMove.from} -> ${actualMove.to}")
            println("    Eval: $actualEval")
            printMoveDetails(gameState, actualMove)

            println("\n  Evaluation difference: ${if (aiBestMove != null) aiEval - actualEval else "N/A"}")
        } else {
            println("  STATUS: AI chose expected move")
        }

        return divergence
    }

    private fun printMoveDetails(
        gameState: GameState,
        move: Move,
    ) {
        val newState = gameState.applyMove(move)
        val movedCob = gameState.cobs[move.from] ?: return

        var capturedPieces = 0
        var upgradedCaptured = 0
        for (vertex in adjacencyMap[move.to] ?: emptyList()) {
            val cob = gameState.cobs[vertex]
            if (cob != null && cob.color != movedCob.color) {
                capturedPieces++
                if (cob.isUpgraded) upgradedCaptured++
            }
        }

        val enemyBase = homeBases[movedCob.color.opponent] ?: emptyList()
        val willUpgrade = move.to in enemyBase && !movedCob.isUpgraded

        println("    Captures: $capturedPieces (Upgraded: $upgradedCaptured)")
        println("    Upgrades: ${if (willUpgrade) "YES" else "NO"}")
        println("    Center control: ${if (move.to in centerVertices) "YES" else "NO"}")

        val mobility = newState.copy(currentTurn = movedCob.color).allMovesForTurn().size
        println("    Resulting mobility: $mobility moves")

        if (newState.isGameOver(engine.positionHistory)) {
            println("    WARNING: GAME ENDS - Winner: ${newState.getWinner(engine.positionHistory)?.name}")
        }
    }

    private fun printBoardState(gameState: GameState) {
        println("\nBoard state:")
        val whiteCobs = gameState.cobs.values.count { it.color == WHITE }
        val blackCobs = gameState.cobs.values.count { it.color == BLACK }
        val whiteUpgraded = gameState.cobs.values.count { it.color == WHITE && it.isUpgraded }
        val blackUpgraded = gameState.cobs.values.count { it.color == BLACK && it.isUpgraded }

        println("  White: $whiteCobs pieces ($whiteUpgraded upgraded)")
        println("  Black: $blackCobs pieces ($blackUpgraded upgraded)")

        val whitesInCenter =
            gameState.cobs.entries.count {
                it.key in centerVertices && it.value.color == WHITE
            }
        val blacksInCenter =
            gameState.cobs.entries.count {
                it.key in centerVertices && it.value.color == BLACK
            }
        println("  Center: W:$whitesInCenter B:$blacksInCenter")
    }

    private fun printDetailedState(gameState: GameState) {
        println("Current turn: ${gameState.currentTurn.name}")
        println("Pieces on board:")
        gameState.cobs.entries
            .sortedBy { it.key.zone.name }
            .sortedBy { it.key.position }
            .forEach { (pos, cob) ->
                val upgraded = if (cob.isUpgraded) "[U]" else "   "
                val color = if (cob.color == WHITE) "W" else "B"
                println("  $pos: $color $upgraded")
            }
        printBoardState(gameState)
    }

    private fun printEvaluation(gameState: GameState) {
        val eval = boardEvaluator.evaluate(gameState, evalConfig)
        val quickEval = evaluator.quickEvaluate(gameState, evalConfig)
        println("\nFull evaluation: $eval")
        println("Quick evaluation: $quickEval")

        val advantage =
            when {
                eval > 100 -> "White significant advantage"
                eval > 50 -> "White moderate advantage"
                eval > 10 -> "White slight advantage"
                eval > -10 -> "Balanced position"
                eval > -50 -> "Black slight advantage"
                eval > -100 -> "Black moderate advantage"
                else -> "Black significant advantage"
            }
        println(advantage)
    }

    private fun buildStateBeforeMove2Black(): GameState {
        var state = initialGameState()

        val setupMoves =
            listOf(
                Move(C2 to C3),
                Move(C7 to C6),
                Move(C1 to B1),
            )

        for (move in setupMoves) {
            val currentPlayer = state.currentTurn
            state = state.applyMove(move)
            state = state.copy(currentTurn = currentPlayer.opponent)
            recordRealMove(state, currentPlayer)
        }

        return state
    }

    private fun buildStateBeforeMove4Black(): GameState {
        var state = initialGameState()

        val setupMoves =
            listOf(
                Move(C2 to C3),
                Move(C7 to C6),
                Move(C1 to B1),
                Move(C8 to B4),
                Move(D2 to C2),
                Move(D3 to C7),
                Move(D1 to C1),
            )

        for (move in setupMoves) {
            val currentPlayer = state.currentTurn
            state = state.applyMove(move)
            state = state.copy(currentTurn = currentPlayer.opponent)
            recordRealMove(state, currentPlayer)
        }

        return state
    }

    // ==================== Data Classes ====================

    private data class MoveEvaluation(
        val move: Move,
        val immediateEval: Double,
        val whiteResponseMove: Move?,
        val whiteResponseScore: Double,
    )

    // ==================== Extensions ====================

    private operator fun String.times(n: Int): String = this.repeat(n)

    /**
     * Test para analizar la partida donde el nivel MEDIUM pierde
     */
    @Test
    fun diagnoseMediumLosingSequence() {
        setEvaluationConfig(EvaluationConfig.MEDIUM)

        println("=" * 80)
        println("DIAGNOSTIC: MEDIUM Losing Sequence Analysis")
        println("=" * 80)

        val mediumLosingMoves =
            listOf(
                Move(C2 to C3), // 1. Blancas
                Move(C7 to C6), // 1. Negras
                Move(D2 to C2), // 2. Blancas
                Move(C8 to C9), // 2. Negras - CRITICAL MOVE
                Move(C1 to B1), // 3. Blancas
                Move(D3 to C7), // 3. Negras
                Move(D1 to C1), // 4. Blancas
                Move(C6 to C5), // 4. Negras
                Move(C3 to C4), // 5. Blancas
                Move(C7 to C6), // 5. Negras
                Move(B1 to B6), // 6. Blancas
                Move(C9 to B5), // 6. Negras - CRITICAL MOVE
                Move(C1 to C12), // 7. Blancas
                Move(D4 to C8), // 7. Negras
                Move(C12 to C11), // 8. Blancas
                Move(C8 to B4), // 8. Negras
                Move(C11 to C10), // 9. Blancas
                Move(C6 to B3), // 9. Negras
                Move(B6 to A1), // 10. Blancas - CRITICAL MOVE
            )

        var gameState = initialGameState()
        var moveNumber = 1
        var criticalMovesFound = 0

        for (i in mediumLosingMoves.indices) {
            val move = mediumLosingMoves[i]
            val currentPlayer = gameState.currentTurn
            val isAIMove = currentPlayer == BLACK

            println("\n" + "-" * 80)
            println("Move $moveNumber: ${currentPlayer.name} (${move.from} -> ${move.to})")
            println("-" * 80)

            if (!GameBoard.isValidMove(gameState, move)) {
                println("ERROR: Invalid move in sequence!")
                printBoardState(gameState)
                return
            }

            if (isAIMove) {
                val divergence = analyzeAIDecision(gameState, move)
                if (divergence) criticalMovesFound++
            }

            // Apply move
            gameState = gameState.applyMove(move)
            gameState = gameState.copy(currentTurn = currentPlayer.opponent)
            recordRealMove(gameState, currentPlayer)

            printBoardState(gameState)
            printEvaluation(gameState)

            if (gameState.isGameOver(engine.positionHistory)) {
                println("\nGAME OVER")
                val winner = gameState.getWinner(engine.positionHistory)
                println("Winner: ${winner?.name ?: "DRAW"}")
                break
            }

            if (currentPlayer == BLACK) moveNumber++
        }

        println("\n" + "=" * 80)
        println("MEDIUM DIAGNOSIS COMPLETE")
        println("Critical divergences found: $criticalMovesFound")
        println("=" * 80)
    }

    @Test
    fun diagnoseRulesErrorSequence() {
        println("=" * 80)
        println("DIAGNOSTIC: Rules Error Sequence Analysis")
        println("=" * 80)

        val errorInRulesMoves =
            listOf(
                Move(C1 to C12), // 1. Blancas
                Move(C7 to C6), // 1. Negras
                Move(C2 to C3), // 2. Blancas
                Move(C8 to B4), // 2. Negras
                Move(D2 to C2), // 3. Blancas
                Move(C6 to C5), // 3. Negras
                Move(C3 to C4), // 4. Blancas
                Move(B4 to B3), // 4. Negras
                Move(C2 to B1), // 5. Blancas
                Move(B3 to B2), // 5. Negras
                Move(C12 to B6), // 6. Blancas
                Move(C4 to C3), // 6. Negras
                Move(B1 to A1), // 7. Blancas
                Move(D3 to C7), // 7. Negras
                Move(A1 to B4), // 8. Blancas
                Move(D4 to C8), // 8. Negras
                Move(B6 to A1), // 9. Blancas
                Move(C5 to C4), // 9. Negras
                Move(A1 to B5), // 10. Blancas
                Move(B2 to B1), // 10. Negras
                Move(B5 to C9), // 11. Blancas
                Move(B1 to C1), // 11. Negras
                Move(C8 to D4), // 12. Blancas
                Move(C7 to C8), // 12. Negras - RULES ERROR MOVE
            )

        var gameState = initialGameState()
        var moveNumber = 1

        println("\nSPECIAL ANALYSIS: Rules verification on move 24 (C7 -> C8)")
        println("Expected captures: D4, C9, B4")
        println("=" * 60)

        for (i in errorInRulesMoves.indices) {
            val move = errorInRulesMoves[i]
            val currentPlayer = gameState.currentTurn

            println("\nMove ${i + 1}: ${currentPlayer.name} (${move.from} -> ${move.to})")

            if (!GameBoard.isValidMove(gameState, move)) {
                println("ERROR: Invalid move in sequence!")
                printBoardState(gameState)
                return
            }

            // Special analysis for the rules error move
            if (i == 23) { // Move 24: C7 -> C8 (índice 23 en la lista)
                println("\n" + "!" * 60)
                println("RULES VERIFICATION - Move 24: C7 -> C8")
                println("!" * 60)

                val beforeState = gameState
                println("Board state BEFORE move:")
                printDetailedState(beforeState)

                // Analyze expected captures
                val expectedCaptures = listOf("D4", "C9", "B4")
                println("\nExpected captures (flips): $expectedCaptures")

                val adjacentVertices = adjacencyMap[C8] ?: emptyList()
                println("Adjacent vertices to C8: $adjacentVertices")

                adjacentVertices.forEach { vertex ->
                    val cob = beforeState.cobs[vertex]
                    if (cob != null && cob.color == WHITE) {
                        println("Should flip $vertex (${cob.color.name} ${if (cob.isUpgraded) "Rok" else "Cob"})")
                    }
                }
            }

            // Apply move
            val previousState = gameState
            gameState = gameState.applyMove(move)
            gameState = gameState.copy(currentTurn = currentPlayer.opponent)
            recordRealMove(gameState, currentPlayer)

            // Check captures after move 24
            if (i == 23) {
                println("\nBoard state AFTER move:")
                printDetailedState(gameState)

                val expectedCaptures = listOf(D4, C9, B4)

                println("\nCAPTURE RESULTS:")

                // Check for flipped pieces (changed color)
                val flippedPieces =
                    expectedCaptures.filter { vertex ->
                        val beforeCob = previousState.cobs[vertex]
                        val afterCob = gameState.cobs[vertex]
                        beforeCob != null && afterCob != null && beforeCob.color != afterCob.color
                    }

                val notFlippedPieces =
                    expectedCaptures.filter { vertex ->
                        val beforeCob = previousState.cobs[vertex]
                        val afterCob = gameState.cobs[vertex]
                        beforeCob != null && (afterCob == null || beforeCob.color == afterCob.color)
                    }

                if (flippedPieces.isNotEmpty()) {
                    println("SUCCESS: Pieces flipped (captured and converted): $flippedPieces")
                    flippedPieces.forEach { vertex ->
                        val beforeCob = previousState.cobs[vertex]
                        val afterCob = gameState.cobs[vertex]!!
                        println("  $vertex: ${beforeCob?.color}->${afterCob.color} (${if (afterCob.isUpgraded) "Rok" else "Cob"})")
                    }
                }

                if (notFlippedPieces.isNotEmpty()) {
                    println("ERROR: These pieces should be flipped but aren't: $notFlippedPieces")
                    notFlippedPieces.forEach { vertex ->
                        val beforeCob = previousState.cobs[vertex]
                        val afterCob = gameState.cobs[vertex]
                        println("  $vertex: Before=${beforeCob?.color}->After=${afterCob?.color}")
                    }
                }

                // Verify the moving piece
                val movedPiece = gameState.cobs[C8]
                println("Piece at C8 after move: ${movedPiece?.color?.name} ${if (movedPiece?.isUpgraded == true) "Rok" else "Cob"}")

                // Count the results
                val blackPieces = gameState.cobs.values.count { it.color == BLACK }
                val whitePieces = gameState.cobs.values.count { it.color == WHITE }
                println("Piece count after move - Black: $blackPieces, White: $whitePieces")

                if (flippedPieces.size == expectedCaptures.size) {
                    println("SUCCESS: All expected pieces were flipped!")
                } else {
                    println("ISSUE: Only ${flippedPieces.size} out of ${expectedCaptures.size} pieces were flipped")
                }
            }

            if (gameState.isGameOver(engine.positionHistory)) {
                println("\nGAME OVER at move ${i + 1}")
                val winner = gameState.getWinner(engine.positionHistory)
                println("Winner: ${winner?.name ?: "DRAW"}")

                val blackPieces = gameState.cobs.values.count { it.color == BLACK }
                val whitePieces = gameState.cobs.values.count { it.color == WHITE }
                println("Final piece count - Black: $blackPieces, White: $whitePieces")

                if (winner == BLACK && blackPieces > whitePieces) {
                    println("Black wins as expected with piece advantage")
                } else {
                    println("UNEXPECTED RESULT")
                }
                break
            }

            if (currentPlayer == BLACK) moveNumber++
        }

        println("\n" + "=" * 80)
        println("RULES VERIFICATION COMPLETE")
        println("Final board state:")
        printDetailedState(gameState)

        // Final analysis
        val blackPieces = gameState.cobs.values.count { it.color == BLACK }
        val whitePieces = gameState.cobs.values.count { it.color == WHITE }
        println("Final piece count - Black: $blackPieces, White: $whitePieces")
        println("Black advantage: ${blackPieces - whitePieces} pieces")

        if (blackPieces > whitePieces + 2) {
            println("SUCCESS: Triple capture working correctly!")
        } else {
            println("ISSUE: Capture logic may still have problems")
        }
        println("=" * 80)
    }
}