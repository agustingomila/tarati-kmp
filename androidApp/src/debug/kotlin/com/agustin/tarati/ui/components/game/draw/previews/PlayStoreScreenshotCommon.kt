package com.agustin.tarati.ui.components.game.draw.previews

import android.content.res.Configuration
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.services.localization.LocalAppLanguage
import com.agustin.tarati.ui.components.game.draw.board.drawBoardBackground
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.BOTTOM
import com.agustin.tarati.ui.components.game.draw.previews.TextPosition.TOP
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import java.util.*
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════════════════
//  DIMENSIONES PLAY STORE
//  Formato portrait recomendado: 1080 × 1920 px.
//  A densidad xxhdpi (×3): 360 × 640 dp → 1080 × 1920 px exactos.
// ══════════════════════════════════════════════════════════════════════════════

const val STORE_W_DP = 360
const val STORE_H_DP = 640

// ══════════════════════════════════════════════════════════════════════════════
//  CONVENCIÓN DE NOMBRES PARA ASSETS
//  Ubicación : res/drawable/
//  Formato   : ss_{n}_{lang}_{palette}.png
//
//  {n}      = número de captura con cero a la izquierda: 01 … 07
//  {lang}   = idioma:  es | en
//  {palette}= paleta:  classic | dark | nature | grayscale
//
//  Capturas disponibles:
//    01 — Tablero principal en partida
//    02 — IA oponente con niveles de dificultad
//    03 — Historial de movimientos con navegación
//    04 — Paletas de colores del tablero
//    05 — Editor de tablero (posición inicial personalizada)
//    06 — Biblioteca de partidas guardadas
//    07 — Detalle y análisis de partida guardada
//
//  Para activar un asset real, reemplazar `screenshotRes = null` por
//  `screenshotRes = R.drawable.ss_01_es_classic` en el @Preview correspondiente.
//  Mientras el drawable no exista se renderiza el tablero de Tarati con los
//  colores de la paleta indicada como placeholder.
//
//  Paletas activas: Classic · Dark · Nature · Grayscale.
//  Halloween y Christmas quedan excluidas (temas estacionales desbloqueables).
// ══════════════════════════════════════════════════════════════════════════════

// ── Geometría del teléfono (dp; convertidos a px dentro del DrawScope) ─────────

/** El teléfono ocupa el 75.5 % del ancho del canvas. */
const val PHONE_W_DP = 272f

/** Relación de aspecto de un teléfono moderno: 19.5 : 9. */
const val PHONE_ASPECT_RATIO = 19.5f / 9f

/** Radio de esquina del cuerpo exterior del teléfono. */
const val PHONE_CORNER_DP = 34f

/** Radio de esquina del área de pantalla (ligeramente menor que el cuerpo). */
const val SCREEN_CORNER_DP = 20f

/** Bezeles laterales (izquierdo y derecho). */
const val BEZEL_SIDE_DP = 10f

/** Bezel superior; aloja la cámara frontal. */
const val BEZEL_TOP_DP = 24f

/** Bezel inferior; aloja el indicador de inicio (home bar). */
const val BEZEL_BOTTOM_DP = 28f

/**
 * Tamaño lógico del contenido renderizado dentro de la pantalla del teléfono.
 * Coincide con las dimensiones de un Pixel 5 (~411×891 dp), que es el dispositivo
 * de referencia usado en [GameScreenPreviews] y otros previews de pantalla completa.
 *
 * El contenido Compose se renderiza a este tamaño y luego se escala para encajar
 * en el área de pantalla del frame (~252×537 dp), produciendo el efecto de una
 * captura de pantalla de alta densidad — idéntico al resultado de tomar un
 * screenshot real de un teléfono moderno y escalarla al tamaño del frame.
 */
const val CONTENT_W_DP = 411f
const val CONTENT_H_DP = 891f

/**
 * Fracción reservada para el título (zona superior) y el subtítulo (zona inferior).
 * 13 % × 2 = 26 % en total; el 74 % restante queda para el teléfono + márgenes.
 */
const val CENTERED_TEXT_FRACTION = 0.13f

/**
 * Altura de la barra de estado simulada en coordenadas de pantalla (dp).
 * Usada tanto para dibujar los iconos en [drawStatusBarOverlay] como para
 * calcular el offset del contenido Compose en la Capa 3.
 */
private const val STATUS_BAR_H_DP = 26f

