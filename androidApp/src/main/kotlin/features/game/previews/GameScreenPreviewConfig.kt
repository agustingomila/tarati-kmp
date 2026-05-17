package com.agustin.tarati.features.game.previews

import androidx.compose.material3.DrawerValue
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.GameState
import com.agustin.tarati.features.settings.BoardVisualState
import com.agustin.tarati.ui.components.sidebar.SidebarUIState
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette

/**
 * Configuración inmutable para los previews de [GameScreen].
 *
 * @param gameState Posición a mostrar en el tablero. Si es null se usa la posición
 *                  inicial del [GameManagerState]
 *                  ([GameState.initialGameState]).
 *                  Pasá un [GameState] construido manualmente para mostrar posiciones
 *                  específicas de media partida en los previews.
 */
data class GameScreenPreviewConfig(
    val darkTheme: Boolean = false,
    val palette: BoardPalette = ClassicPalette,
    val drawerStateValue: DrawerValue = DrawerValue.Closed,
    val playerSide: CobColor = CobColor.WHITE,
    val landScape: Boolean = false,
    val isEditing: Boolean = false,
    val isTutorialActive: Boolean = false,
    val boardVisualState: BoardVisualState = BoardVisualState(),
    val gameState: GameState? = null,
    val sidebarUIState: SidebarUIState = SidebarUIState(),
    val historyPanelOpen: Boolean = false,
)