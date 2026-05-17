package com.agustin.tarati.game.ai.tournament.helpers

data class PerformanceMetrics(
    val averageCacheHitRate: Double,
    val averageNodesPerMove: Double,
    val averageCutoffsPerMove: Double,
    val averageMoveTimeMs: Double,
)
