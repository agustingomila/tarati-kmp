package com.agustin.tarati.ui.components.game.animation

import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.VisualPositionCache
import com.agustin.tarati.core.domain.game.board.buildPositionCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Mantiene el tamaño del contenedor del tablero, la orientación, y el caché
 * de posiciones de pantalla ([GameBoard.VisualPositionCache]).
 *
 * Extraído de [BoardAnimationViewModel] para separar la responsabilidad de
 * geometría del pipeline de animaciones. Ver [IBoardGeometryViewModel].
 *
 * ## Thread safety
 * [_positionCache] es una `var` escrita desde el hilo principal (callbacks de
 * layout de Compose). No necesita sincronización adicional porque Compose
 * garantiza que `onGloballyPositioned` y `onSizeChanged` se ejecutan en el
 * hilo principal.
 */
class BoardGeometryViewModel : ViewModel(), IBoardGeometryViewModel {

    private val _boardOrientation = MutableStateFlow(BoardOrientation.PORTRAIT_WHITE)

    private val _boardSize = MutableStateFlow(Size.Zero)
    override val boardSize: StateFlow<Size> = _boardSize.asStateFlow()

    private var _positionCache: VisualPositionCache =
        buildPositionCache(Size.Zero, BoardOrientation.PORTRAIT_WHITE)
    override val positionCache: VisualPositionCache
        get() = _positionCache

    override fun updateBoardSize(size: Size) {
        _boardSize.update { size }
        _positionCache = buildPositionCache(size, _boardOrientation.value)
    }

    override fun updateBoardOrientation(orientation: BoardOrientation) {
        _boardOrientation.update { orientation }
        _positionCache = buildPositionCache(_boardSize.value, orientation)
    }
}