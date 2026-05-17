package com.agustin.tarati.features.library.previews

import com.agustin.tarati.core.data.database.dto.GameDto
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.PGNHeader
import com.agustin.tarati.core.data.repositories.SavedGame
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.B2
import com.agustin.tarati.core.domain.game.board.GameBoard.B3
import com.agustin.tarati.core.domain.game.board.GameBoard.B4
import com.agustin.tarati.core.domain.game.board.GameBoard.B5
import com.agustin.tarati.core.domain.game.board.GameBoard.B6
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C11
import com.agustin.tarati.core.domain.game.board.GameBoard.C12
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C3
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.C8
import com.agustin.tarati.core.domain.game.board.GameBoard.C9
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D2
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.core.domain.game.play.GameState.Companion.parseBoardNotation
import com.agustin.tarati.core.domain.game.play.HistoryEntry
import com.agustin.tarati.core.domain.game.play.MatchResult
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.features.library.IGamesLibraryViewModel
import com.agustin.tarati.features.library.previews.PreviewGamesLibrary.savedGames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación fake de [IGamesLibraryViewModel] para previews de Compose.
 *
 * ## Por qué StateFlow y no Flow.map
 * `collectAsState()` tiene dos sobrecargas:
 * - `Flow<T>.collectAsState(initial: T)` — devuelve `initial` en la primera
 *   composición y actualiza el estado en una corutina. En previews, esa corutina
 *   puede no ejecutarse antes del primer render → pantalla vacía.
 * - `StateFlow<T>.collectAsState()` — lee `.value` de forma **síncrona** en la
 *   primera composición, sin necesitar ninguna corutina. Los datos aparecen
 *   desde el primer frame.
 *
 * Al declarar [savedGames] como [StateFlow] (satisfaciendo la interfaz, que
 * también exige [StateFlow]) y respaldarlo con un [MutableStateFlow] con datos
 * iniciales, los previews muestran la lista completa sin necesitar efectos.
 */
object PreviewGamesLibrary : IGamesLibraryViewModel {

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _savedGames = MutableStateFlow(previewSavedGames)
    override val savedGames: StateFlow<List<SavedGame>> = _savedGames.asStateFlow()

    override fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _savedGames.value =
            if (query.isBlank()) {
                previewSavedGames
            } else {
                val q = query.trim().lowercase()
                previewSavedGames.filter { game ->
                    game.whitePlayer.lowercase().contains(q) ||
                            game.blackPlayer.lowercase().contains(q) ||
                            game.result.lowercase().contains(q) ||
                            game.date.lowercase().contains(q)
                }
            }
    }

    override fun deleteGame(gameId: String) {
        _savedGames.value = _savedGames.value.filter { it.id != gameId }
    }

    override fun loadGame(gameId: String): Flow<MatchDto?> = flowOf(previewMatchDto)

    override fun saveCurrentGame(match: MatchDto) {
        // No-op para preview
    }
}

// ── Datos de preview ──────────────────────────────────────────────────────────

val previewGameState = parseBoardNotation(previewBoardPosition)

val previewSavedGames =
    listOf(
        SavedGame(
            id = "1",
            whitePlayer = "Gómez, Juan",
            blackPlayer = "Smith, John",
            result = "1-0",
            date = "2024.03.15",
            moveCount = 40,
        ),
        SavedGame(
            id = "2",
            whitePlayer = "López, Carlos",
            blackPlayer = "Johnson, Carl",
            result = "1-0",
            date = "2024.04.02",
            moveCount = 60,
        ),
        SavedGame(
            id = "3",
            whitePlayer = "Pérez, Luis",
            blackPlayer = "Robertson, Louis",
            result = "0-1",
            date = "2024.04.10",
            moveCount = 35,
        ),
        SavedGame(
            id = "4",
            whitePlayer = "Fernández, Ana",
            blackPlayer = "García, Miguel",
            result = "½-½",
            date = "2024.04.18",
            moveCount = 52,
        ),
        SavedGame(
            id = "5",
            whitePlayer = "Martínez, Pedro",
            blackPlayer = "Williams, Kate",
            result = "0-1",
            date = "2024.04.22",
            moveCount = 28,
        ),
    )

val previewHeader =
    PGNHeader(
        event = "Tarati World Championship",
        site = "Buenos Aires",
        date = "2024.03.15",
        round = "1",
        white = "Gómez, Juan",
        black = "Smith, John",
        result = "1-0",
        gameType = "Classical",
        rules = "Tarati",
        timeControl = "Unlimited",
        termination = "Normal",
    )

val previewMoveList: List<Move> =
    listOf(
        Move(C2 to B1),
        Move(C8 to C9),
        Move(D2 to C2),
        Move(C7 to B4),
        Move(C1 to C12),
        Move(D3 to C7),
        Move(D1 to C1),
        Move(D4 to C8),
        Move(B1 to A1),
        Move(C9 to B5),
        Move(C12 to B6),
        Move(C8 to C9),
        Move(A1 to B3),
        Move(B5 to A1),
        Move(B6 to B5),
        Move(A1 to B1),
        Move(C9 to C8),
        Move(C1 to D1),
        Move(C7 to D3),
        Move(B3 to A1),
        Move(B4 to C7),
        Move(B1 to C1),
        Move(C7 to B4),
        Move(C1 to B1),
        Move(B4 to B3),
        Move(B1 to C1),
        Move(C8 to B4),
        Move(C1 to C12),
        Move(B5 to C9),
        Move(D1 to C1),
        Move(C9 to C8),
        Move(C2 to D2),
        Move(A1 to B5),
        Move(C12 to B6),
        Move(B4 to A1),
        Move(C1 to B1),
        Move(B3 to B2),
        Move(D2 to C2),
        Move(C8 to B4),
        Move(B6 to C12),
        Move(B2 to C3),
        Move(B1 to B2),
        Move(B4 to B3),
        Move(C12 to B6),
        Move(C2 to B1),
        Move(C3 to C2),
        Move(B3 to B4),
        Move(C2 to C1),
        Move(B6 to C11),
        Move(C1 to C12),
        Move(A1 to B6),
        Move(B1 to A1),
        Move(B2 to B3),
        Move(A1 to B2),
        Move(B4 to C7),
        Move(B5 to A1),
        Move(C7 to B4),
        Move(B2 to B1),
        Move(B3 to B2),
        Move(B6 to B5),
        Move(C11 to B6),
        Move(B4 to C7),
        Move(B5 to B4),
        Move(D3 to D4),
        Move(B4 to C8),
    )

val exampleMoveHistory: List<HistoryEntry> = previewMoveList.mapIndexed { index, move ->
    val color = if (index % 2 == 0) WHITE else BLACK
    HistoryEntry(move, GameState(mapOf(), color))
}

const val previewBoardPosition = "B2B/B3W/B4W/B5b/B6W/C11W/C12W/D3W w"

val previewGameDto =
    GameDto(
        boardPosition = previewBoardPosition,
        matchResult = MatchResult.WHITE_WON,
        moveHistory = previewMoveList,
    )

val previewMatchDto =
    MatchDto(
        header = previewHeader,
        game = previewGameDto,
    )