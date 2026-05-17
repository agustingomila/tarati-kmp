package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.agustin.tarati.core.domain.game.board.Vertex
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.CobColor.BLACK
import com.agustin.tarati.core.domain.game.pieces.CobColor.WHITE
import com.agustin.tarati.ui.components.game.animation.AnimatedCob
import com.agustin.tarati.ui.components.game.draw.board.LightOfDay
import com.agustin.tarati.ui.components.game.draw.board.getLightOfDay
import com.agustin.tarati.ui.components.game.draw.common.NoiseTexture
import com.agustin.tarati.ui.theme.BoardColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawCob(
    position: Offset,
    radius: Float,
    selectedVertex: Vertex?,
    vertex: Vertex,
    cob: Cob,
    hourOfDay: Float = 12f,
    selectionTimeMs: Long = 0L,
    colors: BoardColors,
    // Pre-calculados opcionalmente por el caller para evitar recalcular por pieza.
    // Si es null, se calculan aquí (compatibilidad con callers que dibujan una sola pieza).
    precomputedLight: LightOfDay? = null,
    precomputedOrganicColors: Map<CobColor, Color>? = null,
) {
    val lightOfDay = precomputedLight ?: getLightOfDay(hourOfDay, radius)
    val pieceColors = getPieceColors(cob, colors)
    val organicColor = precomputedOrganicColors?.get(cob.color)
        ?: createOrganicColor(pieceColors, hourOfDay, colors)

    drawOrganicCob(position, radius, hourOfDay, lightOfDay, pieceColors, colors, organicColor = organicColor)

    if (cob.isUpgraded) {
        drawRoc(position, radius, cob, colors)
    }

    if (vertex == selectedVertex) {
        val baseColor = if (cob.color == WHITE) colors.whiteCobSelectColor else colors.blackCobSelectColor
        drawSelection(position, radius, baseColor, colors, selectionTimeMs)
    }
}

// ── Helpers de textura ───────────────────────────────────────────────────────
//
internal fun DrawScope.drawOrganicCob(
    position: Offset,
    radius: Float,
    hourOfDay: Float,
    lightOfDay: LightOfDay,
    pieceColors: PieceColor,
    colors: BoardColors,
    withTexture: Boolean = true,
    organicColor: Color? = null,
) {
    // 1. Sombra base — usa boardVertexColor (color más oscuro del tablero), no el
    // de la pieza. La sombra es ambiental: depende de la paleta, no del bando.
    drawCircle(
        color = colors.boardVertexColor.copy(alpha = 0.30f * lightOfDay.shadowIntensity),
        center =
            position.copy(
                x = position.x + lightOfDay.shadowOffsetX,
                y = position.y + lightOfDay.shadowOffsetY,
            ),
        radius = radius * 1.2f,
    )

    // 2. Cuerpo principal con borde — usa organicColor pre-calculado si disponible
    val organicColor = organicColor ?: createOrganicColor(pieceColors, hourOfDay, colors)
    drawCobWithBorder(
        position = position,
        radius = radius,
        fillColor = organicColor,
        borderColor = pieceColors.borderColor,
    )

    // 3. Textura de grano. Saltada cuando el caller gestiona la textura
    // externamente (e.g. drawConversionFromCenter) para evitar sobre-texturizado.
    if (withTexture) with(NoiseTexture) { applyNoise(position, radius) }
}

fun DrawScope.drawRoc(
    position: Offset,
    radius: Float,
    cob: Cob,
    colors: BoardColors,
) {
    val upgradeColor =
        when (cob.color) {
            WHITE -> colors.blackCobColor
            BLACK -> colors.whiteCobColor
        }

    // Punto central de upgrade
    drawCircle(
        color = upgradeColor,
        center = position,
        radius = radius * 0.2f,
    )
}

