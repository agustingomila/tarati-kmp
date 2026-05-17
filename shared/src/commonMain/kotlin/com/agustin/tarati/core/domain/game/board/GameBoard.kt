package com.agustin.tarati.core.domain.game.board

import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.Move
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Estructura lógica del tablero de Tarati.
 * Contiene la definición de vertices, edges, regiones y reglas del juego.
 *
 * NO contiene código de renderizado visual (sin dependencias de Compose).
 */
object GameBoard {
    // ========== Constantes de Juego ==========

    /** Umbral en unidades normalizadas para determinar si un movimiento es "hacia adelante" */
    private const val FORWARD_MOVE_THRESHOLD = 10f

    /** Tamaño de referencia para cálculos normalizados */
    private const val REFERENCE_BOARD_SIZE = 1100f

    /** Ancho de vértice en unidades normalizadas */
    private const val VERTEX_WIDTH = 250f

    // ========== Zones ==========

    val ABSOLUTE = Zone('A')
    val BRIDGE = Zone('B')
    val CIRCUMFERENCE = Zone('C')
    val DOMESTIC = Zone('D')

    // ========== Vertex Collections ==========

    val A1 = Vertex(ABSOLUTE, 1)

    val bridgeVertices = (1..6).map { Vertex(BRIDGE, it) }
    val circumferenceVertices = (1..12).map { Vertex(CIRCUMFERENCE, it) }
    val domesticVertices = (1..4).map { Vertex(DOMESTIC, it) }

    // Named vertices for convenience
    val B1 get() = bridgeVertices[0]
    val B2 get() = bridgeVertices[1]
    val B3 get() = bridgeVertices[2]
    val B4 get() = bridgeVertices[3]
    val B5 get() = bridgeVertices[4]
    val B6 get() = bridgeVertices[5]

    val C1 get() = circumferenceVertices[0]
    val C2 get() = circumferenceVertices[1]
    val C3 get() = circumferenceVertices[2]
    val C4 get() = circumferenceVertices[3]
    val C5 get() = circumferenceVertices[4]
    val C6 get() = circumferenceVertices[5]
    val C7 get() = circumferenceVertices[6]
    val C8 get() = circumferenceVertices[7]
    val C9 get() = circumferenceVertices[8]
    val C10 get() = circumferenceVertices[9]
    val C11 get() = circumferenceVertices[10]
    val C12 get() = circumferenceVertices[11]

    val D1 get() = domesticVertices[0]
    val D2 get() = domesticVertices[1]
    val D3 get() = domesticVertices[2]
    val D4 get() = domesticVertices[3]

    // All vertices
    val centerVertices = listOf(A1) + bridgeVertices
    val vertices = centerVertices + circumferenceVertices + domesticVertices

    val externalBoundary =
        listOf(C1, D1, D2, C2, C3, C4, C5, C6, C7, D3, D4, C8, C9, C10, C11, C12)

    // ========== Edge Definitions ==========

    val whiteDomesticEdges =
        listOf(
            Edge(D1 to D2),
            Edge(D1 to C1),
            Edge(D2 to C2),
        )

    val blackDomesticEdges =
        listOf(
            Edge(D3 to D4),
            Edge(D3 to C7),
            Edge(D4 to C8),
        )

    val domesticEdges = whiteDomesticEdges + blackDomesticEdges

    val bridgeEdges =
        (0..5).map { index ->
            Edge(bridgeVertices[index] to bridgeVertices[(index + 1) % 6])
        }

    val circumferenceEdges =
        (0..11).map { index ->
            Edge(circumferenceVertices[index] to circumferenceVertices[(index + 1) % 12])
        }

    val bridgeToCircumferenceEdges =
        listOf(
            Edge(C1 to B1),
            Edge(C2 to B1),
            Edge(C3 to B2),
            Edge(C4 to B2),
            Edge(C5 to B3),
            Edge(C6 to B3),
            Edge(C7 to B4),
            Edge(C8 to B4),
            Edge(C9 to B5),
            Edge(C10 to B5),
            Edge(C11 to B6),
            Edge(C12 to B6),
        )

    val absoluteCenterToBridgeEdges = bridgeVertices.map { Edge(it to A1) }

