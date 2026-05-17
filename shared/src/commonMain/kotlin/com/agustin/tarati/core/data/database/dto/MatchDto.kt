package com.agustin.tarati.core.data.database.dto

import com.agustin.tarati.core.data.database.dto.GameDto.Companion.parseGameDto
import com.agustin.tarati.core.data.database.dto.PGNHeader.Companion.parsePGNHeader
import com.agustin.tarati.core.data.database.entities.GameEntity
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

    companion object {
        fun parseMatchDto(entity: GameEntity): MatchDto? =
            try {
                MatchDto(
                    id = entity.id,
                    header = parsePGNHeader(entity),
                    game = parseGameDto(entity),
                    createdAt = entity.createdAt,
                )
            } catch (_: Exception) {
                null
            }
    }
}