/**
 * Offset superior que se aplica al contenido Compose en la Capa 3, expresado
 * en coordenadas de contenido (411 × 891 dp).
 *
 * Derivado de: STATUS_BAR_H_DP × (CONTENT_H_DP / screenH_dp)
 *   screenH_dp = PHONE_W_DP × PHONE_ASPECT_RATIO − BEZEL_TOP_DP − BEZEL_BOTTOM_DP
 *             = 272 × (19.5/9) − 24 − 28 = 537.33 dp
 *   offset    = 26 × (891 / 537.33) ≈ 43 dp
 *
 * Este valor desplaza la UI del composable hacia abajo exactamente la misma
 * distancia que ocupa la barra de estado, simulando el efecto de
 * `showSystemUi = true` con `WindowInsets.statusBars`.
 */
const val STATUS_BAR_CONTENT_OFFSET_DP = 43f

/**
 * Fracción de la altura del canvas reservada para el bloque de texto (~24.5 %).
 * El teléfono ocupa el 75.5 % restante visible (~82 % de su altura total,
 * quedando el 18 % cortado fuera del borde opuesto al texto).
 */
const val TEXT_AREA_FRACTION = 0.245f

// ── Posición del texto ────────────────────────────────────────────────────────

/**
 * Controla en qué extremo del canvas se coloca el bloque de texto.
 *
 * - [TOP]:    texto arriba  → teléfono entra desde abajo, se ve la parte superior.
 * - [BOTTOM]: texto abajo   → teléfono entra desde arriba, se ve la parte inferior.
 */
enum class TextPosition { TOP, BOTTOM }

// ── Colores de fondo por paleta ────────────────────────────────────────────────

/**
 * Gradiente vertical de fondo (top → bottom) coordinado con la estética de
 * cada tema del juego.
 */
fun screenshotBackground(palette: BoardPalette): Pair<Color, Color> = when (palette) {
    ClassicPalette -> palette.boardPerimeterColor to palette.boardBackground
    DarkPalette -> palette.boardPerimeterColor to palette.boardBackground
    NaturePalette -> palette.boardPerimeterColor to palette.boardBackground
    GrayscalePalette -> palette.boardPerimeterColor to palette.boardBackground
    else -> GrayscalePalette.boardPerimeterColor to GrayscalePalette.boardBackground
}

// ── Textos ─────────────────────────────────────────────────────────────────────
internal data class TextBlockSpec(
    val text: String,
    val style: TextStyle,
    val areaTopDp: Float,
    val areaHDp: Float
)

// ══════════════════════════════════════════════════════════════════════════════
//  COMPOSABLE PRINCIPAL
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Envuelve [content] con los providers necesarios para que
 * [localizedString]
 * resuelva strings en el [locale] indicado, independientemente del idioma del
 * dispositivo. Provee [LocalContext], [LocalConfiguration] y [LocalAppLanguage]
 * — los tres valores que lee [localizedString].
 */
@Composable
fun WithLocale(locale: Locale, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val deviceConfig = LocalConfiguration.current
    val localizedConfig = remember(locale, deviceConfig) {
        Configuration(deviceConfig).apply { setLocale(locale) }
    }
    val localizedContext = remember(locale, deviceConfig) {
        context.createConfigurationContext(localizedConfig)
    }

    val appLanguage = LocalAppLanguage.current

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfig,
        LocalAppLanguage provides appLanguage,
        content = content,
    )
}

internal fun DrawScope.drawBoardOverlay(
    boardColors: BoardColors,
    tiltDeg: Float,
    boardSizeFraction: Float,
    centerXFraction: Float,
    centerYFraction: Float,
    alpha: Float,
) {
    val boardSize = size.width * boardSizeFraction
    val boardLeft = size.width * centerXFraction - boardSize / 2f
    val boardTop = size.height * centerYFraction - boardSize / 2f

    // Colores del tablero en versión tenue — solo las propiedades que
    // drawBoardBackground usa para dibujar regiones.
    val subtleColors = boardColors.copy(
        boardPatternColor1 = boardColors.boardPatternColor1.copy(alpha = alpha * 1.4f),
        boardPatternColor2 = boardColors.boardPatternColor2.copy(alpha = alpha * 0.8f),
        boardPatternColor3 = boardColors.boardPatternColor3.copy(alpha = 0f),
    )

    clipRect(0f, 0f, size.width, size.height) {
        rotate(
            degrees = tiltDeg,
            pivot = Offset(size.width / 2f, size.height * centerYFraction),
        ) {
            translate(left = boardLeft, top = boardTop) {
                drawBoardBackground(
                    canvasSize = Size(boardSize, boardSize),
                    orientation = BoardOrientation.PORTRAIT_WHITE,
                    regionsVisible = true,
                    perimeterVisible = false,
                    bordersVisible = false,
                    baseBoardVisible = false,
                    colors = subtleColors,
                )
            }
        }
    }
}

