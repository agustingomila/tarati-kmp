package com.agustin.tarati.game.ai.tournament.manager

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class TournamentConfig(
    val gamesPerMatch: Int = 50,
    val maxMovesPerGame: Int = 200,
    val maxTimePerMove: Duration = 30.seconds,
    val alternateColors: Boolean = true,
    val verbose: Boolean = false,
    val showProgress: Boolean = true,
    val collectMetrics: Boolean = true,
)
