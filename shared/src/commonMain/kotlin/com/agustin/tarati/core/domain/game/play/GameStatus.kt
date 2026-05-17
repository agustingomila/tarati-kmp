package com.agustin.tarati.core.domain.game.play

import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus {
    PLAYING,
    NO_PLAYING,
    GAME_OVER,
}