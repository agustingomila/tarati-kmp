package com.agustin.tarati.core.domain.ai.api

data class CacheStats(
    val fullEvaluationSize: Int,
    val quickEvaluationSize: Int,
    val moveOrderingSize: Int,
    val hitRate: Double,
)