fun DrawScope.drawSelection(
    position: Offset,
    radius: Float,
    baseColor: Color,
    colors: BoardColors,
    timeMs: Long = 0L,
) {
    val highlightRadius = radius * 1.4f

    // ── Anillos base ──────────────────────────────────────────────────────────
    // Sin listOf/Triple: llamadas directas eliminan ~4 allocations/frame a 60fps.
    drawCircle(
        color = colors.selectionIndicatorColor, center = position,
        radius = highlightRadius, style = Stroke(width = radius * 0.6f), alpha = 0.1f
    )
    drawCircle(
        color = colors.selectionIndicatorColor, center = position,
        radius = highlightRadius, style = Stroke(width = radius * 0.3f), alpha = 0.2f
    )
    drawCircle(
        color = baseColor, center = position,
        radius = highlightRadius, style = Stroke(width = radius * 0.08f), alpha = 0.6f
    )

    // ── Brillo giratorio ──────────────────────────────────────────────────────
    val glowAngleDeg = (timeMs % 2000L) / 2000f * 360f
    val extraSweep = 40f

    // SolidColor y Rect precalculados fuera de cada drawArcWithArrowHead:
    // baseColor.copy() alloca un nuevo Color; SolidColor() lo envuelve.
    // Calculándolos una vez evitamos 4 allocations por frame (2 Color + 2 SolidColor).
    val brushOuter = SolidColor(baseColor.copy(alpha = 0.25f))
    val brushInner = SolidColor(baseColor.copy(alpha = 0.70f))

    // Arco exterior difuso
    val rOuter = highlightRadius * 0.94f
    val strokeOuter = radius * 0.3f
    drawArcWithArrowHead(
        rect = Rect(Offset(position.x - rOuter, position.y - rOuter), Size(rOuter * 2f, rOuter * 2f)),
        brush = brushOuter,
        startAngle = glowAngleDeg - extraSweep,
        sweepAngle = 80f * (1f + extraSweep / highlightRadius),
        useCenter = false,
        style = Stroke(width = strokeOuter),
        arrowSize = strokeOuter * 2f,
        arrowWidth = 0f,
        arrowAtStart = true,
        arrowAtEnd = true,
    )

    // Arco interior principal
    val strokeInner = radius * 0.12f
    drawArcWithArrowHead(
        rect = Rect(
            Offset(position.x - highlightRadius, position.y - highlightRadius),
            Size(highlightRadius * 2f, highlightRadius * 2f)
        ),
        brush = brushInner,
        startAngle = glowAngleDeg,
        sweepAngle = 80f,
        useCenter = false,
        style = Stroke(width = strokeInner),
        arrowSize = strokeInner * 3f,
        arrowWidth = strokeInner * 1.2f,
        arrowAtStart = true,
        arrowAtEnd = true,
    )

    // ── Punto de brillo ───────────────────────────────────────────────────────
    val headAngleRad = ((glowAngleDeg + 40.0) * PI / 180.0).toFloat()
    drawCircle(
        color = baseColor.copy(alpha = 0.9f),
        center = Offset(
            position.x + highlightRadius * cos(headAngleRad),
            position.y + highlightRadius * sin(headAngleRad),
        ),
        radius = radius * 0.12f,
    )
}

fun DrawScope.drawAnimatedCob(
    position: Offset,
    radius: Float,
    vertex: Vertex,
    selectedVertex: Vertex?,
    animatedCob: AnimatedCob,
    hourOfDay: Float = 12f,
    selectionTimeMs: Long = 0L,
    animationType: ConversionAnimationType = ConversionAnimationType.FROM_CENTER,
    colors: BoardColors,
    precomputedLight: LightOfDay? = null,
) {
    val cob = animatedCob.cob
    val lightOfDay = precomputedLight ?: getLightOfDay(hourOfDay, radius)
    val pieceColors = getPieceColors(cob, colors)

    // Dibujar pieza base
    if (!animatedCob.isConverting) {
        drawOrganicCob(position, radius, hourOfDay, lightOfDay, pieceColors, colors)
    } else {
        drawConverting(position, radius, animatedCob, animationType, hourOfDay, lightOfDay, pieceColors, colors)
    }

    // Dibujar upgrade
    if (cob.isUpgraded) {
        drawUpgrading(position, radius, animatedCob, animationType, colors)
    }

    if (vertex == selectedVertex) {
        drawSelection(position, radius, pieceColors.baseColor, colors, selectionTimeMs)
    }
}

