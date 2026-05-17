package com.agustin.tarati.core.data.database.dto

import com.agustin.tarati.core.data.database.entities.GameEntity
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.MatchResult.BLACK_WON
import com.agustin.tarati.core.domain.game.play.MatchResult.UNDEFINED
import com.agustin.tarati.core.domain.game.play.MatchResult.WHITE_WON
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.core.domain.game.play.getValue
import com.agustin.tarati.core.domain.game.play.groupByTurns
import com.agustin.tarati.core.utils.helpers.getCurrentDate

data class PGNHeader(
    val event: String = "Tarati Game",
    val site: String = "?",
    val date: String = getCurrentDate(),
    val round: String = "-",
    val white: String = "Player W",
    val black: String = "Player B",
    val result: String = UNDEFINED.getValue(),
    val gameType: String = "-",
    val rules: String = "-",
    val timeControl: String = "N/A",
    val termination: String = "N/A",
    val observations: String = "",
) {
    fun toPGNString(): String =
        buildString {
            appendLine("[Event \"$event\"]")
            appendLine("[Site \"$site\"]")
            appendLine("[Date \"$date\"]")
            appendLine("[Round \"$round\"]")
            appendLine("[White \"$white\"]")
            appendLine("[Black \"$black\"]")
            appendLine("[Result \"$result\"]")
            appendLine("[GameType \"$gameType\"]")
            appendLine("[Rules \"$rules\"]")
            appendLine("[TimeControl \"$timeControl\"]")
            appendLine("[Termination \"$termination\"]")
            appendLine("[Observations \"$observations\"]")
        }.trim()

    companion object {
        val invalidValues: List<String> =
            listOf(
                "?",
                "-",
                "N/A",
                "",
            )

        fun List<Move>.generatePGNMoveHistory(
            gameState: GameState,
            playerSide: CobColor,
            aiEnabled: Boolean,
            appendFen: Boolean,
            positionHistory: Map<String, Int> = emptyMap(),
        ): String {
            val moveHistory = this.toList()

            val header = createPGNHeader(gameState, playerSide, aiEnabled, positionHistory)
            val movesText = generateMoveNotation(moveHistory)
            val result = getGameResult(gameState, positionHistory)

            return buildString {
                appendLine(header.toPGNString())
                appendLine()
                append(movesText)
                if (result != UNDEFINED.getValue()) {
                    append(" $result")
                }
                if (appendFen) {
                    appendLine()
                    appendLine(gameState.toPositionNotation())
                }
            }
        }

        private fun generateMoveNotation(moveHistory: List<Move>): String =
            buildString {
                moveHistory.groupByTurns().chunked(2).forEachIndexed { index, turnPair ->
                    val moveNumber = index + 1
                    val whiteGroup = turnPair.firstOrNull()
                    val blackGroup = turnPair.getOrNull(1)

                    append("$moveNumber. ")
                    whiteGroup?.let { append(it.notation) }
                    blackGroup?.let { append(" ${it.notation}") }
                    append(" ")
                }
            }.trim()

        private fun getGameResult(gameState: GameState, positionHistory: Map<String, Int>): String =
            getMatchResult(gameState, positionHistory).getValue()

        private fun getMatchResult(gameState: GameState, positionHistory: Map<String, Int>): MatchResult {
            val winner = gameState.getWinner(positionHistory)
            return when (winner) {
                CobColor.WHITE -> WHITE_WON
                CobColor.BLACK -> BLACK_WON
                else -> UNDEFINED
            }
        }

        private fun getTermination(gameState: GameState, positionHistory: Map<String, Int>): String {
            val winner = gameState.getWinner(positionHistory)
            return when {
                winner == null -> "Unterminated"
                else -> "Normal"
            }
        }

        fun createPGNHeader(
            gameState: GameState,
            playerSide: CobColor = CobColor.WHITE,
            aiEnabled: Boolean = false,
            positionHistory: Map<String, Int> = emptyMap(),
        ): PGNHeader {
            val result = getGameResult(gameState, positionHistory)
            val termination = getTermination(gameState, positionHistory)

            val whitePlayer =
                when {
                    playerSide == CobColor.WHITE -> "Human"
                    aiEnabled -> "AI"
                    else -> "Human"
                }
            val blackPlayer =
                when {
                    playerSide == CobColor.BLACK -> "Human"
                    aiEnabled -> "AI"
                    else -> "Human"
                }

            return PGNHeader(
                white = whitePlayer,
                black = blackPlayer,
                result = result,
                termination = termination,
            )
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
    }
}