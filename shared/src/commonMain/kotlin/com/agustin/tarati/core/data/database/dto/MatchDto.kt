package com.agustin.tarati.core.data.database.dto

import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.parseBoardNotation
import kotlin.time.Clock

data class MatchDto(
    val id: String? = null,
    val header: PGNHeader,
    val game: GameDto,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
) {
    fun toGameState(): GameState = parseBoardNotation(this.game.boardPosition)

    companion object
}