/**
 * Gradiente vertical de fondo + tablero(s) tenues + resplandor radial.
 *
 * @param boardColors    Colores de la paleta para los tableros de fondo.
 */
internal fun DrawScope.drawBackgroundLayer(
    gradTop: Color,
    gradBottom: Color,
    textPosition: TextPosition,
    canvasSize: Size,
    boardColors: BoardColors,
) {
    drawRect(brush = Brush.verticalGradient(listOf(gradTop, gradBottom)))

    data class BoardSpec(
        val tiltDeg: Float,
        val centerXFraction: Float,
        val centerYFraction: Float,
        val boardSizeFraction: Float,
        val alpha: Float = 0.16f
    )

    val configs = if (textPosition == BOTTOM) {
        listOf(
            BoardSpec(-8f, 0.38f, 0.88f, 1.8f),   // principal inferior
            BoardSpec(10f, 0.78f, 0.38f, 1.1f),   // secundario 1
            BoardSpec(-10f, 0.18f, 0.18f, 0.75f)  // secundario 2
        )
    } else {
        listOf(
            BoardSpec(8f, 0.62f, 0.12f, 1.8f),    // principal superior
            BoardSpec(-10f, 0.18f, 0.62f, 1.1f),  // secundario 1
            BoardSpec(10f, 0.78f, 0.82f, 0.75f)   // secundario 2
        )
    }

    configs.forEach { spec ->
        drawBoardOverlay(
            boardColors,
            tiltDeg = spec.tiltDeg,
            centerXFraction = spec.centerXFraction,
            centerYFraction = spec.centerYFraction,
            boardSizeFraction = spec.boardSizeFraction,
            alpha = spec.alpha
        )
    }

    val glowY = canvasSize.height * if (textPosition == TOP) 0.62f else 0.38f
    val glowR = canvasSize.width * 0.75f
    drawCircle(
        brush = Brush.radialGradient(
            0f to Color.White.copy(alpha = 0.07f),
            1f to Color.Transparent,
            center = Offset(canvasSize.width / 2f, glowY),
            radius = glowR,
        ),
        radius = glowR,
        center = Offset(canvasSize.width / 2f, glowY),
    )
}

/**
 * Sombra difusa con [BlurMaskFilter] — desenfoque gaussiano real.
 * [scale] ajusta offset y radio de blur proporcionalmente al tamaño del teléfono.
 */
internal fun DrawScope.drawPhoneShadow(
    phoneX: Float, phoneY: Float, phoneW: Float, phoneH: Float,
    scale: Float = 1f, colors: BoardColors
) {
    val offsetX = 6.dp.toPx() * scale
    val offsetY = 10.dp.toPx() * scale
    val blurRadius = 5.dp.toPx() * scale
    val corner = PHONE_CORNER_DP.dp.toPx() * scale

    drawContext.canvas.nativeCanvas.apply {
        drawRoundRect(
            phoneX + offsetX,
            phoneY + offsetY,
            phoneX + offsetX + phoneW,
            phoneY + offsetY + phoneH,
            corner, corner,
            Paint().apply {
                isAntiAlias = true
                color = colors.blackCobShadowColor.toArgb()
                alpha = 80
                maskFilter = BlurMaskFilter(
                    blurRadius,
                    BlurMaskFilter.Blur.NORMAL,
                )
            }
        )
    }
}

/** Cuerpo del teléfono: base oscura + gradiente lateral que simula aluminio. */
internal fun DrawScope.drawPhoneBody(
    topLeft: Offset,
    size: Size,
    phoneRefWidthPx: Float = PHONE_W_DP.dp.toPx()
) {
    val scale = size.width / phoneRefWidthPx
    val corner = CornerRadius(PHONE_CORNER_DP.dp.toPx() * scale)

    // Base
    drawRoundRect(
        color = Color(0xFF1A1A1A),
        topLeft = topLeft,
        size = size,
        cornerRadius = corner
    )

    // Overlay degradado
    drawRoundRect(
        brush = Brush.horizontalGradient(
            0f to Color.White.copy(alpha = 0.09f),
            0.12f to Color.Transparent,
            0.88f to Color.Transparent,
            1f to Color.Black.copy(alpha = 0.20f),
            startX = topLeft.x,
            endX = topLeft.x + size.width
        ),
        topLeft = topLeft,
        size = size,
        cornerRadius = corner
    )
}

