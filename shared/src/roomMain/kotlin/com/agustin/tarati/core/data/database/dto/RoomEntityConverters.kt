package com.agustin.tarati.core.data.database.dto

import com.agustin.tarati.core.data.database.entities.GameEntity
import com.agustin.tarati.core.domain.game.play.Move.Companion.parseMoveHistory
import com.agustin.tarati.core.domain.game.play.parseMatchResult

/** Conversores [GameEntity] → DTOs del paquete commonMain. */

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

fun parsePGNHeader(entity: GameEntity): PGNHeader =
    PGNHeader(
        event = entity.event,
        site = entity.site,
        date = entity.date,
        round = entity.round,
        white = entity.white,
        black = entity.black,
        result = entity.result,
        gameType = entity.gameType,
        rules = entity.rules,
        timeControl = entity.timeControl,
        termination = entity.termination,
        observations = entity.observations,
    )

fun parseGameDto(entity: GameEntity): GameDto =
    GameDto(
        initialBoardPosition = entity.initialBoardPosition,
        boardPosition = entity.boardPosition,
        matchResult = parseMatchResult(entity.matchResult),
        moveHistory = parseMoveHistory(entity.moveHistory),
    )