    val edges =
        domesticEdges + bridgeEdges + circumferenceEdges + bridgeToCircumferenceEdges + absoluteCenterToBridgeEdges

    // ========== Game Areas ==========

    val homeBases =
        mapOf(
            CobColor.WHITE to listOf(C1, C2, D1, D2),
            CobColor.BLACK to listOf(C7, C8, D3, D4),
        )

    /**
     * Vertices where cobs are promoted to roks when advanced onto them.
     *
     * Per the patent: "A cob piece is promoted to a rok piece when it is advanced onto
     * an opponent's home-base stopping point." The opponent's home base has four stopping
     * points (C-ring + D-ring), so ALL four trigger promotion on arrival via forward move.
     *
     * The "dead cob" concept is orthogonal: a cob that ARRIVES at a D-ring vertex via
     * capture (flip) is never passed through upgradeIfInEnemyBase — it stays as a cob
     * and is immediately dead because there are no forward moves from D3/D4 (for white)
     * or D1/D2 (for black). A cob that MOVES forward onto a D-ring vertex is promoted
     * immediately and becomes a rok, which is never dead.
     */
    val upgradeVertices =
        mapOf(
            CobColor.WHITE to listOf(C7, C8, D3, D4),
            CobColor.BLACK to listOf(C1, C2, D1, D2),
        )

    /**
     * Vertices where a cob of the given color is immediately dead and cannot advance.
     * These are the D-ring (outermost) vertices of the opponent's home base.
     * A cob can only reach these via capture (flip), never via forward movement.
     */
    val deadVertices =
        mapOf(
            CobColor.WHITE to listOf(D3, D4),
            CobColor.BLACK to listOf(D1, D2),
        )

    // ========== Regions ==========

    val centralRegions =
        (0..5).map { index ->
            Region(listOf(A1, bridgeVertices[index], bridgeVertices[(index + 1) % 6]))
        }

    val circumferenceRegions =
        listOf(
            Region(listOf(B1, C1, C2)),
            Region(listOf(B1, C2, C3, B2)),
            Region(listOf(B2, C3, C4)),
            Region(listOf(B2, C4, C5, B3)),
            Region(listOf(B3, C5, C6)),
            Region(listOf(B3, C6, C7, B4)),
            Region(listOf(B4, C7, C8)),
            Region(listOf(B4, C8, C9, B5)),
            Region(listOf(B5, C9, C10)),
            Region(listOf(B5, C10, C11, B6)),
            Region(listOf(B6, C11, C12)),
            Region(listOf(B6, C12, C1, B1)),
        )

    val domesticRegions =
        listOf(
            Region(listOf(C1, C2, D2, D1)),
            Region(listOf(C7, C8, D4, D3)),
        )

    val allRegions = domesticRegions + centralRegions + circumferenceRegions

    val vertexToRegions by lazy {
        allRegions
            .flatMap { region ->
                region.vertices.map { vertex -> vertex to region }
            }.groupBy({ it.first }, { it.second })
    }

    /**
     * Mapa de adyacencias del tablero.
     * CRÍTICO: Usado por el motor de IA para generar movimientos válidos.
     */
    val adjacencyMap by lazy {
        edges.fold(mutableMapOf<Vertex, MutableList<Vertex>>()) { map, edge ->
            map.apply {
                getOrPut(edge.from) { mutableListOf() }.add(edge.to)
                getOrPut(edge.to) { mutableListOf() }.add(edge.from)
            }
        }
    }

    // ========== Movement Logic ==========

    /**
     * Posición normalizada de un vértice (sin depender de Compose).
     */
    private data class Position2D(val x: Float, val y: Float)

