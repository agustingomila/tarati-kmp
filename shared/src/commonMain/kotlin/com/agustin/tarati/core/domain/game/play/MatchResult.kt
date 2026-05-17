package com.agustin.tarati.core.domain.game.play

import com.agustin.tarati.core.domain.game.play.MatchResult.BLACK_WON
import com.agustin.tarati.core.domain.game.play.MatchResult.UNDEFINED
import com.agustin.tarati.core.domain.game.play.MatchResult.WHITE_WON

enum class MatchResult {
    UNDEFINED,
    WHITE_WON,
    BLACK_WON,
}

fun MatchResult.getValue(): String =
    when (this) {
        UNDEFINED -> "*"
        WHITE_WON -> "1-0"
        BLACK_WON -> "0-1"
    }

fun parseMatchResult(resultStr: String): MatchResult =
    when (resultStr) {
        UNDEFINED.getValue() -> UNDEFINED
        WHITE_WON.getValue() -> WHITE_WON
        BLACK_WON.getValue() -> BLACK_WON
        else -> throw IllegalArgumentException("Unknown match result: $resultStr")
    }
