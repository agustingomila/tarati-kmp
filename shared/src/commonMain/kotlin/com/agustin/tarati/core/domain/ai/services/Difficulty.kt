package com.agustin.tarati.core.domain.ai.services

enum class Difficulty(val depth: Int) {
    EASY(2),
    MEDIUM(3),
    HARD(5),
    CHAMPION(7);

    companion object {
        val ALL = entries
        val MIN = EASY
        val DEFAULT = MEDIUM
        val MAX = CHAMPION

        fun getByOrdinal(ordinal: Int): Difficulty = ALL.getOrElse(ordinal) { DEFAULT }
    }
}