package com.agustin.tarati.game.logic

import androidx.compose.ui.geometry.Size
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.D1
import com.agustin.tarati.core.domain.game.board.GameBoard.D2
import com.agustin.tarati.core.domain.game.board.GameBoard.D3
import com.agustin.tarati.core.domain.game.board.GameBoard.D4
import com.agustin.tarati.core.domain.game.board.GameBoard.vertices
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardVisualPositionTest {
    @Test
    fun getVisualPosition_handlesExtendedCoordinates() {
        val size = Size(500f, 500f)
        val orientation = BoardOrientation.PORTRAIT_WHITE

        // Verificar que todas las posiciones visuales sean calculadas correctamente
        // incluso para bases con coordenadas extendidas
        vertices.forEach { vertex ->
            val visualPosition =
                getVisualPosition(
                    vertex,
                    size,
                    orientation,
                )

            // Las bases pueden estar fuera del área central, pero deberían ser posiciones válidas
            assertFalse("Vertex $vertex X should not be NaN", visualPosition.x.isNaN())
            assertFalse("Vertex $vertex Y should not be NaN", visualPosition.y.isNaN())
            assertFalse("Vertex $vertex X should not be infinite", visualPosition.x.isInfinite())
            assertFalse("Vertex $vertex Y should not be infinite", visualPosition.y.isInfinite())

            // Aunque algunas bases puedan estar fuera del canvas, deberían ser posiciones razonables
            val reasonableRange = -100f..(size.width + 100f)
            assertTrue(
                "Vertex $vertex X should be in reasonable range, but was ${visualPosition.x}",
                visualPosition.x in reasonableRange,
            )
            assertTrue(
                "Vertex $vertex Y should be in reasonable range, but was ${visualPosition.y}",
                visualPosition.y in reasonableRange,
            )
        }
    }

    @Test
    fun getVisualPositionPortraitWhite_basesPositionedCorrectly() {
        val size = Size(500f, 500f)
        val orientation = BoardOrientation.PORTRAIT_WHITE

        val d1 = getVisualPosition(D1, size, orientation)
        val d2 = getVisualPosition(D2, size, orientation)
        val d3 = getVisualPosition(D3, size, orientation)
        val d4 = getVisualPosition(D4, size, orientation)

        // En orientación PORTRAIT_WHITE, D1 y D2 deberían estar abajo, D3 y D4 arriba
        assertTrue("D1 should be below center", d1.y > size.height / 2)
        assertTrue("D2 should be below center", d2.y > size.height / 2)
        assertTrue("D3 should be above center", d3.y < size.height / 2)
        assertTrue("D4 should be above center", d4.y < size.height / 2)
    }

    @Test
    fun getVisualPositionPortraitBlack_basesPositionedCorrectly() {
        val size = Size(500f, 500f)
        val orientation = BoardOrientation.PORTRAIT_BLACK

        val d1 = getVisualPosition(D1, size, orientation)
        val d2 = getVisualPosition(D2, size, orientation)
        val d3 = getVisualPosition(D3, size, orientation)
        val d4 = getVisualPosition(D4, size, orientation)

        // En orientación PORTRAIT_WHITE, D3 y D4 deberían estar abajo, D1 y D2 arriba
        assertTrue("D1 should be above center", d1.y < size.height / 2)
        assertTrue("D2 should be above center", d2.y < size.height / 2)
        assertTrue("D3 should be below center", d3.y > size.height / 2)
        assertTrue("D4 should be below center", d4.y > size.height / 2)
    }
}