    /**
     * Calcula la posición normalizada de un vértice.
     * NOTA: Esta es una versión simplificada para lógica de juego.
     * Para renderizado visual, usar BoardGeometry en androidApp.
     */
    private fun getLogicalPosition(
        vertex: Vertex,
        boardSize: Pair<Float, Float>,
        vWidth: Float,
    ): Position2D {
        val (width, height) = boardSize
        val centerX = width / 2
        val centerY = height / 2

        if (vertex == A1) return Position2D(centerX, centerY)

        return when (vertex.zone) {
            BRIDGE -> {
                val angle = (vertex.position - 1) * (PI / 3)
                Position2D(
                    x = centerX + vWidth * cos(angle + PI / 2).toFloat(),
                    y = centerY + vWidth * sin(angle + PI / 2).toFloat(),
                )
            }

            CIRCUMFERENCE -> {
                val angle = (vertex.position - 1) * (PI / 6) - PI / 12 + PI / 2
                val radius = vWidth * (1 + sqrt(11.0 / 13)).toFloat()
                Position2D(
                    x = centerX + radius * cos(angle).toFloat(),
                    y = centerY + radius * sin(angle).toFloat(),
                )
            }

            DOMESTIC -> {
                val connectedC = getConnectedCircumferenceVertex(vertex)
                val baseRadius = vWidth * (1 + sqrt(11.0 / 13)).toFloat()
                val baseAngle = (connectedC.position - 1) * (PI / 6) - PI / 12 + PI / 2

                val basePos =
                    Position2D(
                        x = centerX + baseRadius * cos(baseAngle).toFloat(),
                        y = centerY + baseRadius * sin(baseAngle).toFloat(),
                    )

                val displacement =
                    if (vertex in homeBases[CobColor.WHITE]!!) {
                        Position2D(0f, vWidth)
                    } else {
                        Position2D(0f, -vWidth)
                    }

                Position2D(
                    x = basePos.x + displacement.x,
                    y = basePos.y + displacement.y
                )
            }

            else -> Position2D(centerX, centerY)
        }
    }

    private fun getConnectedCircumferenceVertex(domesticVertex: Vertex): Vertex =
        domesticEdges
            .filter { it.from == domesticVertex || it.to == domesticVertex }
            .flatMap { listOf(it.from, it.to) }
            .first { it.zone == CIRCUMFERENCE }

    /**
     * Determina si un movimiento es "hacia adelante" para un color dado.
     * WHITE avanza hacia arriba (Y decrece), BLACK hacia abajo (Y crece).
     */
    fun isForwardMove(
        color: CobColor,
        move: Move,
    ): Boolean {
        val boardCenter = VERTEX_WIDTH to VERTEX_WIDTH
        val fromPos = getLogicalPosition(move.from, boardCenter, VERTEX_WIDTH)
        val toPos = getLogicalPosition(move.to, boardCenter, VERTEX_WIDTH)

        return when (color) {
            CobColor.WHITE -> fromPos.y - toPos.y > FORWARD_MOVE_THRESHOLD
            else -> toPos.y - fromPos.y > FORWARD_MOVE_THRESHOLD
        }
    }

    /**
     * Returns all non-forward adjacent moves available from [from] if it belongs to [color]'s
     * own home base. These moves are only legally playable when they result in at least one
     * capture (see GameState.getHomeBaseMoves for the full validity check).
     */
    fun getHomeBaseNonForwardMoves(
        color: CobColor,
        from: Vertex,
    ): List<Move> {
        val ownBase = homeBases[color] ?: return emptyList()
        if (from !in ownBase) return emptyList()
        return adjacencyMap[from]
            ?.filter { to -> !isForwardMove(color, Move(from to to)) }
            ?.map { to -> Move(from to to) }
            ?: emptyList()
    }

    /**
     * Validates whether [move] is legal in [gameState].
     *
     * Valid move categories:
     * 1. Home-base non-forward moves that produce at least one capture (pre-adjacency rule).
     * 2. Normal forward moves for cobs to an empty adjacent vertex.
     * 3. Any-direction moves for roks to an empty adjacent vertex.
     */
    fun isValidMove(
        gameState: GameState,
        move: Move,
    ): Boolean {
        val cob = gameState.cobs[move.from] ?: return false
        if (cob.color != gameState.currentTurn) return false

        if (gameState.getHomeBaseMoves(move.from, cob).contains(move)) {
            return true
        }

        val isAdjacent = adjacencyMap[move.from]?.contains(move.to) ?: false
        if (!isAdjacent) return false

        return when {
            gameState.cobs.containsKey(move.to) -> false
            !cob.isUpgraded -> isForwardMove(cob.color, move)
            else -> true
        }
    }
}