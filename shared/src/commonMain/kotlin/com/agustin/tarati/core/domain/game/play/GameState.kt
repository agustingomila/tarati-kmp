package com.agustin.tarati.core.domain.game.play

import com.agustin.tarati.core.data.database.dto.GameDto
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.PGNHeader.Companion.createPGNHeader
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.board.GameBoard.adjacencyMap
import com.agustin.tarati.core.domain.game.board.GameBoard.deadVertices
import com.agustin.tarati.core.domain.game.board.GameBoard.getHomeBaseNonForwardMoves
import com.agustin.tarati.core.domain.game.board.GameBoard.isForwardMove
import com.agustin.tarati.core.domain.game.board.GameBoard.isValidMove
import com.agustin.tarati.core.domain.game.board.GameBoard.vertexToRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.vertices
import com.agustin.tarati.core.domain.game.board.Region
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.helpers.GameStateBuilder
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.pieces.PieceCounts
import com.agustin.tarati.core.domain.game.pieces.flipAdjacentCobs
import com.agustin.tarati.core.domain.game.pieces.getName
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.core.domain.game.play.GameState.Companion.POS_DELIMITER_CHAR
import com.agustin.tarati.core.domain.game.play.GameState.Companion.TURN_DELIMITER_CHAR
import com.agustin.tarati.core.domain.game.play.GameState.Companion.initialGameState
import com.agustin.tarati.core.domain.game.play.MatchResult.UNDEFINED
import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val cobs: Map<Vertex, Cob>,
    val currentTurn: CobColor,
    /**
     * Half-move clock for the 50-move rule (§7.2). Counts consecutive half-moves
     * (single-player turns) without a cob being moved or promoted. Resets to 0
     * whenever a cob is moved (forward, home-base) or any promotion occurs.
     * Rok moves increment it. At 100 (= 50 moves per player) the game may be drawn,
     * unless the player to move has an immediate winning move available.
     */
    val halfMoveClock: Int = 0,
    /**
     * Set to true when a player explicitly claims the 50-move draw (§7.2.2).
     * This is the only way FIFTY_MOVES enters isGameOver — the rule is never
     * auto-applied; the human claims it via the UI, the AI auto-claims it via
     * a LaunchedEffect in GameScreen.
     */
    val claimedFiftyMoveDraw: Boolean = false,
    /**
     * Color del bando que perdió por tiempo, o `null` si la partida no terminó
     * por timeout. Seteado por [GameEvents.onTimeout]
     * cuando [IClockService.timeoutEvents] emite.
     *
     * Cuando es no-null:
     *  - [isGameOver] retorna `true`.
     *  - [getMatchState] retorna [GameResult.TIMEOUT] con `winner = timedOutColor.opponent`.
     *
     * No participa en [hashBoard] (que solo incluye cobs + currentTurn), por lo
     * que no afecta al transposition table de la IA ni al historial de posiciones.
     */
    val timedOutColor: CobColor? = null,
) {
    // Función de extensión para modificar piezas
    fun modifyCob(
        position: Vertex,
        cob: Cob?,
    ): GameState = this.modifyCob(position, cob?.color, cob?.isUpgraded)

    fun modifyCob(
        position: Vertex,
        color: CobColor? = null,
        isUpgraded: Boolean? = null,
    ): GameState {
        val newCobs = cobs.toMutableMap()

        if (color == null && isUpgraded == null) {
            // Si ambos son null, eliminar la pieza
            newCobs.remove(position)
        } else {
            val currentCob = newCobs[position]
            val newColor = color ?: currentCob?.color ?: WHITE
            val newUpgraded = isUpgraded ?: currentCob?.isUpgraded ?: false

            newCobs[position] = Cob(newColor, newUpgraded)
        }

        return this.copy(cobs = newCobs)
    }

    // Función para mover piezas
    fun moveCob(move: Move): GameState {
        val newCobs = cobs.toMutableMap()
        val cob = newCobs[move.from] ?: return this

        newCobs.remove(move.from)
        newCobs[move.to] = cob

        return this.copy(cobs = newCobs)
    }

    // Función para cambiar el turno
    fun withTurn(newTurn: CobColor): GameState = this.copy(currentTurn = newTurn)

    /**
     * Genera una clave compacta de la posición actual para ser usada como
     * clave en la tabla de transposición y en el historial de posiciones
     * (detección de triple repetición).
     *
     * ## Por qué no usar hashCode()
     * El [hashCode] de Kotlin para data classes es un Int de 32 bits — demasiado
     * corto para distinguir de forma confiable los ~10k estados que puede alcanzar
     * una partida. Las colisiones producirían falsos positivos de triple repetición
     * o entradas incorrectas en la tabla de transposición, ambos errores silenciosos
     * que corrompen el juego.
     *
     * ## Construcción a partir de toPositionNotation
     * Reutiliza [toPositionNotation], que produce una representación canónica
     * ordenada de todas las piezas y el turno, y reemplaza los delimitadores
     * ['/' y ' '] por el carácter nulo [Char(0)]. Dos posiciones idénticas
     * siempre producen el mismo hash, y posiciones distintas producen hashes
     * distintos — sin colisiones por diseño, al ser el hash el estado completo.
     */
    /**
     * Zobrist hash of the current position.
     *
     * Computes a 64-bit XOR fingerprint from precomputed random keys for
     * each (vertex, piece-type) pair on the board, XOR-ed with a side-to-move
     * key when it is BLACK's turn. Probability of collision between distinct
     * positions is 1/2^64 -- negligible in practice.
     *
     * Complexity: O(n) over pieces on the board. No String allocation,
     * no sort, no delimiter replacement (vs. the previous O(n log n) impl).
     *
     * Returns a hex String for drop-in compatibility with positionHistory,
     * TranspositionTable, and HybridEvaluationCache (all use String keys).
     */
    fun hashBoard(): String {
        var hash =
            if (currentTurn == BLACK) ZOBRIST_SIDE_TO_MOVE else 0L
        for ((vertex, cob) in cobs) {
            val vi = VERTEX_INDEX[vertex] ?: continue
            val pi = when {
                cob.color == WHITE && !cob.isUpgraded -> 0
                cob.color == WHITE && cob.isUpgraded -> 1
                !cob.isUpgraded -> 2
                else -> 3
            }
            hash = hash xor ZOBRIST_PIECES[vi][pi]
        }
        return hash.toString(16)
    }

    // ==================== Estado del Juego ====================

    fun isGameOver(positionHistory: Map<String, Int> = emptyMap()): Boolean {
        if (timedOutColor != null) return true
        if (claimedFiftyMoveDraw) return true

        val whiteCobs = this.cobs.values.count { it.color == WHITE }
        val blackCobs = this.cobs.values.count { it.color == BLACK }

        return whiteCobs == 0 || blackCobs == 0 ||
                this.allMovesForTurn().isEmpty() ||
                this.hasTripleRepetition(positionHistory)
    }

    fun getWinner(
        positionHistory: Map<String, Int> = emptyMap(),
    ): CobColor? = getMatchState(positionHistory).winner

    fun getMatchState(
        positionHistory: Map<String, Int> = emptyMap(),
    ): MatchState {
        val whiteCobs = this.cobs.values.count { it.color == WHITE }
        val blackCobs = this.cobs.values.count { it.color == BLACK }

        var matchState =
            MatchState(
                gameState = this,
                gameResult = GameResult.PLAYING,
                winner = null,
                moveHistory = positionHistory,
            )

        if (!this.isGameOver(positionHistory)) return matchState

        // TIMEOUT tiene precedencia sobre todas las demás condiciones: es un
        // evento externo (del reloj) que invalida la partida independientemente
        // de la posición del tablero.
        this.timedOutColor?.let { loser ->
            matchState = matchState.copy(
                winner = loser.opponent,
                gameResult = GameResult.TIMEOUT,
            )
            return matchState
        }

        if (this.claimedFiftyMoveDraw) {
            // Draw — no winner. Must be checked before triple repetition since both
            // could technically be true; 50-move draw takes precedence as it was
            // established first in game time.
            matchState = matchState.copy(winner = null, gameResult = GameResult.FIFTY_MOVES)
            return matchState
        }

        if (this.hasTripleRepetition(positionHistory)) {
            matchState = matchState.copy(winner = this.currentTurn, gameResult = GameResult.TRIPLE)
            return matchState
        }

        matchState =
            when {
                whiteCobs == 0 -> {
                    matchState.copy(winner = BLACK, gameResult = GameResult.MIT)
                }

                blackCobs == 0 -> {
                    matchState.copy(winner = WHITE, gameResult = GameResult.MIT)
                }

                this.allMovesForTurn().isEmpty() -> {
                    matchState.copy(winner = this.currentTurn.opponent, gameResult = GameResult.STALEMIT)
                }

                else -> {
                    matchState.copy(winner = this.currentTurn, gameResult = GameResult.STALEMIT)
                }
            }

        return matchState
    }

    fun hasTripleRepetition(
        positionHistory: Map<String, Int> = emptyMap(),
    ): Boolean {
        val hash = this.hashBoard()
        return (positionHistory[hash] ?: 0) >= 3
    }

    fun checkIfWouldCauseRepetition(
        positionHistory: Map<String, Int> = emptyMap(),
    ): Boolean {
        val hash = this.hashBoard()
        val currentCount = positionHistory[hash] ?: 0
        return (currentCount + 1) >= 3
    }

    /**
     * Returns true if the 50-move draw rule can be claimed (§7.2).
     *
     * Conditions:
     * - At least 100 consecutive half-moves (50 per player) without a cob move or promotion.
     * - The player to move does NOT have an immediate winning move available.
     *   (A player who is about to win cannot be forced into a draw by this rule.)
     */
    /**
     * Returns true when the 50-move draw can be claimed (§7.2.2):
     * clock ≥ 100 AND the current player has no immediate winning move.
     * Used by the UI to show the claim badge and by the AI to auto-claim.
     */
    fun canClaimFiftyMoveDraw(): Boolean {
        if (halfMoveClock < 100) return false
        return !hasWinningMoveAvailable()
    }

    /**
     * Returns true if the current player has at least one move that immediately ends
     * the game in their favour. Used to suppress the 50-move draw when a win is available.
     *
     * Intentionally avoids calling [isGameOver] on the resulting state to prevent
     * mutual recursion through [canClaimFiftyMoveDraw].
     */
    private fun hasWinningMoveAvailable(): Boolean =
        allMovesForTurn().any { move ->
            isImmediateWinAfter(applyMove(move))
        }

    /**
     * Checks whether [next] (the state after our move) is an immediate win for
     * [currentTurn] without going through [isGameOver] / [canClaimFiftyMoveDraw].
     */
    private fun isImmediateWinAfter(next: GameState): Boolean {
        val opponentColor = currentTurn.opponent
        // MIT: all opponent pieces captured
        if (next.cobs.values.none { it.color == opponentColor }) return true
        // STALEMIT: opponent has no normal moves and no forced promotions
        if (next.normalMovesForTurn().isEmpty() && next.getForcedPromotions().isEmpty()) return true
        return false
    }

    fun allMovesForTurn(): MutableList<Move> {
        val normal = normalMovesForTurn()
        if (normal.isNotEmpty()) return normal.toMutableList()
        return getForcedPromotions().toMutableList()
    }

    /**
     * Memoized list of standard moves for the current turn player.
     *
     * [GameState] is immutable, so this result is computed at most once per instance.
     * [LazyThreadSafetyMode.NONE] is safe because the minimax engine is single-threaded;
     * it avoids the AtomicReferenceFieldUpdater overhead of the synchronized default.
     *
     * Not a constructor parameter: invisible to equals, hashCode, copy, and Parcelize.
     */
    private val normalMoves: List<Move> by lazy(LazyThreadSafetyMode.NONE) {
        buildList {
            cobs.forEach { (from, cob) ->
                if (cob.color != currentTurn) return@forEach
                // Movimientos no-forward desde base propia (incluye capturas desde base)
                addAll(getHomeBaseMoves(from, cob))
                // Movimientos normales (forward para cobs, cualquier dirección para roks)
                adjacencyMap[from]
                    ?.filter { to -> isValidMove(this@GameState, Move(from to to)) }
                    ?.forEach { to -> add(Move(from to to)) }
            }
        }
    }

    /** Returns [normalMoves], computing it at most once per [GameState] instance. */
    fun normalMovesForTurn(): List<Move> = normalMoves

    /**
     * Returns forced promotion moves available when the player has no normal moves.
     *
     * Both triggers share the same prerequisite: [normalMovesForTurn] must be empty.
     * "Wherever it is positioned" in the sole-piece rule refers to the piece's location
     * on the board, not to an exemption from the no-normal-moves requirement.
     *
     * Two triggers (patent §6.3 / §6.4):
     *
     * - **Sole remaining cob**: if the current player has exactly one piece left and it
     *   is not yet a rok, it must be promoted regardless of where it sits on the board.
     *
     * - **Dead piece unlock**: if the player has one or more dead cobs, any dead cob that
     *   would gain at least one move after promotion is eligible.
     *
     * Uses [normalMovesForTurn] (not [allMovesForTurn]) on the post-promotion state to
     * avoid infinite recursion.
     */
    fun getForcedPromotions(): List<Move> {
        val myPieces = cobs.entries.filter { it.value.color == currentTurn }

        // Prerequisite for both triggers: no normal moves available (patent §6.3 / §6.4).
        if (normalMovesForTurn().isNotEmpty()) return emptyList()

        // Sole remaining cob: must promote regardless of position (patent §6.4).
        // "Wherever it is positioned" means the board location does not matter —
        // not that the piece can be promoted while it still has legal moves.
        if (myPieces.size == 1 && !myPieces.first().value.isUpgraded) {
            return listOf(Move(myPieces.first().key to myPieces.first().key))
        }

        // Dead piece unlock: promote a dead cob only if doing so gains at least one move
        // (patent §6.3: "if this permits it to be moved").
        val deadCobs = getDeadCobsForCurrentTurn()
        if (deadCobs.isEmpty()) return emptyList()

        return deadCobs
            .filter { (vertex, _) ->
                applyPromotion(Move(vertex to vertex)).normalMovesForTurn().isNotEmpty()
            }
            .map { (vertex, _) -> Move(vertex to vertex) }
    }

    /**
     * Applies a forced in-place promotion: upgrades the cob at [move.from] to a rok
     * without moving it. The turn remains with the same player so they can move the
     * newly promoted rok immediately (patent §6.3: promotion replaces a normal move,
     * it does not consume the player's turn).
     */
    fun applyPromotion(move: Move): GameState {
        val mutableCobs = cobs.toMutableMap()
        val cob = mutableCobs[move.from] ?: return this
        mutableCobs[move.from] = cob.copy(isUpgraded = true)
        // Promotion resets the 50-move clock (§7.2) but keeps the current turn.
        return GameState(mutableCobs, currentTurn, halfMoveClock = 0)
    }

    fun getPieceCounts(): PieceCounts {
        val whiteCount = this.cobs.values.count { it.color == WHITE }
        val blackCount = this.cobs.values.count { it.color == BLACK }
        return PieceCounts(whiteCount, blackCount)
    }

    /**
     * Returns true if [cob] at [vertex] is dead according to the patent rules.
     *
     * A cob is dead if:
     * 1. **Primary dead**: it sits on one of the opponent's two outermost home-base vertices
     *    (D3/D4 for white, D1/D2 for black). These are only reachable via capture, never by
     *    forward movement, so no forward advance is possible from them.
     * 2. **Dead by proxy**: every forward-adjacent vertex is occupied by a dead cob of the
     *    same color. Blocked by an enemy piece, a rok of any color, or a live friendly cob
     *    does NOT make this cob dead — any of those blockers can move away.
     *
     * Roks are never dead.
     * Recursion is safe because forward moves are acyclic: proxy chains always terminate
     * at primary dead pieces.
     */
    fun isDeadCob(vertex: Vertex, cob: Cob): Boolean {
        if (cob.isUpgraded) return false
        if (vertex in deadVertices[cob.color].orEmpty()) return true

        val forwardNeighbors = adjacencyMap[vertex]?.filter { to ->
            isForwardMove(cob.color, Move(vertex to to))
        } ?: emptyList()

        // A cob with no forward neighbors is not dead by proxy — it just cannot move,
        // which the patent explicitly says does not make a piece dead by itself.
        if (forwardNeighbors.isEmpty()) return false

        return forwardNeighbors.all { to ->
            val blocker = cobs[to]
            blocker != null &&
                    !blocker.isUpgraded &&
                    blocker.color == cob.color &&
                    isDeadCob(to, blocker)
        }
    }

    /**
     * Returns all dead cobs belonging to the current turn player, paired with their vertex.
     * Used to determine eligibility for forced promotion (Fase 3).
     */
    fun getDeadCobsForCurrentTurn(): List<Pair<Vertex, Cob>> =
        cobs.entries
            .filter { (vertex, cob) -> cob.color == currentTurn && isDeadCob(vertex, cob) }
            .map { it.toPair() }

    fun detectCaptures(move: Move, newState: GameState): List<Pair<Vertex, Cob>> =
        (adjacencyMap[move.to] ?: emptyList()).mapNotNull { vertex ->
            val oldCob = cobs[vertex] ?: return@mapNotNull null
            val newCob = newState.cobs[vertex] ?: return@mapNotNull null
            (vertex to newCob).takeIf { oldCob.color != newCob.color }
        }

    fun detectUpgrades(newState: GameState): List<Pair<Vertex, Cob>> =
        newState.cobs.entries.mapNotNull { (vertex, newCob) ->
            val oldCob = cobs[vertex]
            val wasUpgraded = when {
                // In-place promotion
                oldCob != null && !oldCob.isUpgraded && newCob.isUpgraded -> true
                // Arrived via forward move onto upgrade vertex
                oldCob == null && newCob.isUpgraded ->
                    cobs.any { (fromVertex, fromCob) ->
                        fromCob.color == newCob.color &&
                                !fromCob.isUpgraded &&
                                newState.cobs[fromVertex] == null
                    }

                else -> false
            }
            (vertex to newCob).takeIf { wasUpgraded }
        }

    fun isEmptyBoard(): Boolean = this.cobs.isEmpty()

    fun isInitialState(playerSide: CobColor): Boolean = this == initialGameState(playerSide)

    fun findClosedRegions(
        to: Vertex,
        color: CobColor,
    ): List<Region> =
        vertexToRegions[to]
            ?.takeIf { it.isNotEmpty() }
            ?.filter { region ->
                region.vertices.all { vertex ->
                    cobs[vertex]?.color == color
                }
            } ?: emptyList()

    /**
     * Returns valid destination vertices for [cob] at [from] for UI highlighting.
     *
     * For normal/rok moves: forward (or any-direction for roks) empty adjacent vertices,
     * plus home-base capture targets.
     *
     * For forced promotion candidates: includes [from] itself, signalling to the UI that
     * the user should tap this vertex again to confirm the in-place promotion.
     */
    fun getValidVertex(
        from: Vertex,
        cob: Cob,
    ): List<Vertex> {
        val forwardMoves =
            adjacencyMap[from]?.filter { to ->
                !this.cobs.containsKey(to) &&
                        (cob.isUpgraded || isForwardMove(cob.color, Move(from to to)))
            } ?: emptyList()

        val homeBaseMoveTargets = getHomeBaseMoves(from, cob).map { it.to }
        val base = (forwardMoves + homeBaseMoveTargets).distinct()

        // If this piece is a forced promotion candidate, include its own vertex so the
        // highlight system shows "tap here to promote".
        val canPromote = getForcedPromotions().any { it.from == from }
        return if (canPromote) base + from else base
    }

    /**
     * Returns all legally playable non-forward moves from [from] for [cob] when it sits on
     * its own home base.
     *
     * A home-base move is valid only if it results in at least one capture. Two capture
     * mechanisms are evaluated:
     *
     * **Normal pre-adjacency captures**: an opponent's piece adjacent to the destination
     * that was NOT adjacent to the origin is captured.
     */
    fun getHomeBaseMoves(
        from: Vertex,
        cob: Cob,
    ): List<Move> {
        if (cob.isUpgraded) return emptyList()

        return getHomeBaseNonForwardMoves(cob.color, from).filter { move ->
            // Destination must be empty
            if (this.cobs[move.to] != null) return@filter false

            val originAdjacents = adjacencyMap[move.from]?.toSet() ?: emptySet()

            // Valid only if the move produces at least one capture (pre-adjacency rule)
            adjacencyMap[move.to]?.any { adj ->
                adj !in originAdjacents && this.cobs[adj]?.color == cob.color.opponent
            } ?: false
        }
    }

    fun getPossiblesVertexForCob(
        cob: Cob,
        vertex: Vertex,
    ): List<Vertex> =
        adjacencyMap[vertex]?.filter { to ->
            !cobs.containsKey(to) &&
                    (cob.isUpgraded || isForwardMove(cob.color, Move(vertex to to)))
        } ?: emptyList()

    /**
     * Applies [move] to produce the next game state.
     *
     * Capture logic by move type:
     * - **All other moves**: flips opponent pieces adjacent to the destination that were NOT
     *   adjacent to the origin (pre-adjacency rule). This covers both normal forward moves
     *   and home-base non-forward captures.
     */
    fun applyMove(move: Move): GameState {
        if (move.isPromotion()) return applyPromotion(move)

        val mutableCobs = this.cobs.toMutableMap()
        val movedCob = mutableCobs.remove(move.from) ?: return this

        val placedCob = movedCob.upgradeIfInEnemyBase(move.to)
        mutableCobs[move.to] = placedCob

        // Contar piezas adversarias antes de flipAdjacentCobs para detectar capturas.
        val opponentColor = placedCob.color.opponent
        val opponentsBefore = mutableCobs.values.count { it.color == opponentColor }
        placedCob.color.flipAdjacentCobs(mutableCobs, move.to, move.from)
        val hadCaptures = mutableCobs.values.count { it.color == opponentColor } < opponentsBefore

        // Resetear si: (a) pieza no promovida (Cob), o (b) Rok que capturó piezas.
        // Solo incrementar si el Rok se mueve sin realizar ninguna captura.
        val newClock = if (movedCob.isUpgraded && !hadCaptures) halfMoveClock + 1 else 0

        return GameState(mutableCobs, this.currentTurn.opponent, newClock)
    }

    /**
     * Función que, a partir de un GameState, genera una notación similar a FEN (Forsyth-Edwards Notation) en ajedrez.
     * Usar [POS_DELIMITER_CHAR] y [TURN_DELIMITER_CHAR] por futuros cambios.
     *
     * Cada posición ocupada en el tablero se escribe seguida por el color y el estado de la pieza:
     *    Cob: w/b - Rok: W/B,
     * y al final el indicador del turno actual (w/b) separado por un espacio.
     *
     * Ejemplo:
     *
     * ```B1B/B2B/B4B/B5w/C1B/C2B/C8B/C9b w```
     */
    fun toPositionNotation(): String =
        buildString {
            cobs.entries
                .sortedBy { it.key.name }
                .forEach { (position, piece) ->
                    val colorChar =
                        piece.color.getName().let { name ->
                            if (piece.isUpgraded) name.uppercase() else name
                        }
                    append("${position.name}$colorChar$POS_DELIMITER_CHAR")
                }

            if (cobs.isNotEmpty()) {
                setLength(length - 1)
            }

            append("$TURN_DELIMITER_CHAR${currentTurn.getName()}")
        }

    fun toMatchDto(
        moves: List<Move> = listOf(),
        result: MatchResult = UNDEFINED,
    ): MatchDto =
        MatchDto(
            header = createPGNHeader(this),
            game =
                GameDto(
                    boardPosition = this.toPositionNotation(),
                    matchResult = result,
                    moveHistory = moves,
                ),
        )

    fun getCobAtVertex(from: Vertex): Cob? = this.cobs[from]

    companion object {
        // Zobrist hashing
        // 23 vertices x 4 piece types + 1 side-to-move key = 93 Long values.
        // Piece type index: WHITE_COB=0, WHITE_ROK=1, BLACK_COB=2, BLACK_ROK=3
        // Vertex index: position in GameBoard.vertices (0..22)
        private val ZOBRIST_PIECES: Array<LongArray> =
            Array(vertices.size) { LongArray(4) { kotlin.random.Random.nextLong() } }

        private val ZOBRIST_SIDE_TO_MOVE: Long = kotlin.random.Random.nextLong()

        /** Stable vertex-to-index mapping for Zobrist key lookup (O(1)). */
        private val VERTEX_INDEX: Map<Vertex, Int> =
            vertices.mapIndexed { i, v -> v to i }.toMap()

        const val POS_DELIMITER_CHAR = '/'
        const val TURN_DELIMITER_CHAR = ' '

        fun initialGameState(currentTurn: CobColor = WHITE): GameState {
            val map =
                mapOf(
                    GameBoard.C1 to Cob(WHITE, false),
                    GameBoard.C2 to Cob(WHITE, false),
                    GameBoard.D1 to Cob(WHITE, false),
                    GameBoard.D2 to Cob(WHITE, false),
                    GameBoard.C7 to Cob(BLACK, false),
                    GameBoard.C8 to Cob(BLACK, false),
                    GameBoard.D3 to Cob(BLACK, false),
                    GameBoard.D4 to Cob(BLACK, false),
                )
            return GameState(cobs = map, currentTurn = currentTurn)
        }

        fun createGameState(block: GameStateBuilder.() -> Unit): GameState = GameStateBuilder().apply(block).build()

        fun cleanGameState(currentTurn: CobColor = WHITE): GameState =
            GameState(cobs = mapOf(), currentTurn = currentTurn)

        fun parseBoardNotation(notation: String): GameState {
            val cobs = mutableMapOf<Vertex, Cob>()

            val parts = notation.split(TURN_DELIMITER_CHAR)
            if (parts.size != 2) {
                throw IllegalArgumentException("Formato de notación inválido: debe contener piezas y turno separados por espacio")
            }

            val piecesPart = parts[0]
            val turnPart = parts[1]

            if (piecesPart.isNotEmpty()) {
                val pieceEntries = piecesPart.split(POS_DELIMITER_CHAR)
                for (entry in pieceEntries) {
                    if (entry.length < 3) continue

                    // Extraer la posición (todos los caracteres excepto el último)
                    val positionStr = entry.dropLast(1).uppercase()
                    val colorChar = entry.last()

                    val vertex =
                        vertices.find { it.name == positionStr }
                            ?: throw IllegalArgumentException("Posición inválida: $positionStr")

                    val color =
                        when (colorChar.lowercaseChar()) {
                            WHITE.getName() -> WHITE
                            BLACK.getName() -> BLACK
                            else -> throw IllegalArgumentException("Color inválido: $colorChar")
                        }

                    val isUpgraded = colorChar.isUpperCase()

                    cobs[vertex] = Cob(color, isUpgraded)
                }
            }

            val currentTurn =
                when (turnPart.lowercase()) {
                    "white", "w" -> WHITE
                    "black", "b" -> BLACK
                    else -> throw IllegalArgumentException("Turno inválido: $turnPart")
                }

            return GameState(cobs, currentTurn)
        }

        /**
         * Reconstruye el [GameState] después de aplicar los primeros
         * [moveIndex]+1 movimientos sobre [initialState].
         *
         * @param initialState Estado del tablero antes del primer movimiento.
         *   Por defecto [initialGameState] para compatibilidad con partidas
         *   que no persisten la posición inicial.
         */
        fun getBoardStateAtMove(
            moveHistory: List<Move>,
            moveIndex: Int? = null,
            initialState: GameState = initialGameState(),
        ): GameState {
            val moveIndex = moveIndex ?: moveHistory.lastIndex
            var currentState = initialState

            val movesToApply = moveHistory.take(moveIndex + 1)
            movesToApply.forEach { move ->
                currentState = currentState.applyMove(move)
            }

            return currentState
        }
    }
}