fun DrawScope.drawConverting(
    position: Offset,
    radius: Float,
    animatedCob: AnimatedCob,
    animationType: ConversionAnimationType,
    hourOfDay: Float,
    lightOfDay: LightOfDay,
    pieceColors: PieceColor,
    colors: BoardColors,
) {
    val waveColor =
        when (animatedCob.cob.color) {
            WHITE -> colors.whiteConvertingWaveColor
            BLACK -> colors.blackConvertingWaveColor
        }

    when (animationType) {
        ConversionAnimationType.FROM_CENTER ->
            drawConversionFromCenter(
                position,
                radius,
                animatedCob,
                hourOfDay,
                lightOfDay,
                waveColor,
                pieceColors,
                colors,
            )

        ConversionAnimationType.FROM_BORDER ->
            drawConversionFromBorder(position, radius, animatedCob, waveColor, hourOfDay, colors)

        ConversionAnimationType.FLIP ->
            drawCoinFlip(position, radius, animatedCob, hourOfDay, lightOfDay, colors)
    }
}

fun DrawScope.drawUpgrading(
    position: Offset,
    radius: Float,
    animatedCob: AnimatedCob,
    animationType: ConversionAnimationType,
    colors: BoardColors,
) {
    if (animatedCob.upgradeProgress <= 0f) return

    when (animationType) {
        ConversionAnimationType.FROM_CENTER ->
            drawUpgradeFromCenter(position, radius, animatedCob, colors)

        ConversionAnimationType.FROM_BORDER ->
            drawUpgradeFromBorder(position, radius, animatedCob, colors)

        ConversionAnimationType.FLIP -> Unit
    }
}

fun DrawScope.drawCobWithBorder(
    position: Offset,
    radius: Float,
    fillColor: Color,
    borderColor: Color,
    borderWidth: Float = radius * 0.3f,
) {
    drawCircle(color = fillColor, center = position, radius = radius)
    drawCircle(
        color = borderColor,
        center = position,
        radius = radius,
        style = Stroke(width = borderWidth),
    )
}

// region Funciones Auxiliares de Dibujo

/**
 * Devuelve los 4 colores que se usan para pintar una pieza
 */
fun getPieceColors(
    cob: Cob,
    colors: BoardColors,
): PieceColor {
    val result: PieceColor =
        when (cob.color) {
            WHITE ->
                PieceColor(
                    colors.whiteCobColor,
                    colors.whiteCobBorderColor,
                    colors.whiteCobLightColor,
                    colors.whiteCobShadowColor,
                )

            BLACK ->
                PieceColor(
                    colors.blackCobColor,
                    colors.blackCobBorderColor,
                    colors.blackCobLightColor,
                    colors.blackCobShadowColor,
                )
        }
    return result
}

/**
 * Crea un color orgánico usando los tonos de borde específicos
 */
fun createOrganicColor(
    pieceColor: PieceColor,
    hourOfDay: Float,
    colors: BoardColors,
): Color {
    val normalizedHour = (hourOfDay % 24f) / 24f
    val warmth = 0.95f + 0.1f * cos(normalizedHour * 2f * PI.toFloat())

    val lightColor = pieceColor.lightColor
    val shadowColor = pieceColor.shadowColor

    // Usar los colores de la paleta para crear variaciones orgánicas
    return when (val baseColor = pieceColor.baseColor) {
        colors.whiteCobColor -> {
            // Para piezas blancas: mezclar con el color claro del borde negro
            blendColors(baseColor, lightColor, 0.15f * warmth)
        }

        colors.blackCobColor -> {
            // Para piezas negras: mezclar con el color oscuro del borde blanco
            blendColors(baseColor, shadowColor, 0.1f * (2f - warmth))
        }

        else -> {
            // Para colores personalizados
            val blendColor =
                if (baseColor.red + baseColor.green + baseColor.blue > 1.5f) {
                    lightColor
                } else {
                    shadowColor
                }
            blendColors(baseColor, blendColor, 0.12f)
        }
    }
}

// endregion

