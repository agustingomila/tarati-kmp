package com.agustin.tarati.features.library.previews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.repositories.SavedGame
import com.agustin.tarati.core.domain.game.board.GameBoard.B1
import com.agustin.tarati.core.domain.game.board.GameBoard.C1
import com.agustin.tarati.core.domain.game.board.GameBoard.C12
import com.agustin.tarati.core.domain.game.board.GameBoard.C2
import com.agustin.tarati.core.domain.game.board.GameBoard.C5
import com.agustin.tarati.core.domain.game.board.GameBoard.C6
import com.agustin.tarati.core.domain.game.board.GameBoard.C7
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.play.Move
import com.agustin.tarati.features.detail.MoveRow
import com.agustin.tarati.features.library.EmptySavedGames
import com.agustin.tarati.features.library.GameInfoRow
import com.agustin.tarati.features.library.GamesLibraryScreen
import com.agustin.tarati.features.library.IGamesLibraryViewModel
import com.agustin.tarati.features.library.SavedGameItem
import com.agustin.tarati.features.library.StaticBoardRenderer
import com.agustin.tarati.ui.theme.ChristmasPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.HalloweenPalette
import com.agustin.tarati.ui.theme.NaturePalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

// ── Factories de ViewModels de preview ────────────────────────────────────────

@Composable
private fun rememberFullListVm(): IGamesLibraryViewModel =
    remember {
        object : IGamesLibraryViewModel {
            override val searchQuery: StateFlow<String> = MutableStateFlow("")
            override val savedGames: StateFlow<List<SavedGame>> = MutableStateFlow(previewSavedGames)
            override fun setSearchQuery(query: String) {}
            override fun deleteGame(gameId: String) {}
            override fun loadGame(gameId: String): Flow<MatchDto?> = flowOf(previewMatchDto)
            override fun saveCurrentGame(match: MatchDto) {}
        }
    }

@Composable
private fun rememberEmptyVm(): IGamesLibraryViewModel =
    remember {
        object : IGamesLibraryViewModel {
            override val searchQuery: StateFlow<String> = MutableStateFlow("")
            override val savedGames: StateFlow<List<SavedGame>> = MutableStateFlow(emptyList())
            override fun setSearchQuery(query: String) {}
            override fun deleteGame(gameId: String) {}
            override fun loadGame(gameId: String): Flow<MatchDto?> = flowOf(previewMatchDto)
            override fun saveCurrentGame(match: MatchDto) {}
        }
    }

@Composable
private fun rememberSearchVm(query: String): IGamesLibraryViewModel {
    val q = query.lowercase()
    val filtered = previewSavedGames.filter { game ->
        game.whitePlayer.lowercase().contains(q) ||
                game.blackPlayer.lowercase().contains(q) ||
                game.result.lowercase().contains(q) ||
                game.date.lowercase().contains(q)
    }
    return remember(query) {
        object : IGamesLibraryViewModel {
            override val searchQuery: StateFlow<String> = MutableStateFlow(query)
            override val savedGames: StateFlow<List<SavedGame>> = MutableStateFlow(filtered)
            override fun setSearchQuery(query: String) {}
            override fun deleteGame(gameId: String) {}
            override fun loadGame(gameId: String): Flow<MatchDto?> = flowOf(previewMatchDto)
            override fun saveCurrentGame(match: MatchDto) {}
        }
    }
}

@Composable
private fun rememberNoResultsVm(query: String = "xyzxyz"): IGamesLibraryViewModel =
    remember(query) {
        object : IGamesLibraryViewModel {
            override val searchQuery: StateFlow<String> = MutableStateFlow(query)
            override val savedGames: StateFlow<List<SavedGame>> = MutableStateFlow(emptyList())
            override fun setSearchQuery(query: String) {}
            override fun deleteGame(gameId: String) {}
            override fun loadGame(gameId: String): Flow<MatchDto?> = flowOf(previewMatchDto)
            override fun saveCurrentGame(match: MatchDto) {}
        }
    }

