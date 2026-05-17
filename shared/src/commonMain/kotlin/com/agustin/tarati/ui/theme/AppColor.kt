package com.agustin.tarati.ui.theme

import androidx.compose.runtime.Composable

/**
 * Devuelve los colores del tablero correspondientes a la paleta activa en la
 * jerarquía de composición actual.
 *
 * Delega a [rememberBoardColors], que lee [LocalBoardPalette.current] en lugar
 * del `PaletteManager` directamente. La paleta correcta llega hasta aquí
 * gracias a que [TaratiTheme] provee [LocalBoardPalette] con la paleta resuelta
 * — ya sea desde `PaletteManager` (producción) o desde un parámetro explícito
 * (previews aislados).
 */
@Composable
fun getBoardColors(): BoardColors = rememberBoardColors()

/**
 * Devuelve los colores del tablero para una [palette] específica, sin modificar
 * el [PaletteManager] global.
 *
 * Esta sobrecarga está destinada a sitios que necesitan los colores de una paleta
 * concreta fuera del flujo normal de `TaratiTheme` (p. ej. cálculos offline).
 * Para uso en composables dentro de `TaratiTheme`, preferir [getBoardColors]
 * sin parámetros, que lee la paleta del [LocalBoardPalette] activo.
 */
@Composable
fun getBoardColors(palette: BoardPalette): BoardColors {
    // Delega a rememberBoardColors construyendo manualmente el BoardColors
    // desde la paleta explícita, sin tocar PaletteManager.
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