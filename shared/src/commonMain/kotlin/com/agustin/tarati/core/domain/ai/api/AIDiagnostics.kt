package com.agustin.tarati.core.domain.ai.api

data class AIDiagnostics(
    val cacheStats: CacheStats,
    val positionHistorySize: Int,
    val transpositionTableSize: Int,
    val nodesEvaluated: Long = 0,
    val cutoffs: Int = 0,
    val cacheHits: Int = 0,
)