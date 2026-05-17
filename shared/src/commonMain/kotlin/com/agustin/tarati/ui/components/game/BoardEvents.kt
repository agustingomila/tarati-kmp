package com.agustin.tarati.ui.components.game

import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.play.Move

interface BoardEvents {
    fun onMove(move: Move)

    fun onEditPiece(from: Vertex)

    fun onResetCompleted()
}