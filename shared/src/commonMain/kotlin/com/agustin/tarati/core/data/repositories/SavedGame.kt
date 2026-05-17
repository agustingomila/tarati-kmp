package com.agustin.tarati.core.data.repositories

data class SavedGame(
    val id: String,
    val whitePlayer: String,
    val blackPlayer: String,
    val result: String,
    val date: String,
    val moveCount: Int,
)