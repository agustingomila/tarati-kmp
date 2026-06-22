package com.agustin.tarati.core.data.database.dto

import androidx.compose.runtime.Immutable
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.parseBoardNotation
import kotlin.time.Clock

/**
 * DTO de solo lectura de una partida (cabecera PGN + datos de juego). Anotado
 * [@Immutable] para Compose: todos sus campos son inmutables ([PGNHeader] es solo
 * `String`; [GameDto] está anotado), habilitando el skip de recomposición en los
 * composables que lo reciben como parámetro.
 */
@Immutable
data class MatchDto(
    val id: String? = null,
    val header: PGNHeader,
    val game: GameDto,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
) {
    fun toGameState(): GameState = parseBoardNotation(this.game.boardPosition)

    companion object
}