// ── Funciones de dibujo para el abanico ──────────────────────────────────────

/**
 * Sombra de un teléfono escalada proporcionalmente a [phoneW].
 * Separada de [drawPhoneUnit] para poder dibujarla antes que los cuerpos
 * de todos los teléfonos sin que queden sombras sobre los cuerpos vecinos.
 */
/**
 * Sombra difusa de un teléfono escalado — delega en [drawPhoneShadow] pasando
 * el factor de escala calculado a partir de [phoneW].
 * El parámetro [color] se mantiene por compatibilidad con callers existentes
 * pero el alpha real se fija en [drawPhoneShadow] (~0.31).
 */
internal fun DrawScope.drawPhoneUnitShadow(
    phoneX: Float, phoneY: Float, phoneW: Float, phoneH: Float, boardColors: BoardColors
) {
    val scale = phoneW / PHONE_W_DP.dp.toPx()
    drawPhoneShadow(phoneX, phoneY, phoneW, phoneH, scale, boardColors)
}

internal fun DrawScope.drawScreenReflection(
    screenX: Float, screenY: Float, screenW: Float, screenH: Float,
) {
    val reflectW = screenW * 0.42f
    drawRect(
        brush = Brush.linearGradient(
            0.00f to Color.White.copy(alpha = 0.13f),
            0.60f to Color.White.copy(alpha = 0.09f),
            1.00f to Color.White.copy(alpha = 0.06f),
            start = Offset(screenX, screenY),
            end = Offset(screenX + reflectW, screenY),
        ),
        topLeft = Offset(screenX, screenY),
        size = Size(reflectW, screenH),
    )
}

/**
 * Borde interior de pantalla (separación entre pantalla y bezel)
 */
internal fun DrawScope.drawScreenInnerBorder(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    cornerRadius: Float = SCREEN_CORNER_DP.dp.toPx(),
    color: Color = Color.Black,
    alpha: Float = 0.26f,
    strokeWidth: Float = 1.dp.toPx()
) {
    drawRoundRect(
        color = color.copy(alpha = alpha),
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = strokeWidth),
    )
}

/**
 * Simula la barra de estado del sistema dentro del frame del teléfono.
 *
 * Dibuja (de izquierda a derecha): hora "9:41" — (espacio) — barras de señal —
 * icono WiFi — icono de batería. El posicionamiento es estrictamente de
 * derecha a izquierda para evitar solapamientos en el frame reducido.
 *
 * La capa de gradiente oscuro mejora la legibilidad sobre cualquier fondo
 * de la UI subyacente. Todo el contenido está clipeado al path redondeado
 * de la pantalla.
 *
 * @param screenX  Coordenada X del borde izquierdo de la pantalla (px).
 * @param screenY  Coordenada Y del borde superior de la pantalla (px).
 * @param screenW  Ancho de la pantalla en px.
 */
