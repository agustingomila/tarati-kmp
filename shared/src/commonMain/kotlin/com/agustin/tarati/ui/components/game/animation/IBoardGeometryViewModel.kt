package com.agustin.tarati.ui.components.game.animation

import androidx.compose.ui.geometry.Size
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.VisualPositionCache
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato público de la geometría del tablero.
 *
 * Separa la responsabilidad de mantener el tamaño del contenedor y la
 * orientación del tablero del pipeline de animaciones.
 *
 * ## Por qué separar
 * [boardSize] y [positionCache] son datos de layout que consumen tanto el
 * [BoardRenderer]
 * (para dibujar piezas y highlights) como el [BoardAnimationViewModel]
 * (para interpolar posiciones durante animaciones).
 * No tienen ninguna dependencia con la lógica de animación — se actualizan
 * desde eventos de layout de Compose y son completamente independientes del
 * [moveChannel] y del estado visual.
 *
 * ## Flujo de actualización
 * ```
 * BoardRenderer.onGloballyPositioned → onBoardSizeChange
 *                                          ↓
 *                                  BoardGeometryViewModel.updateBoardSize
 *                                          ↓
 *                              _positionCache = buildPositionCache(size, orientation)
 *                                          ↓
 *                      BoardAnimationViewModel._positionCache[vertex]  (animaciones,
 *                          actualizado en paralelo vía onBoardSizeChange/Orientation)
 *                      BoardRenderer.positionCache (remember → reconstruye en cambios)
 * ```
 */
interface IBoardGeometryViewModel {

    /** Tamaño actual del contenedor del tablero en píxeles. */
    val boardSize: StateFlow<Size>

    /** Caché de posiciones de pantalla precalculadas para los 23 vértices. */
    val positionCache: VisualPositionCache

    /**
     * Actualiza el tamaño del contenedor y reconstruye [positionCache].
     * Llamado desde
     * [BoardRenderEvents.onBoardSizeChange].
     */
    fun updateBoardSize(size: Size)

    /**
     * Actualiza la orientación del tablero y reconstruye [positionCache].
     * Llamado desde
     * [BoardRenderEvents.onUpdateBoardOrientation].
     */
    fun updateBoardOrientation(orientation: BoardOrientation)
}