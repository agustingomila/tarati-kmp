package com.agustin.tarati.game.ai.tournament.manager

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.evaluator.EvaluationConfig
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.game.ai.tournament.engine.base.cloneForMatch
import com.agustin.tarati.game.ai.tournament.helpers.EnginePerformance
import com.agustin.tarati.game.ai.tournament.helpers.PerformanceMetrics
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.log10
import kotlin.math.roundToInt

class TournamentRunner(
    private val parallelMatches: Int = Runtime.getRuntime().availableProcessors().coerceAtLeast(2),
) {

    /**
     * Ejecuta un match entre dos engines con configuraciones opcionales
     */
    fun runEngineMatch(
        engineA: IAIEngine,
        engineB: IAIEngine,
        configA: EvaluationConfig? = null,
        configB: EvaluationConfig? = null,
        tournamentConfig: TournamentConfig = TournamentConfig(),
        logInfo: (String) -> Unit,
    ): TournamentResult {
        var winsA = 0
        var winsB = 0
        var draws = 0
        var totalMoves = 0
        var movesWhenAWins = 0
        var movesWhenBWins = 0
        var aWinCount = 0
        var bWinCount = 0
        var timeoutsA = 0
        var timeoutsB = 0
        val performanceMetrics = mutableListOf<PerformanceMetrics>()

        logInfo("\n${"=".repeat(70)}")
        logInfo("ENGINE MATCH: ${engineA.name} vs ${engineB.name}")
        logInfo("Games: ${tournamentConfig.gamesPerMatch}")
        logInfo("Max moves per game: ${tournamentConfig.maxMovesPerGame}")
        logInfo("=".repeat(70))

        repeat(tournamentConfig.gamesPerMatch) { gameIndex ->
            if (tournamentConfig.showProgress && gameIndex % 10 == 0) {
                print(".")
                if (gameIndex % 50 == 0 && gameIndex > 0) {
                    logInfo(" [$gameIndex/${tournamentConfig.gamesPerMatch}]")
                }
            }

            val (whiteEngine, blackEngine, whiteConfig, blackConfig) =
                if (tournamentConfig.alternateColors && gameIndex % 2 == 0) {
                    Quadruple(engineA, engineB, configA, configB)
                } else {
                    Quadruple(engineB, engineA, configB, configA)
                }

            val gameResult =
                playGame(
                    whiteEngine = whiteEngine,
                    blackEngine = blackEngine,
                    whiteConfig = whiteConfig,
                    blackConfig = blackConfig,
                    tournamentConfig = tournamentConfig,
                    gameNumber = gameIndex + 1,
                    logInfo = logInfo,
                )

            when (gameResult.winner) {
                WHITE if whiteEngine == engineA -> {
                    winsA++
                    movesWhenAWins += gameResult.moves
                    aWinCount++
                }

                WHITE if whiteEngine == engineB -> {
                    winsB++
                    movesWhenBWins += gameResult.moves
                    bWinCount++
                }

                BLACK if blackEngine == engineA -> {
                    winsA++
                    movesWhenAWins += gameResult.moves
                    aWinCount++
                }

                BLACK if blackEngine == engineB -> {
                    winsB++
                    movesWhenBWins += gameResult.moves
                    bWinCount++
                }

                else -> draws++
            }

            totalMoves += gameResult.moves
            gameResult.performanceMetrics?.let { performanceMetrics.add(it) }

            if (gameResult.timeout) {
                when {
                    (gameResult.winner == WHITE && whiteEngine == engineA) ||
                            (gameResult.winner == BLACK && blackEngine == engineA) -> timeoutsB++

                    (gameResult.winner == WHITE && whiteEngine == engineB) ||
                            (gameResult.winner == BLACK && blackEngine == engineB) -> timeoutsA++
                }
            }
        }

        if (tournamentConfig.showProgress) {
            logInfo(" [${tournamentConfig.gamesPerMatch}/${tournamentConfig.gamesPerMatch}]")
        }

        val avgMovesA = if (aWinCount > 0) movesWhenAWins.toDouble() / aWinCount else 0.0
        val avgMovesB = if (bWinCount > 0) movesWhenBWins.toDouble() / bWinCount else 0.0
        val avgPerformance =
            if (performanceMetrics.isNotEmpty()) {
                PerformanceMetrics(
                    averageCacheHitRate = performanceMetrics.map { it.averageCacheHitRate }.average(),
                    averageNodesPerMove = performanceMetrics.map { it.averageNodesPerMove }.average(),
                    averageCutoffsPerMove = performanceMetrics.map { it.averageCutoffsPerMove }.average(),
                    averageMoveTimeMs = performanceMetrics.map { it.averageMoveTimeMs }.average(),
                )
            } else {
                null
            }

        return TournamentResult(
            engineA = engineA,
            engineB = engineB,
            configA = configA,
            configB = configB,
            winsA = winsA,
            winsB = winsB,
            draws = draws,
            totalGames = tournamentConfig.gamesPerMatch,
            averageMoves = totalMoves.toDouble() / tournamentConfig.gamesPerMatch,
            averageMovesA = avgMovesA,
            averageMovesB = avgMovesB,
            timeoutsA = timeoutsA,
            timeoutsB = timeoutsB,
            performanceMetrics = avgPerformance,
            logInfo = logInfo,
        )
    }

    // Función para obtener los resultados head-to-head desde fuera
    private val headToHeadResults = ConcurrentHashMap<Pair<String, String>, HeadToHeadResult>()

    fun getHeadToHeadResults(): Map<Pair<String, String>, HeadToHeadResult> = headToHeadResults.toMap()

    /**
     * Round-robin para múltiples engines
     */
    fun runEngineRoundRobin(
        engines: List<IAIEngine>,
        configs: Map<String, EvaluationConfig> = emptyMap(),
        tournamentConfig: TournamentConfig = TournamentConfig(),
        logInfo: (String) -> Unit,
    ): List<EnginePerformance> {
        val results = mutableMapOf<String, EnginePerformance>()

        engines.forEach { engine ->
            val config = configs[engine.name]
            results[engine.name] = EnginePerformance(engine = engine, config = config)
        }

        val totalMatches = engines.size * (engines.size - 1) / 2
        var completedMatches = 0

        logInfo("\n${"=".repeat(70)}")
        logInfo("ENGINE ROUND ROBIN TOURNAMENT")
        logInfo("Competitors: ${engines.size}")
        logInfo("Total matches: $totalMatches")
        logInfo("Games per match: ${tournamentConfig.gamesPerMatch}")
        logInfo("=".repeat(70))

        // Build all match pairs upfront so each can run on its own thread.
        // Each pair clones fresh engine instances so there is no shared mutable state.
        data class MatchPair(
            val nameA: String, val nameB: String,
            val engineA: IAIEngine, val engineB: IAIEngine,
            val configA: EvaluationConfig?, val configB: EvaluationConfig?,
            val index: Int,
        )

        val matchPairs = buildList {
            var idx = 0
            for (i in engines.indices) {
                for (j in i + 1 until engines.size) {
                    idx++
                    val a = engines[i]
                    val b = engines[j]
                    add(
                        MatchPair(
                            nameA = a.name, nameB = b.name,
                            engineA = a.cloneForMatch(), engineB = b.cloneForMatch(),
                            configA = configs[a.name], configB = configs[b.name],
                            index = idx,
                        )
                    )
                }
            }
        }

        // pairByFuture maps each submitted Future back to its MatchPair so results
        // can be logged and accumulated as soon as each match finishes, regardless
        // of submission order. ExecutorCompletionService.take() blocks until the
        // next completed future is available, delivering them in completion order.
        val executor = Executors.newFixedThreadPool(parallelMatches)
        val completion = ExecutorCompletionService<TournamentResult>(executor)
        val pairByFuture = mutableMapOf<Future<TournamentResult>, MatchPair>()

        for (pair in matchPairs) {
            val future = completion.submit {
                runEngineMatch(
                    engineA = pair.engineA,
                    engineB = pair.engineB,
                    configA = pair.configA,
                    configB = pair.configB,
                    tournamentConfig = tournamentConfig,
                    logInfo = {},
                )
            }
            pairByFuture[future] = pair
        }

        executor.shutdown()

        // Collect and log each result as soon as its match completes.
        repeat(matchPairs.size) {
            val future = completion.take()
            val matchResult = future.get()
            val pair = pairByFuture.getValue(future)
            completedMatches++
            logInfo("\n[Match ${pair.index}/$totalMatches] ${pair.nameA} vs ${pair.nameB}")
            logInfo("Result: ${pair.nameA} ${matchResult.winsA}-${matchResult.winsB} ${pair.nameB} (${matchResult.draws} draws)")
            logInfo("Elo difference: ${matchResult.eloDifference}")

            synchronized(results) {
                results[pair.nameA] = results[pair.nameA]!!.withResults(
                    additionalWins = matchResult.winsA,
                    additionalLosses = matchResult.winsB,
                    additionalDraws = matchResult.draws,
                    additionalMoves = (matchResult.averageMovesA * matchResult.winsA).toInt(),
                    additionalTimeouts = matchResult.timeoutsA,
                    additionalMetrics = matchResult.performanceMetrics,
                )
                results[pair.nameB] = results[pair.nameB]!!.withResults(
                    additionalWins = matchResult.winsB,
                    additionalLosses = matchResult.winsA,
                    additionalDraws = matchResult.draws,
                    additionalMoves = (matchResult.averageMovesB * matchResult.winsB).toInt(),
                    additionalTimeouts = matchResult.timeoutsB,
                    additionalMetrics = matchResult.performanceMetrics,
                )
            }

            headToHeadResults[Pair(pair.nameA, pair.nameB)] = HeadToHeadResult(
                engineA = pair.nameA,
                engineB = pair.nameB,
                winsA = matchResult.winsA,
                winsB = matchResult.winsB,
                draws = matchResult.draws,
                averageMoves = matchResult.averageMoves,
                timeoutsA = matchResult.timeoutsA,
                timeoutsB = matchResult.timeoutsB,
            )
        }

        val sortedResults = results.values.sortedByDescending { it.score }
        printEngineLeaderboard(sortedResults, logInfo)

        // Imprimir tabla de enfrentamientos directos
        printHeadToHeadResults(headToHeadResults, engines, logInfo)

        return sortedResults
    }

    /**
     * Juega una partida individual con métricas de performance
     */
    private fun playGame(
        whiteEngine: IAIEngine,
        blackEngine: IAIEngine,
        whiteConfig: EvaluationConfig?,
        blackConfig: EvaluationConfig?,
        tournamentConfig: TournamentConfig,
        gameNumber: Int = 1,
        logInfo: (String) -> Unit,
    ): TestGameResult {
        whiteEngine.clearHistory()
        blackEngine.clearHistory()
        val gameHistory = mutableMapOf<String, Int>()

        var gameState = initialGameState()
        var moves = 0
        val moveTimes = mutableListOf<Long>()
        val cacheStats = mutableListOf<Double>()
        val nodesEvaluated = mutableListOf<Int>()
        val cutoffs = mutableListOf<Int>()

        while (moves < tournamentConfig.maxMovesPerGame && !gameState.isGameOver(gameHistory)) {
            val currentEngine =
                when (gameState.currentTurn) {
                    WHITE -> whiteEngine
                    else -> blackEngine
                }
            val currentConfig =
                when (gameState.currentTurn) {
                    WHITE -> whiteConfig
                    else -> blackConfig
                }

            currentConfig?.let { currentEngine.setConfig(it) }

            val startTime = System.currentTimeMillis()
            val result = runBlocking { currentEngine.getNextMove(gameState) }
            val endTime = System.currentTimeMillis()

            if (result.move == null) break

            moveTimes.add(endTime - startTime)

            if (tournamentConfig.collectMetrics) {
                val diagnostics = currentEngine.getDiagnostics()
                diagnostics?.let {
                    cacheStats.add(it.cacheStats.hitRate)
                    nodesEvaluated.add(it.nodesEvaluated.toInt())
                    cutoffs.add(it.cutoffs)
                }
            }

            if (tournamentConfig.verbose && moves < 10) {
                logInfo("  Move $moves: ${gameState.currentTurn} ${result.move?.from}->${result.move?.to}")
            }

            val newState = gameState.applyMove(result.move!!)
            val nextState = newState.copy(currentTurn = gameState.currentTurn.opponent)

            val hash = nextState.hashBoard()
            val count = (gameHistory[hash] ?: 0) + 1
            gameHistory[hash] = count

            if (count >= 3) {
                val winner = nextState.currentTurn.opponent
                if (tournamentConfig.verbose) {
                    logInfo("  Game $gameNumber ended by triple repetition at move $moves")
                    logInfo("  Winner: $winner")
                }
                return TestGameResult(
                    winner = winner,
                    moves = moves + 1,
                    timeout = false,
                    performanceMetrics = createPerformanceMetrics(moveTimes, cacheStats, nodesEvaluated, cutoffs),
                )
            }

            gameState = nextState
            moves++
        }

        val winner = gameState.getWinner(gameHistory)
        val timeout = moves >= tournamentConfig.maxMovesPerGame

        if (tournamentConfig.verbose) {
            logInfo("  Game $gameNumber ended in $moves moves")
            logInfo("  Winner: ${winner ?: "DRAW"} ${if (timeout) "(timeout)" else ""}")
        }

        return TestGameResult(
            winner = winner,
            moves = moves,
            timeout = timeout,
            performanceMetrics = createPerformanceMetrics(moveTimes, cacheStats, nodesEvaluated, cutoffs),
        )
    }

    private fun createPerformanceMetrics(
        moveTimes: List<Long>,
        cacheStats: List<Double>,
        nodesEvaluated: List<Int>,
        cutoffs: List<Int>,
    ): PerformanceMetrics? {
        if (moveTimes.isEmpty()) return null

        return PerformanceMetrics(
            averageCacheHitRate = cacheStats.average(),
            averageNodesPerMove = nodesEvaluated.average(),
            averageCutoffsPerMove = cutoffs.average(),
            averageMoveTimeMs = moveTimes.average(),
        )
    }

    private fun printEngineLeaderboard(
        results: List<EnginePerformance>,
        logInfo: (String) -> Unit,
    ) {
        logInfo("\n${"=".repeat(90)}")
        logInfo("FINAL ENGINE LEADERBOARD")
        logInfo("=".repeat(90))
        logInfo(
            "%-3s %-20s %6s %6s %6s %6s %8s %8s %8s %8s".format(
                "Pos",
                "Engine",
                "Wins",
                "Loss",
                "Draw",
                "Games",
                "Score",
                "Win%",
                "AvgMvs",
                "Elo",
            ),
        )
        logInfo("-".repeat(90))

        results.forEachIndexed { index, perf ->
            val elo = calculateElo(perf.winRate)
            logInfo(
                "%-3d %-20s %6d %6d %6d %6d %8.1f %7.1f%% %8.1f %8d".format(
                    index + 1,
                    perf.engine.name.take(20),
                    perf.wins,
                    perf.losses,
                    perf.draws,
                    perf.totalGames,
                    perf.score,
                    perf.winRate * 100,
                    perf.averageMoves,
                    elo,
                ),
            )
        }
        logInfo("=".repeat(90))

        results.firstOrNull()?.averagePerformance?.let { metrics ->
            logInfo("\nPerformance Metrics Summary:")
            logInfo("  Average cache hit rate: ${"%.1f%%".format(metrics.averageCacheHitRate * 100)}")
            logInfo("  Average nodes per move: ${metrics.averageNodesPerMove.toInt()}")
            logInfo("  Average cutoffs per move: ${metrics.averageCutoffsPerMove.toInt()}")
            logInfo("  Average move time: ${"%.1f".format(metrics.averageMoveTimeMs)}ms")
        }
    }

    /**
     * Imprime una tabla de enfrentamientos directos entre todos los engines
     */
    fun printHeadToHeadResults(
        headToHeadResults: Map<Pair<String, String>, HeadToHeadResult>,
        engines: List<IAIEngine>,
        logInfo: (String) -> Unit,
    ) {
        val engineNames = engines.map { it.name }.sorted()
        val maxNameLength = (engineNames.maxOfOrNull { it.length } ?: 15) + 2

        logInfo("\n${"=".repeat(100)}")
        logInfo("HEAD-TO-HEAD RESULTS MATRIX")
        logInfo("=".repeat(100))

        // Header
        val headerLine1 =
            " ".repeat(maxNameLength) +
                    engineNames.joinToString("") { " %8s".format(it) }
        logInfo(headerLine1)

        val headerLine2 =
            "Engine".padEnd(maxNameLength) +
                    engineNames.joinToString("") { " %8s".format(it.take(6)) } +
                    " %8s %8s".format("Total", "Win%")
        logInfo(headerLine2)
        logInfo("-".repeat(100))

        // Matriz de resultados con colores y alineación
        engineNames.forEach { rowEngine ->
            val row = StringBuilder()
            row.append(rowEngine.padEnd(maxNameLength))

            var totalWins = 0
            var totalGames = 0

            engineNames.forEach { colEngine ->
                if (rowEngine == colEngine) {
                    row.append(" %8s".format("---"))
                } else {
                    val result =
                        headToHeadResults[Pair(rowEngine, colEngine)] ?: headToHeadResults[Pair(colEngine, rowEngine)]

                    if (result != null) {
                        val wins = if (rowEngine == result.engineA) result.winsA else result.winsB
                        val losses = if (rowEngine == result.engineA) result.winsB else result.winsA
                        val games = wins + losses + result.draws

                        totalWins += wins
                        totalGames += games

                        val score = formatScoreWithColor(wins, losses, games)
                        row.append(score)
                    } else {
                        row.append(" %8s".format("-"))
                    }
                }
            }

            val totalWinRate = if (totalGames > 0) "%.1f%%".format(totalWins.toDouble() / totalGames * 100) else "0.0%"
            row.append(" %8d".format(totalWins))
            row.append(" %8s".format(totalWinRate))

            logInfo(row.toString())
        }

        logInfo("-".repeat(100))

        // Leyenda de colores
        logInfo("\nColor Legend: \u001B[32mGreen\u001B[0m = Strong win (>66%) | \u001B[31mRed\u001B[0m = Weak performance (<33%)")

        // Detalles adicionales
        logInfo("\nDETAILED RESULTS:")
        logInfo("-".repeat(90))
        headToHeadResults.entries.sortedBy { it.key.first }.forEach { (pair, result) ->
            val (engineA, engineB) = pair
            val totalGames = result.winsA + result.winsB + result.draws
            val winRateA = if (totalGames > 0) "%.1f%%".format(result.winsA.toDouble() / totalGames * 100) else "0.0%"
            val winRateB = if (totalGames > 0) "%.1f%%".format(result.winsB.toDouble() / totalGames * 100) else "0.0%"

            // Aplicar colores también en los detalles
            val colorA =
                when {
                    result.winsA > result.winsB * 2 -> "\u001B[32m"
                    result.winsB > result.winsA * 2 -> "\u001B[31m"
                    else -> ""
                }
            val colorB =
                when {
                    result.winsB > result.winsA * 2 -> "\u001B[32m"
                    result.winsA > result.winsB * 2 -> "\u001B[31m"
                    else -> ""
                }
            val reset = "\u001B[0m"

            logInfo(
                "${colorA}${engineA.padEnd(20)}$reset vs ${colorB}${engineB.padEnd(20)}$reset: " +
                        "${result.winsA} - ${result.winsB} - ${result.draws} " +
                        "(A: $winRateA | B: $winRateB)",
            )
        }

        logInfo("=".repeat(100))
    }

    /**
     * Función auxiliar para formatear scores con colores manteniendo la alineación
     */
    private fun formatScoreWithColor(
        wins: Int,
        losses: Int,
        games: Int,
    ): String {
        val plainScore = "$wins-$losses"
        val targetWidth = 8

        return if (games > 0) {
            val winRate = wins.toDouble() / games
            val padding = targetWidth - plainScore.length
            val spaces = " ".repeat(if (padding > 0) padding else 0)

            when {
                winRate >= 0.66 -> "${spaces}\u001B[32m$plainScore\u001B[0m"
                winRate >= 0.33 -> "${spaces}$plainScore"
                else -> "${spaces}\u001B[31m$plainScore\u001B[0m"
            }
        } else {
            " ".repeat(targetWidth - 2) + "- " // Centrar el guión
        }
    }

    /**
     * Data class para almacenar resultados de enfrentamientos directos
     */
    data class HeadToHeadResult(
        val engineA: String,
        val engineB: String,
        val winsA: Int,
        val winsB: Int,
        val draws: Int,
        val averageMoves: Double = 0.0,
        val timeoutsA: Int = 0,
        val timeoutsB: Int = 0,
    ) {
        val totalGames: Int get() = winsA + winsB + draws
    }

    private fun calculateElo(winRate: Double): Int =
        if (winRate >= 0.999) {
            2800
        } else if (winRate <= 0.001) {
            1200
        } else {
            (2000 + (-400 * log10(1 / winRate - 1)).roundToInt()).coerceIn(1200, 2800)
        }

    private data class TestGameResult(
        val winner: CobColor?,
        val moves: Int,
        val timeout: Boolean,
        val performanceMetrics: PerformanceMetrics? = null,
    )

    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
    )
}