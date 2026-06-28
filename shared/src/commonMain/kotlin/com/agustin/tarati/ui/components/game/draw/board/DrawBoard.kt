package com.agustin.tarati.ui.components.game.draw.board

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.BoardRect
import com.agustin.tarati.core.domain.game.board.GameBoard.centralRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.circumferenceRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.domesticRegions
import com.agustin.tarati.core.domain.game.board.GameBoard.externalBoundary
import com.agustin.tarati.core.domain.game.board.GameBoard.vertices
import com.agustin.tarati.core.domain.game.board.Region
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.board.getBoardRect
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import com.agustin.tarati.core.utils.helpers.getCurrentHour
import com.agustin.tarati.ui.components.game.draw.common.NoiseTexture
import com.agustin.tarati.ui.theme.BoardColors
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

fun DrawScope.drawBoardBackground(
    canvasSize: Size,
    orientation: BoardOrientation,
    regionsVisible: Boolean,
    perimeterVisible: Boolean,
    edgesVisible: Boolean = true,
    bordersVisible: Boolean = true,
    baseBoardVisible: Boolean = true,
    noiseVisible: Boolean = true,
    colors: BoardColors,
) {
    if (baseBoardVisible) {
        drawBaseBoard(canvasSize, orientation, colors)
    }

    if (perimeterVisible) {
        drawPerimeter(
            canvasSize = canvasSize,
            orientation = orientation,
            hourOfDay = getCurrentHour(),
            colors = colors,
            edgesVisible = edgesVisible
        )
    }

    if (regionsVisible) {
        // Dibujar regiones centrales
        drawBoardPatternTwoColors(
            canvasSize = canvasSize,
            regions = centralRegions,
            surfaceColor1 = colors.boardPatternColor3,
            surfaceColor2 = colors.boardPatternColor2,
            borderColor = colors.boardPatternBorderColor,
            bordersVisible = bordersVisible,
            orientation = orientation,
        )

        // Dibujar regiones de circunferencia
        drawBoardPatternTwoColors(
            canvasSize = canvasSize,
            regions = circumferenceRegions,
            surfaceColor1 = colors.boardPatternColor3,
            surfaceColor2 = colors.boardPatternColor1,
            borderColor = colors.boardPatternBorderColor,
            bordersVisible = bordersVisible,
            orientation = orientation,
        )

        // Dibujar regiones domésticas
        drawBoardPatternSingleColor(
            canvasSize = canvasSize,
            regions = domesticRegions,
            surfaceColor = colors.boardPatternColor1,
            borderColor = colors.boardPatternBorderColor,
            bordersVisible = bordersVisible,
            orientation = orientation,
        )
    }

    // Pase de textura de grano sobre toda la superficie del tablero.
    // ShaderBrush con BitmapShader REPEAT: el tiling lo realiza el driver gráfico
    // sin loops manuales. BlendMode.Overlay oscurece píxeles oscuros del ruido y
    // aclara los claros, produciendo textura granulada que preserva la paleta.
    //
    // Acotado al bounding box del tablero: correcto cuando el canvas ES el tablero
    // (juego real, previews). En capas de fondo donde el tablero ocupa solo una
    // sub-región del canvas, el grano debe aplicarse una sola vez sobre todo el
    // espacio (noiseVisible = false aquí) para no dejar zonas sin textura.
    if (noiseVisible) {
        val grainRect = calculateBoardBoundingBox(
            getBoardRect(vertices, canvasSize, orientation), canvasSize, 0.1f,
        )
        with(NoiseTexture) {
            applyNoise(
                topLeft = grainRect.topLeft,
                size = grainRect.size,
                cornerRadius = CornerRadius(16f),
                alpha = 0.07f,
            )
        }
    }
}

private fun DrawScope.drawBaseBoard(
    canvasSize: Size,
    orientation: BoardOrientation,
    colors: BoardColors,
) {
    // Fondo base del tablero
    val boardRect =
        calculateBoardBoundingBox(
            getBoardRect(
                vertices = vertices,
                canvasSize = canvasSize,
                orientation = orientation,
            ),
            canvasSize,
            0.1f,
        )

    drawRoundRect(
        color = colors.boardBackground.copy(alpha = 0.6f),
        topLeft = boardRect.topLeft,
        size = boardRect.size,
        cornerRadius = CornerRadius(16f),
    )
}

fun DrawScope.drawBoardPatternTwoColors(
    canvasSize: Size,
    regions: List<Region>,
    surfaceColor1: Color,
    surfaceColor2: Color,
    borderColor: Color,
    bordersVisible: Boolean = true,
    orientation: BoardOrientation,
) {
    regions.forEachIndexed { index, region ->
        val path = createBoundaryPath(canvasSize, orientation, region)

        // Intercalar entre color1 y color2
        val regionColor =
            if (index % 2 == 0) {
                surfaceColor1
            } else {
                surfaceColor2
            }

        drawPath(
            path = path,
            color = regionColor,
            style = Fill,
        )

        if (bordersVisible) {
            // Borde sutil entre casillas
            drawPath(
                path = path,
                color = borderColor.copy(alpha = 0.2f),
                style = Stroke(width = 1f),
            )
        }
    }
}