// ═════════════════════════════════════════════════════════════════════════════
// Pantalla completa — GamesLibraryScreen
// ═════════════════════════════════════════════════════════════════════════════

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Classic Light",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_ClassicLight() {
    val vm = rememberFullListVm()
    PreviewContainer(ClassicPalette) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Classic Dark",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_ClassicDark() {
    val vm = rememberFullListVm()
    PreviewContainer(
        ClassicPalette,
        darkTheme = true
    ) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Nature",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_Nature() {
    val vm = rememberFullListVm()
    PreviewContainer(NaturePalette) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Dark Palette",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_DarkPalette() {
    val vm = rememberFullListVm()
    PreviewContainer(
        DarkPalette,
        darkTheme = true
    ) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Halloween",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_Halloween() {
    val vm = rememberFullListVm()
    PreviewContainer(
        HalloweenPalette,
        darkTheme = true
    ) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Christmas",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_Christmas() {
    val vm = rememberFullListVm()
    PreviewContainer(ChristmasPalette) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(showBackground = true, showSystemUi = true, name = "Library — Vacía", device = "spec:width=411dp,height=891dp")
@Composable
fun GamesLibraryScreenPreview_Empty() {
    val vm = rememberEmptyVm()
    PreviewContainer(ClassicPalette) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Buscando «Gómez»",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_WithSearch() {
    val vm = rememberSearchVm("Gómez")
    PreviewContainer(ClassicPalette) { GamesLibraryScreen(viewModel = vm) }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Library — Sin resultados",
    device = "spec:width=411dp,height=891dp"
)
@Composable
fun GamesLibraryScreenPreview_NoResults() {
    val vm = rememberNoResultsVm()
    PreviewContainer(ClassicPalette) { GamesLibraryScreen(viewModel = vm) }
}

// ═════════════════════════════════════════════════════════════════════════════
// Componentes individuales
// ═════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "Empty — Classic Light")
@Composable
fun PreviewEmptySavedGames_Light() {
    PreviewContainer(ClassicPalette) { EmptySavedGames() }
}

@Preview(showBackground = true, name = "Empty — Classic Dark")
@Composable
fun PreviewEmptySavedGames_Dark() {
    PreviewContainer(ClassicPalette, darkTheme = true) { EmptySavedGames() }
}

@Preview(showBackground = true, widthDp = 411, name = "SavedGameItem — Classic Light")
@Composable
fun PreviewSavedGameItem_ClassicLight() {
    PreviewContainer(ClassicPalette) {
        SavedGameItem(savedGame = previewSavedGames.first(), onLoadGame = PreviewGamesLibrary::loadGame)
    }
}

@Preview(showBackground = true, widthDp = 411, name = "SavedGameItem — Classic Dark")
@Composable
fun PreviewSavedGameItem_ClassicDark() {
    PreviewContainer(ClassicPalette, darkTheme = true) {
        SavedGameItem(savedGame = previewSavedGames.first(), onLoadGame = PreviewGamesLibrary::loadGame)
    }
}

@Preview(showBackground = true, widthDp = 411, name = "SavedGameItem — Nature")
@Composable
fun PreviewSavedGameItem_Nature() {
    PreviewContainer(NaturePalette) {
        SavedGameItem(savedGame = previewSavedGames[1], onLoadGame = PreviewGamesLibrary::loadGame)
    }
}

@Preview(showBackground = true, widthDp = 411, name = "SavedGameItem — Halloween")
@Composable
fun PreviewSavedGameItem_Halloween() {
    PreviewContainer(HalloweenPalette, darkTheme = true) {
        SavedGameItem(savedGame = previewSavedGames[2], isSelected = true, onLoadGame = PreviewGamesLibrary::loadGame)
    }
}

