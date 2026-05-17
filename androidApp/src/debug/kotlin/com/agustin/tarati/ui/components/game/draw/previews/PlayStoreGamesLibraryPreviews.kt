package com.agustin.tarati.ui.components.game.draw.previews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.repositories.SavedGame
import com.agustin.tarati.features.library.GamesLibraryScreen
import com.agustin.tarati.features.library.IGamesLibraryViewModel
import com.agustin.tarati.features.library.previews.PreviewContainer
import com.agustin.tarati.features.library.previews.previewMatchDto
import com.agustin.tarati.features.library.previews.previewSavedGames
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.TOP
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// 06 — Biblioteca de partidas guardadas             TextPosition.TOP
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun rememberLibraryVm(): IGamesLibraryViewModel = remember {
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
private fun GamesLibraryStoreScreenshotContent(palette: BoardPalette) {
    PreviewContainer(palette = palette) {
        GamesLibraryScreen(viewModel = rememberLibraryVm())
    }
}

// ── ES ────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_ES_Classic() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Guardá tus partidas",
            subtitle = "Biblioteca completa con historial,\nposiciones y resultados",
            textPosition = TOP, palette = ClassicPalette,
        ) { GamesLibraryStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_ES_Dark() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Guardá tus partidas",
            subtitle = "Biblioteca completa con historial,\nposiciones y resultados",
            textPosition = TOP, palette = DarkPalette,
        ) { GamesLibraryStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_ES_Nature() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Guardá tus partidas",
            subtitle = "Biblioteca completa con historial,\nposiciones y resultados",
            textPosition = TOP, palette = NaturePalette,
        ) { GamesLibraryStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_ES", locale = "es", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_ES_Grayscale() {
    WithLocale(Locale.forLanguageTag("es")) {
        PlayStoreComposeScreenshot(
            title = "Guardá tus partidas",
            subtitle = "Biblioteca completa con historial,\nposiciones y resultados",
            textPosition = TOP, palette = GrayscalePalette,
        ) { GamesLibraryStoreScreenshotContent(GrayscalePalette) }
    }
}

// ── EN ────────────────────────────────────────────────────────────────────────

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_EN_Classic() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Save your games",
            subtitle = "Full library with move history,\npositions and results",
            textPosition = TOP, palette = ClassicPalette,
        ) { GamesLibraryStoreScreenshotContent(ClassicPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_EN_Dark() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Save your games",
            subtitle = "Full library with move history,\npositions and results",
            textPosition = TOP, palette = DarkPalette,
        ) { GamesLibraryStoreScreenshotContent(DarkPalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_EN_Nature() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Save your games",
            subtitle = "Full library with move history,\npositions and results",
            textPosition = TOP, palette = NaturePalette,
        ) { GamesLibraryStoreScreenshotContent(NaturePalette) }
    }
}

@Preview(group = "PlayStore_EN", showBackground = false, widthDp = STORE_W_DP, heightDp = STORE_H_DP)
@Composable
fun StoreGamesLibrary06_EN_Grayscale() {
    WithLocale(Locale.ENGLISH) {
        PlayStoreComposeScreenshot(
            title = "Save your games",
            subtitle = "Full library with move history,\npositions and results",
            textPosition = TOP, palette = GrayscalePalette,
        ) { GamesLibraryStoreScreenshotContent(GrayscalePalette) }
    }
}