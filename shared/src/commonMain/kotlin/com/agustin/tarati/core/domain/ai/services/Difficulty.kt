package com.agustin.tarati.core.domain.ai.services

import kotlin.enums.EnumEntries

enum class Difficulty(val depth: Int) {
    EASY(2),
    MEDIUM(3),
    HARD(5),
    CHAMPION(7);

    companion object {
        val ALL: EnumEntries<Difficulty> = entries
        val MIN: Difficulty = EASY
        val DEFAULT: Difficulty = MEDIUM
        val MAX: Difficulty = CHAMPION

        fun getByOrdinal(ordinal: Int): Difficulty = ALL.getOrElse(ordinal) { DEFAULT }
    }
}