@Preview(showBackground = true, widthDp = 411, name = "SavedGameItem — Multiselect")
@Composable
fun PreviewSavedGameItem_Multiselect() {
    PreviewContainer(ClassicPalette) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SavedGameItem(
                savedGame = previewSavedGames[0],
                isSelected = true,
                isMultiSelectMode = true,
                onLoadGame = PreviewGamesLibrary::loadGame
            )
            SavedGameItem(
                savedGame = previewSavedGames[1],
                isSelected = false,
                isMultiSelectMode = true,
                onLoadGame = PreviewGamesLibrary::loadGame
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300, name = "Board — Classic")
@Composable
fun PreviewStaticBoardRenderer_Classic() {
    PreviewContainer(ClassicPalette) {
        StaticBoardRenderer(
            modifier = Modifier.size(220.dp),
            gameState = previewGameState
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300, name = "Board — Dark")
@Composable
fun PreviewStaticBoardRenderer_Dark() {
    PreviewContainer(DarkPalette, darkTheme = true) {
        StaticBoardRenderer(
            modifier = Modifier.size(220.dp),
            gameState = previewGameState
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300, name = "Board — Nature")
@Composable
fun PreviewStaticBoardRenderer_Nature() {
    PreviewContainer(NaturePalette) {
        StaticBoardRenderer(
            modifier = Modifier.size(220.dp),
            gameState = previewGameState
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300, name = "Board — Grayscale")
@Composable
fun PreviewStaticBoardRenderer_Grayscale() {
    PreviewContainer(GrayscalePalette) {
        StaticBoardRenderer(
            modifier = Modifier.size(220.dp),
            gameState = previewGameState
        )
    }
}

@Preview(showBackground = true, widthDp = 360, name = "GameInfoRow — Classic Light")
@Composable
fun GameInfoRowPreview_Light() {
    PreviewContainer(ClassicPalette) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GameInfoRow(label = "Jugadores", value = "Gómez vs Smith")
            GameInfoRow(label = "Resultado", value = "1-0")
            GameInfoRow(label = "Fecha", value = "2024.03.15")
            GameInfoRow(label = "Movimientos", value = "40")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "GameInfoRow — Classic Dark")
@Composable
fun GameInfoRowPreview_Dark() {
    PreviewContainer(ClassicPalette, darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GameInfoRow(label = "Jugadores", value = "Gómez vs Smith")
            GameInfoRow(label = "Resultado", value = "1-0")
            GameInfoRow(label = "Fecha", value = "2024.03.15")
            GameInfoRow(label = "Movimientos", value = "40")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "MoveRow — Classic Light")
@Composable
fun MoveRowPreview_Light() {
    PreviewContainer(ClassicPalette) {
        Column {
            MoveRow(
                moveNumber = 1,
                whiteMove = Move(C2 to B1).name,
                blackMove = Move(C7 to C6).name,
                isEven = true,
                whiteIsCurrent = true
            )
            MoveRow(moveNumber = 2, whiteMove = Move(C1 to C12).name, blackMove = Move(D3 to C7).name, isEven = false)
            MoveRow(
                moveNumber = 3,
                whiteMove = Move(D1 to C1).name,
                blackMove = Move(C6 to C5).name,
                isEven = true,
                blackIsCurrent = true
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "MoveRow — Classic Dark")
@Composable
fun MoveRowPreview_Dark() {
    PreviewContainer(ClassicPalette, darkTheme = true) {
        Column {
            MoveRow(
                moveNumber = 1,
                whiteMove = Move(C2 to B1).name,
                blackMove = Move(C7 to C6).name,
                isEven = true,
                whiteIsCurrent = true
            )
            MoveRow(moveNumber = 2, whiteMove = Move(C1 to C12).name, blackMove = Move(D3 to C7).name, isEven = false)
            MoveRow(moveNumber = 3, whiteMove = Move(D1 to C1).name, blackMove = Move(C6 to C5).name, isEven = true)
        }
    }
}