internal fun DrawScope.drawStatusBarOverlay(
    screenX: Float, screenY: Float, screenW: Float,
    textMeasurer: TextMeasurer,
    boardColors: BoardColors,
    scale: Float = 1f,
) {
    val barH = STATUS_BAR_H_DP.dp.toPx() * scale
    val padH = 9.dp.toPx() * scale
    val bgColor = boardColors.boardPatternColor3
    // Iconos oscuros sobre fondos claros (Classic, Grayscale), blancos sobre oscuros
    val iconColor = if (bgColor.luminance() > 0.35f) Color(0xFF1A1A1A) else Color.White
    val iconAlpha = 0.90f

    // ── Geometría — layout de derecha a izquierda ────────────────────────────
    val iconScale = scale * 0.9f

    // Batería
    val batBodyW = 18.dp.toPx() * scale
    val batBodyH = 9.dp.toPx() * scale
    val batNubW = 2.dp.toPx() * scale
    val batRightX = screenX + screenW - padH // borde derecho del nub
    val batNubX = batRightX - batNubW        // inicio del nub
    val batBodyX = batNubX - batBodyW        // borde izquierdo del cuerpo
    val batBodyY = screenY + (barH - batBodyH) / 2f

    // WiFi — 3 arcos; radio máximo = 3 × 3.2dp
    val wifiMaxR = 3 * 3.2f.dp.toPx() * scale
    val iconGap = 6.dp.toPx() * scale
    val wifiCenterX = batBodyX - iconGap - wifiMaxR
    val wifiBaseY = screenY + barH - 11.dp.toPx() * scale

    // Barras de señal — 4 barras de ancho 2.5dp con gap 1.5dp
    val sigBarW = 2.5.dp.toPx() * scale
    val sigBarGap = 1.5.dp.toPx() * scale
    val sigTotalW = 4 * sigBarW + 3 * sigBarGap    // ≈ 14.5 dp
    val sigEndX = wifiCenterX - wifiMaxR - iconGap // borde derecho de las barras
    val sigStartX = sigEndX - sigTotalW
    val sigBaseY = screenY + barH - 9.dp.toPx() * scale

    // ── Clipping al path redondeado de pantalla ──────────────────────────────
    val screenPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = screenX,
                top = screenY,
                right = screenX + screenW,
                bottom = screenY + barH * 1.8f,
                cornerRadius = CornerRadius(SCREEN_CORNER_DP.dp.toPx()),
            )
        )
    }
    clipPath(screenPath) {
        // Fondo plano con el color de la paleta — igual que la system bar nativa
        // que adopta el color de la app. La transición con la UI es natural porque
        // la mayoría de las apps usan el color de superficie como fondo de su TopBar.
        drawRect(
            color = bgColor,
            topLeft = Offset(screenX, screenY),
            size = Size(screenW, barH),
        )

        // ── Hora ─────────────────────────────────────────────────────────────
        val timeLayout = textMeasurer.measure(
            "9:41",
            TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = iconColor),
        )
        drawText(
            timeLayout,
            topLeft = Offset(
                x = screenX + padH,
                y = screenY + (barH - timeLayout.size.height) / 2f,
            ),
        )

        // ── Barras de señal ───────────────────────────────────────────────────
        drawSignalBars(
            startX = sigStartX,
            baseY = sigBaseY,
            level = 4,          // ejemplo: señal máxima
            color = iconColor,
            alpha = iconAlpha,
            scale = iconScale
        )

        // ── WiFi ──────────────────────────────────────────────────────────────
        drawWifiIcon(
            centerX = wifiCenterX,
            centerY = wifiBaseY,
            strength = 3,       // intensidad máxima
            color = iconColor,
            alpha = iconAlpha,
            scale = iconScale
        )

        // ── Batería ───────────────────────────────────────────────────────────
        // Nub (pequeño rectángulo a la derecha del cuerpo)
        drawBatteryIcon(
            rightNubX = batRightX,
            bodyY = batBodyY,
            bodyHeight = batBodyH,
            chargePercent = 85, // 85% de batería
            color = iconColor,
            alpha = iconAlpha,
            scale = iconScale
        )
    }
}

/**
 * Reflejo de cristal + borde de pantalla.
 * [cornerRadius] en px — defaults al radio estándar ([SCREEN_CORNER_DP]).
 * Pasar un valor explícito en teléfonos escalados donde el radio es proporcional.
 */
internal fun DrawScope.drawScreenGlassOverlay(
    screenX: Float, screenY: Float, screenW: Float, screenH: Float,
    cornerRadius: Float = SCREEN_CORNER_DP.dp.toPx(),
) {
    val screenPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = screenX, top = screenY,
                right = screenX + screenW, bottom = screenY + screenH,
                cornerRadius = CornerRadius(cornerRadius),
            )
        )
    }
    clipPath(screenPath) {
        drawScreenReflection(screenX, screenY, screenW, screenH)
    }
    drawScreenInnerBorder(
        x = screenX,
        y = screenY,
        width = screenW,
        height = screenH
    )
}

/**
 * Cámara frontal: punch-hole centrado en el bezel superior.
 * [scale] escala el radio y el trazo proporcionalmente al tamaño del teléfono.
 */