fun DrawScope.drawBoardPatternSingleColor(
    canvasSize: Size,
    regions: List<Region>,
    surfaceColor: Color,
    borderColor: Color,
    bordersVisible: Boolean = true,
    orientation: BoardOrientation,
) {
    regions.forEach { region ->
        val path = createBoundaryPath(canvasSize, orientation, region)

        // Usar siempre el mismo color
        drawPath(
            path = path,
            color = surfaceColor,
            style = Fill,
        )

        if (bordersVisible) {
            // Borde sutil entre casillas
            drawPath(
                path = path,
                color = borderColor.copy(alpha = 0.3f),
                style = Stroke(width = 1f),
            )
        }
    }
}

private fun DrawScope.drawPerimeter(
    canvasSize: Size,
    orientation: BoardOrientation,
    hourOfDay: Float,
    colors: BoardColors,
    edgesVisible: Boolean,
) {
    // Path base del perímetro
    val boardPath = createBoundaryPath(canvasSize, orientation, externalBoundary)
    val vertexDistance = min(boardPath.getBounds().width, boardPath.getBounds().height) * 0.15f

    // Perímetro principal
    if (edgesVisible) {
        drawPath(
            path = boardPath,
            color = colors.boardEdgeColor.copy(alpha = 0.2f),
            style = Stroke(width = vertexDistance * 1.15f, join = StrokeJoin.Round),
        )
    }
    drawPath(
        path = boardPath,
        color = colors.boardPerimeterColor,
        style = Stroke(width = vertexDistance, join = StrokeJoin.Round),
    )

    // Borde de luz (lado del sol)
    val lightOfDay = getLightOfDay(hourOfDay, 32f)

    val lightBorderPath =
        Path().apply {
            addPath(boardPath)
            translate(
                Offset(
                    +lightOfDay.sunPosition.x * 2f,
                    +lightOfDay.sunPosition.y * 2f,
                ),
            )
        }

    val lightColor = colors.boardPatternColor3.copy(alpha = 0.4f)
    drawPath(
        path = lightBorderPath,
        color = lightColor,
        style = Stroke(width = 4f),
    )

    // Borde de sombra (lado opuesto al sol)
    val shadowBorderPath =
        Path().apply {
            addPath(boardPath)
            translate(
                Offset(
                    -lightOfDay.sunPosition.x * 4f,
                    -lightOfDay.sunPosition.y * 4f,
                ),
            )
        }

    val shadowColor = colors.boardVertexColor.copy(alpha = 0.4f)
    drawPath(
        path = shadowBorderPath,
        color = shadowColor,
        style = Stroke(width = 4f),
    )
}

private fun calculateBoardBoundingBox(
    rect: BoardRect,
    canvasSize: Size,
    margin: Float,
): BoardRect {
    // Añadir un margen para que el fondo se extienda un poco más allá de los vértices
    val margin = minOf(canvasSize.width, canvasSize.height) * margin

    return BoardRect(
        topLeft = Offset(rect.topLeft.x - margin, rect.topLeft.y - margin),
        size = Size(rect.size.width + 2 * margin, rect.size.height + 2 * margin),
    )
}

/**
 * Calcula la posición del sol basada en la hora del día
 */
fun calculateSunPosition(hour: Float): Offset {
    val normalizedHour = normalizeHour(hour)
    val sunAngle = (normalizedHour * 360f - 90f) * (PI / 180f).toFloat()

    val sunHeight = calculateSunHeight(normalizedHour)

    return Offset(
        x = cos(sunAngle) * sunHeight,
        y = -sin(sunAngle) * sunHeight,
    )
}

/**
 * Calcula la altura del sol (0-1) basada en la hora normalizada
 */
private fun calculateSunHeight(normalizedHour: Float): Float {
    val distanceFromNoon = abs(normalizedHour - 0.5f)
    return 1f - (distanceFromNoon * 2f)
}

private fun normalizeHour(hour: Float): Float = (hour % 24f) / 24f

/**
 * Crea un path cerrado a partir de la lista de regiones
 */
fun createBoundaryPath(
    canvasSize: Size,
    orientation: BoardOrientation,
    region: Region,
): Path = createBoundaryPath(canvasSize, orientation, region.vertices)

/**
 * Crea un path cerrado a partir de la lista de vértices
 */
fun createBoundaryPath(
    canvasSize: Size,
    orientation: BoardOrientation,
    boundary: List<Vertex>,
): Path =
    Path().apply {
        boundary.forEachIndexed { index, vertex ->
            val pos = getVisualPosition(vertex, canvasSize, orientation)
            if (index == 0) {
                moveTo(pos.x, pos.y)
            } else {
                lineTo(pos.x, pos.y)
            }
        }
        close()
    }