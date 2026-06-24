package com.agustin.tarati.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf

/**
 * Global singleton that holds the currently active [BoardPalette].
 *
 * ## Why a singleton
 * Palette changes can be triggered from multiple entry points — user selection in
 * Settings, seasonal auto-apply, and special event unlocks — none of which share
 * a common ViewModel scope. A singleton provides a single source of truth that
 * any of these entry points can write to, with Compose observing the change via
 * the `mutableStateOf` backing property.
 *
 * ## Production data flow
 * ```
 * setPalette() → _currentPalette (mutableStateOf)
 *   → TaratiTheme reads currentPalette → provides LocalBoardPalette
 *     → every composable reading LocalBoardPalette.current recomposes
 * ```
 *
 * ## Test risk and mitigation
 * Because `_currentPalette` is `mutableStateOf` inside an `object`, its value
 * persists across tests in the same JVM process. A test that calls `setPalette()`
 * will leave state that bleeds into subsequent tests unless explicitly reset.
 *
 * **Mitigation:** components that call `setPalette()` — currently only
 * [SpecialEventManager] — accept a
 * `paletteApplier: (BoardPalette) -> Unit` parameter that defaults to
 * `PaletteManager.setPalette`. Tests inject a lambda that records the call
 * without touching the singleton, keeping the global state clean.
 *
 * If additional callers are introduced, they should follow the same pattern
 * rather than calling `PaletteManager.setPalette()` directly, to preserve
 * testability.
 */
object PaletteManager {
    private val _currentPalette = mutableStateOf<BoardPalette>(ClassicPalette)
    val currentPalette: BoardPalette get() = _currentPalette.value

    fun setPalette(palette: BoardPalette) {
        _currentPalette.value = palette
    }
}

/**
 * CompositionLocal que transporta la [BoardPalette] activa por el árbol de
 * composición.
 *
 * ## Flujo en producción
 * `TaratiTheme` provee `LocalBoardPalette` con `PaletteManager.currentPalette`.
 * Como `PaletteManager.currentPalette` es un `State`, cualquier cambio de paleta
 * del usuario reactualiza la provisión y todo composable que lea
 * `LocalBoardPalette.current` se recompone automáticamente.
 *
 * ## Flujo en previews
 * `TaratiTheme(palette = X)` provee `LocalBoardPalette` con `X` directamente,
 * sin leer del `PaletteManager`. Los previews quedan completamente aislados entre
 * sí: cambiar la paleta en uno no invalida los demás.
 *
 * El valor por defecto (`ClassicPalette`) solo se usa si se llama a un composable
 * que dependa de `LocalBoardPalette.current` fuera de cualquier `TaratiTheme`,
 * lo cual no debería ocurrir en producción.
 */
val LocalBoardPalette: ProvidableCompositionLocal<BoardPalette> = compositionLocalOf<BoardPalette> { ClassicPalette }

/**
 * Construye un [BoardColors] a partir de la paleta actual en la jerarquía
 * de composición ([LocalBoardPalette]).
 *
 * Al leer `LocalBoardPalette.current` en lugar de `PaletteManager.currentPalette`
 * directamente, este composable recibe la paleta correcta tanto en producción
 * (provista por `TaratiTheme` desde el singleton) como en previews (provista por
 * `TaratiTheme(palette = X)` de forma aislada).
 */
@Composable
fun rememberBoardColors(): BoardColors {
    val palette = LocalBoardPalette.current
    return BoardColors(
        blackCobBorderColor = palette.blackCobBorderColor,
        blackCobColor = palette.blackCobColor,
        boardBackground = palette.boardBackground,
        boardEdgeColor = palette.boardEdgeColor,
        boardPerimeterColor = palette.boardPerimeterColor,
        boardPatternBorderColor = palette.boardPatternBorderColor,
        boardPatternColor1 = palette.boardPatternColor1,
        boardPatternColor2 = palette.boardPatternColor2,
        boardPatternColor3 = palette.boardPatternColor3,
        boardVertexColor = palette.boardVertexColor,
        neutralColor = palette.neutralColor,
        selectionIndicatorColor = palette.selectionIndicatorColor,
        textColor = palette.textColor,
        vertexAdjacentColor = palette.vertexAdjacentColor,
        vertexOccupiedColor = palette.vertexOccupiedColor,
        vertexSelectedColor = palette.vertexSelectedColor,
        whiteCobBorderColor = palette.whiteCobBorderColor,
        whiteCobColor = palette.whiteCobColor,
        whiteCobLightColor = palette.whiteCobLightColor,
        blackCobLightColor = palette.blackCobLightColor,
        whiteCobShadowColor = palette.whiteCobShadowColor,
        blackCobShadowColor = palette.blackCobShadowColor,
        whiteCobSelectColor = palette.whiteCobSelectColor,
        blackCobSelectColor = palette.blackCobSelectColor,
        highlightEdge1Color = palette.highlightEdge1Color,
        highlightEdge2Color = palette.highlightEdge2Color,
        highlightEdge3Color = palette.highlightEdge3Color,
        highlightVertexCapture1Color = palette.highlightVertexCapture1Color,
        highlightVertexCapture2Color = palette.highlightVertexCapture2Color,
        highlightVertexCapture3Color = palette.highlightVertexCapture3Color,
        highlightVertexAdjacent1Color = palette.highlightVertexAdjacent1Color,
        highlightVertexAdjacent2Color = palette.highlightVertexAdjacent2Color,
        highlightVertexAdjacent3Color = palette.highlightVertexAdjacent3Color,
        highlightVertexUpgrade1Color = palette.highlightVertexUpgrade1Color,
        highlightVertexUpgrade2Color = palette.highlightVertexUpgrade2Color,
        highlightVertexUpgrade3Color = palette.highlightVertexUpgrade3Color,
        highlightRegion1Color = palette.highlightRegion1Color,
        highlightRegion2Color = palette.highlightRegion2Color,
        highlightRegion3Color = palette.highlightRegion3Color,
        whiteUpgradingWaveColor = palette.whiteUpgradingWaveColor,
        whiteConvertingWaveColor = palette.whiteConvertingWaveColor,
        blackUpgradingWaveColor = palette.blackUpgradingWaveColor,
        blackConvertingWaveColor = palette.blackConvertingWaveColor,
    )
}