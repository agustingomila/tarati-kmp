package com.agustin.tarati.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.domain.game.play.Move.Companion.MOVE_SEPARATOR
import com.agustin.tarati.core.domain.game.play.getValue
import com.benasher44.uuid.uuid4
import kotlin.time.Clock

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val event: String,
    val site: String,
    val date: String,
    val round: String,
    val white: String,
    val black: String,
    val result: String,
    val gameType: String,
    val rules: String,
    val timeControl: String,
    val termination: String,
    val observations: String,
    val initialBoardPosition: String,
    val boardPosition: String,
    val matchResult: String,
    val moveHistory: String,
    val createdAt: Long,
    val updatedAt: Long,
) {
    companion object {
        fun parseGameEntity(dto: MatchDto): GameEntity =
            GameEntity(
                id = dto.id ?: uuid4().toString(),
                event = dto.header.event,
                site = dto.header.site,
                date = dto.header.date,
                round = dto.header.round,
                white = dto.header.white,
                black = dto.header.black,
                result = dto.header.result,
                gameType = dto.header.gameType,
                rules = dto.header.rules,
                timeControl = dto.header.timeControl,
                termination = dto.header.termination,
                observations = dto.header.observations,
                initialBoardPosition = dto.game.initialBoardPosition,
                boardPosition = dto.game.boardPosition,
                matchResult = dto.game.matchResult.getValue(),
                moveHistory =
                    dto.game.moveHistory.joinToString(",") {
                        "${it.from.name}${MOVE_SEPARATOR}${it.to.name}"
                    },
                createdAt = dto.id?.let { dto.createdAt } ?: Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )
    }
}