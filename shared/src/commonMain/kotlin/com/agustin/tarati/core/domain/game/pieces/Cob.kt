package com.agustin.tarati.core.domain.game.pieces

import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.board.GameBoard.adjacencyMap
import com.agustin.tarati.core.domain.game.board.GameBoard.upgradeVertices
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import kotlinx.serialization.Serializable

/**
 * In Tarati the pieces are called "Cob" and the upgrades pieces are called "Rok"
 */
@Serializable
data class Cob(
    val color: CobColor,
    val isUpgraded: Boolean = false,
) {

    /**
     * Promotes this cob to a rok if it has been advanced onto one of its upgrade vertices
     * (C7, C8, D3, D4 for white; C1, C2, D1, D2 for black).
     *
     * Per the patent: "A cob piece is promoted to a rok piece when it is advanced onto
     * an opponent's home-base stopping point." ALL four home-base stopping points trigger
     * promotion on arrival via forward movement — including the D-ring outermost vertices.
     *
     * Cobs that arrive at D-ring vertices via capture (flip) are never passed through this
     * function; they stay as cobs and become dead (no forward moves available from there).
     *
     * This function is only called for the moving piece in applyMove, never for captured pieces.
     */
    fun upgradeIfInEnemyBase(vertex: Vertex): Cob {
        val myUpgradeVertices = upgradeVertices[this.color] ?: emptyList()
        return if (vertex in myUpgradeVertices) this.copy(isUpgraded = true) else this
    }

    fun calculateMobility(
        gameState: GameState,
        vertex: Vertex,
    ): Int =
        adjacencyMap[vertex]?.count { to ->
            !gameState.cobs.containsKey(to) &&
                    (isUpgraded || GameBoard.isForwardMove(color, Move(vertex to to)))
        } ?: 0

    fun canReachVertex(
        from: Vertex,
        to: Vertex,
    ): Boolean {
        // Los Roks pueden moverse en cualquier dirección
        if (isUpgraded && from.isAdjacentTo(to)) return true

        // Los Cobs solo pueden moverse hacia adelante
        return GameBoard.isForwardMove(color, Move(from to to))
    }

    fun canUpgrade(vertex: Vertex): Boolean = !isUpgraded && (vertex in upgradeVertices[this.color].orEmpty())

    fun countMaxFlipsByType(
        gameState: GameState,
        vertex: Vertex,
    ): Pair<Int, Int> {
        if (!gameState.cobs.containsKey(vertex) || gameState.cobs[vertex] != this) {
            return Pair(0, 0)
        }

        val possibleMoves = gameState.getPossiblesVertexForCob(this, vertex)

        return possibleMoves
            .map { to ->
                val move = Move(vertex to to)
                val newState = gameState.applyMove(move)
                move.countFlipsByType(gameState, newState)
            }.maxByOrNull { (rocFlips, cobFlips) ->
                rocFlips + cobFlips
            } ?: Pair(0, 0)
    }

    fun countThreats(
        gameState: GameState,
        vertex: Vertex,
    ): Int {
        val enemyColor = color.opponent

        return adjacencyMap[vertex]?.count { adjacentVertex ->
            // Solo consideramos casillas vacías adyacentes
            if (gameState.cobs.containsKey(adjacentVertex)) {
                false
            } else {
                // Verificamos si alguna pieza enemiga puede moverse a esta casilla vacía
                adjacencyMap[adjacentVertex]?.any { potentialAttackerVertex ->
                    val potentialAttacker = gameState.cobs[potentialAttackerVertex]

                    // Es una pieza enemiga que puede moverse a la casilla vacía
                    potentialAttacker?.color == enemyColor &&
                            potentialAttacker.canReachVertex(potentialAttackerVertex, adjacentVertex)
                } == true
            }
        } ?: 0
    }
}