internal fun DrawScope.drawCameraLens(
    phoneX: Float, phoneY: Float, phoneW: Float,
    scale: Float = 1f,
) {
    val r = 4.dp.toPx() * scale
    val center = Offset(phoneX + phoneW / 2f, phoneY + BEZEL_TOP_DP.dp.toPx() * scale / 2f)

    data class CameraCircleLayer(
        val color: Color,
        val radiusMulti: Float,
        val style: DrawStyle = Fill
    )

    listOf(
        CameraCircleLayer(Color(0xFF0E0E0E), 1f),
        CameraCircleLayer(Color(0xFF2A2A2A), 0.48f),
        CameraCircleLayer(Color.White.copy(alpha = 0.13f), 1f, Stroke(width = 0.8.dp.toPx() * scale))
    ).forEach { layer ->
        drawCircle(color = layer.color, radius = r * layer.radiusMulti, center = center, style = layer.style)
    }
}

/**
 * Indicador de inicio (home bar) centrado en el bezel inferior.
 * [scale] escala las dimensiones proporcionalmente al tamaño del teléfono.
 */
internal fun DrawScope.drawHomeIndicator(
    phoneX: Float, phoneY: Float, phoneW: Float, phoneH: Float,
    scale: Float = 1f,
) {
    val barW = 48.dp.toPx() * scale
    val barH = 4.dp.toPx() * scale
    drawRoundRect(
        color = Color.White.copy(alpha = 0.44f),
        topLeft = Offset(
            x = phoneX + (phoneW - barW) / 2f,
            y = phoneY + phoneH - BEZEL_BOTTOM_DP.dp.toPx() * scale / 2f - barH / 2f,
        ),
        size = Size(barW, barH),
        cornerRadius = CornerRadius(barH / 2f),
    )
}

/** Borde exterior del teléfono (rim light): línea blanca semitransparente. */
internal fun DrawScope.drawRimLight(
    phoneX: Float, phoneY: Float, phoneW: Float, phoneH: Float,
    scale: Float = 1f,
) {
    drawRoundRect(
        color = Color.White.copy(alpha = 0.5f),
        topLeft = Offset(phoneX, phoneY),
        size = Size(phoneW, phoneH),
        cornerRadius = CornerRadius(PHONE_CORNER_DP.dp.toPx() * scale),
        style = Stroke(width = 2.dp.toPx() * scale),
    )
}

/**
 * Bloque de texto (título + subtítulo) centrado horizontal y verticalmente
 * dentro del área reservada para texto.
 */
internal fun DrawScope.drawTextBlock(
    textMeasurer: TextMeasurer,
    title: String,
    subtitle: String,
    textPosition: TextPosition,
    textAreaH: Float,
    textColor: Color,
) {
    val padH = 24.dp.toPx()
    val maxTextW = size.width - padH * 2f
    val gap = 7.dp.toPx()

    val titleStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.4).sp,
        color = textColor,
        textAlign = TextAlign.Center,
    )
    val subtitleStyle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 19.sp,
        color = textColor.copy(alpha = 0.74f),
        textAlign = TextAlign.Center,
    )

    val titleLayout =
        textMeasurer.measure(title, titleStyle, constraints = Constraints(maxWidth = maxTextW.roundToInt()))
    val subtitleLayout =
        textMeasurer.measure(subtitle, subtitleStyle, constraints = Constraints(maxWidth = maxTextW.roundToInt()))
    val totalH = titleLayout.size.height + gap + subtitleLayout.size.height

    val blockY = when (textPosition) {
        TOP -> (textAreaH - totalH) / 2f
        BOTTOM -> size.height - textAreaH + (textAreaH - totalH) / 2f
    }

    drawText(titleLayout, topLeft = Offset((size.width - titleLayout.size.width) / 2f, blockY))
    drawText(
        subtitleLayout,
        topLeft = Offset((size.width - subtitleLayout.size.width) / 2f, blockY + titleLayout.size.height + gap)
    )
}

/**
 * Dibuja un único bloque de texto centrado dentro de un área vertical arbitraria.
 *
 * @param areaTop Coordenada Y del borde superior del área (px).
 * @param areaH   Altura del área (px).
 */
internal fun DrawScope.drawSingleTextBlock(
    textMeasurer: TextMeasurer,
    text: String,
    style: TextStyle,
    areaTop: Float,
    areaH: Float,
) {
    val padH = 20.dp.toPx()
    val layout = textMeasurer.measure(
        text,
        style,
        constraints = Constraints(maxWidth = (size.width - padH * 2f).roundToInt()),
    )
    val x = (size.width - layout.size.width) / 2f
    val y = areaTop + (areaH - layout.size.height) / 2f
    drawText(layout, topLeft = Offset(x, y))
}