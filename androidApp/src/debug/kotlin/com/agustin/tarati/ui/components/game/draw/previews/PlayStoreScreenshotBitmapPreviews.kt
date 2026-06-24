package com.agustin.tarati.ui.components.game.draw.previews

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.ui.components.game.draw.board.drawBoardBackground
import com.agustin.tarati.ui.components.game.draw.playstore.BEZEL_BOTTOM_DP
import com.agustin.tarati.ui.components.game.draw.playstore.BEZEL_SIDE_DP
import com.agustin.tarati.ui.components.game.draw.playstore.BEZEL_TOP_DP
import com.agustin.tarati.ui.components.game.draw.playstore.CENTERED_TEXT_FRACTION
import com.agustin.tarati.ui.components.game.draw.playstore.PHONE_ASPECT_RATIO
import com.agustin.tarati.ui.components.game.draw.playstore.PHONE_W_DP
import com.agustin.tarati.ui.components.game.draw.playstore.SCREEN_CORNER_DP
import com.agustin.tarati.ui.components.game.draw.playstore.STORE_H_DP
import com.agustin.tarati.ui.components.game.draw.playstore.STORE_W_DP
import com.agustin.tarati.ui.components.game.draw.playstore.TEXT_AREA_FRACTION
import com.agustin.tarati.ui.components.game.draw.playstore.TextBlockSpec
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.BOTTOM
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.TOP
import com.agustin.tarati.ui.components.game.draw.playstore.drawBackgroundLayer
import com.agustin.tarati.ui.components.game.draw.playstore.drawCameraLens
import com.agustin.tarati.ui.components.game.draw.playstore.drawHomeIndicator
import com.agustin.tarati.ui.components.game.draw.playstore.drawPhoneBody
import com.agustin.tarati.ui.components.game.draw.playstore.drawPhoneShadow
import com.agustin.tarati.ui.components.game.draw.playstore.drawPhoneUnitShadow
import com.agustin.tarati.ui.components.game.draw.playstore.drawRimLight
import com.agustin.tarati.ui.components.game.draw.playstore.drawScreenInnerBorder
import com.agustin.tarati.ui.components.game.draw.playstore.drawScreenReflection
import com.agustin.tarati.ui.components.game.draw.playstore.drawSingleTextBlock
import com.agustin.tarati.ui.components.game.draw.playstore.drawTextBlock
import com.agustin.tarati.ui.components.game.draw.playstore.screenshotBackground
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.getBoardColors
import kotlin.math.roundToInt

// ══════════════════════════════════════════════════════════════════════════════
//  CAPTURAS CON IMAGEN ESTÁTICA (BITMAP / DRAWABLE)
//
//  Estas variantes aceptan un drawable pre-renderizado como contenido de la
//  pantalla del teléfono simulado. Útiles cuando ya existe una captura real
//  del dispositivo. Si [screenshotRes] es null se usa el tablero Tarati como
//  placeholder.
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Genera una imagen de captura de pantalla para Play Store de 1080 × 1920 px.
 *
 * ## Layout — TextPosition.TOP
 * ```
 * ┌──────────────────────────────┐
 * │  [título]                    │  ← ~24.5 % de altura
 * │  [subtítulo]                 │
 * │  ┌────────────────────────┐  │
 * │  │ ● STATUS BAR        🔋 │  │  ← bezel superior + cámara
 * │  │ ║   SCREENSHOT PNG   ║ │  │  → 82 % del teléfono visible
 * │  │ ║                    ║ │  │    (resto cortado fuera del canvas)
 * └──┴────────────────────────┴──┘
 * ```
 *
 * ## Layout — TextPosition.BOTTOM
 * ```
 * ┌──┬────────────────────────┬──┐
 * │  │ ║   SCREENSHOT PNG   ║ │  │  → 82 % del teléfono visible
 * │  │ ║                    ║ │  │
 * │  │ ──────  HOME BAR ────│ │  ← bezel inferior + home bar
 * │  └────────────────────────┘  │
 * │  [título]                    │  ← ~24.5 % de altura
 * │  [subtítulo]                 │
 * └──────────────────────────────┘
 * ```
 *
 * ## Reflejo sobre cristal
 * El gradiente diagonal cubre la pantalla completa (screenW × screenH) desde
 * la esquina superior-izquierda hacia la inferior-derecha. Se desvanece solo
 * sin corte recto; el `clipPath` de la pantalla es el único borde visible.
 *
 * ## Placeholder automático
 * Si [screenshotRes] es `null` o el drawable aún no existe, se renderiza el
 * tablero de Tarati con los colores de [palette] como placeholder.
 *
 * @param screenshotRes ID del drawable (`R.drawable.ss_01_es_classic`), o `null`.
 * @param title         Título principal. Máx. ~3 palabras cortas para evitar wrap.
 * @param subtitle      Descripción de apoyo. Máx. 2 líneas (~60 caracteres).
 * @param textPosition  [com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.TOP] o [com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.BOTTOM].
 * @param palette       Paleta de colores; define fondo, texto y placeholder.
 */
@SuppressLint("LocalContextResourcesRead")
@Composable
internal fun PlayStoreScreenshot(
    screenshotRes: Int? = null,
    title: String,
    subtitle: String,
    textPosition: TextPosition,
    palette: BoardPalette = ClassicPalette,
) {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val boardColors = getBoardColors(palette) // @Composable — capturar aquí, no dentro del Canvas

    val (gradTop, gradBottom) = screenshotBackground(palette)
    val textColor = if (gradTop.luminance() > 0.35f) Color(0xFF1A120A) else Color(0xFFF5F0EB)

    val screenshotBitmap: ImageBitmap? = remember(screenshotRes) {
        screenshotRes?.let { res ->
            runCatching {
                BitmapFactory.decodeResource(context.resources, res)?.asImageBitmap()
            }.getOrNull()
        }
    }

    Canvas(modifier = Modifier.size(STORE_W_DP.dp, STORE_H_DP.dp)) {

        val textAreaH = size.height * TEXT_AREA_FRACTION
        val phoneW = PHONE_W_DP.dp.toPx()
        val phoneH = phoneW * PHONE_ASPECT_RATIO
        val phoneX = (size.width - phoneW) / 2f
        val phoneY = when (textPosition) {
            TOP -> textAreaH
            BOTTOM -> size.height - textAreaH - phoneH
        }

        val sideBezel = BEZEL_SIDE_DP.dp.toPx()
        val topBezel = BEZEL_TOP_DP.dp.toPx()
        val botBezel = BEZEL_BOTTOM_DP.dp.toPx()
        val screenX = phoneX + sideBezel
        val screenY = phoneY + topBezel
        val screenW = phoneW - sideBezel * 2f
        val screenH = phoneH - topBezel - botBezel

        drawBackgroundLayer(gradTop, gradBottom, textPosition, size, boardColors)
        drawPhoneShadow(phoneX, phoneY, phoneW, phoneH, colors = boardColors)
        drawPhoneBody(Offset(phoneX, phoneY), Size(phoneW, phoneH))
        drawScreen(screenX, screenY, screenW, screenH, screenshotBitmap, boardColors)
        drawCameraLens(phoneX, phoneY, phoneW)
        drawHomeIndicator(phoneX, phoneY, phoneW, phoneH)
        drawRimLight(phoneX, phoneY, phoneW, phoneH)
        drawTextBlock(textMeasurer, title, subtitle, textPosition, textAreaH, textColor)
    }
}

/**
 * Área de pantalla: screenshot o placeholder + reflejo diagonal + borde.
 *
 * ## Reflejo sobre cristal
 * El [drawRect] cubre la pantalla completa (screenW × screenH). El gradiente
 * lineal va desde la esquina superior-izquierda hasta la inferior-derecha,
 * desvaneciendo a `Transparent` antes del extremo. Al no limitar el tamaño
 * del rect el reflejo llega hasta el borde inferior de la pantalla sin corte
 * recto alguno; el [clipPath] de la pantalla es el único límite visible.
 */
fun DrawScope.drawScreen(
    screenX: Float, screenY: Float, screenW: Float, screenH: Float,
    screenshot: ImageBitmap?,
    boardColors: BoardColors,
) {
    val screenPath = Path().apply {
        addRoundRect(
            RoundRect(
                left = screenX, top = screenY,
                right = screenX + screenW, bottom = screenY + screenH,
                cornerRadius = CornerRadius(SCREEN_CORNER_DP.dp.toPx()),
            )
        )
    }

    clipPath(screenPath) {
        // ── Contenido ─────────────────────────────────────────────────────────
        if (screenshot != null) {
            drawImage(
                image = screenshot,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(screenshot.width, screenshot.height),
                dstOffset = IntOffset(screenX.roundToInt(), screenY.roundToInt()),
                dstSize = IntSize(screenW.roundToInt(), screenH.roundToInt()),
            )
        } else {
            translate(left = screenX, top = screenY) {
                drawBoardBackground(
                    canvasSize = Size(screenW, screenH),
                    orientation = BoardOrientation.PORTRAIT_WHITE,
                    regionsVisible = true,
                    perimeterVisible = true,
                    colors = boardColors,
                )
            }
        }

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
 * Dibuja un teléfono completo (cuerpo, pantalla, cámara, home bar, borde)
 * a tamaño arbitrario. Todos los dp se escalan proporcionalmente a [phoneW].
 *
 * Úsalo dentro de un bloque `rotate(…) { }` para aplicar inclinación.
 */
fun DrawScope.drawPhoneUnit(
    phoneX: Float, phoneY: Float, phoneW: Float, phoneH: Float,
    screenshot: ImageBitmap?,
    boardColors: BoardColors,
) {
    val scale = phoneW / PHONE_W_DP.dp.toPx()
    val sideBezel = BEZEL_SIDE_DP.dp.toPx() * scale
    val topBezel = BEZEL_TOP_DP.dp.toPx() * scale
    val botBezel = BEZEL_BOTTOM_DP.dp.toPx() * scale
    val screenCorner = SCREEN_CORNER_DP.dp.toPx() * scale

    val screenX = phoneX + sideBezel
    val screenY = phoneY + topBezel
    val screenW = phoneW - sideBezel * 2f
    val screenH = phoneH - topBezel - botBezel

    // ── Cuerpo ────────────────────────────────────────────────────────────────
    drawPhoneBody(
        Offset(phoneX, phoneY),
        Size(phoneW, phoneH)
    )

    // ── Pantalla ──────────────────────────────────────────────────────────────
    drawScreenUnit(screenX, screenY, screenW, screenH, screenshot, boardColors, screenCorner)

    // ── Chrome: cámara, home bar, rim light ──────────────────────────────────
    drawCameraLens(phoneX, phoneY, phoneW, scale)
    drawHomeIndicator(phoneX, phoneY, phoneW, phoneH, scale)
    drawRimLight(phoneX, phoneY, phoneW, phoneH, scale)
}

/**
 * Pantalla de un teléfono escalado: screenshot o placeholder + reflejo + borde.
 * Acepta [cornerRadius] en px para adaptarse al tamaño del teléfono.
 */
fun DrawScope.drawScreenUnit(
    screenX: Float, screenY: Float, screenW: Float, screenH: Float,
    screenshot: ImageBitmap?,
    boardColors: BoardColors,
    cornerRadius: Float,
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
        if (screenshot != null) {
            drawImage(
                image = screenshot,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(screenshot.width, screenshot.height),
                dstOffset = IntOffset(screenX.roundToInt(), screenY.roundToInt()),
                dstSize = IntSize(screenW.roundToInt(), screenH.roundToInt()),
            )
        } else {
            translate(left = screenX, top = screenY) {
                drawBoardBackground(
                    canvasSize = Size(screenW, screenH),
                    orientation = BoardOrientation.PORTRAIT_WHITE,
                    regionsVisible = true,
                    perimeterVisible = true,
                    colors = boardColors,
                )
            }
        }

        drawScreenReflection(screenX, screenY, screenW, screenH)
    }

    drawScreenInnerBorder(
        x = screenX,
        y = screenY,
        width = screenW,
        height = screenH,
        cornerRadius = cornerRadius
    )
}

// ══════════════════════════════════════════════════════════════════════════════
//  VARIANTE EN ABANICO PARA "PERSONALIZÁ TU TABLERO"
//
//  Capturas necesarias (3 por idioma, una por paleta):
//    ss_01_es_classic.png  · ss_01_es_dark.png  · ss_01_es_nature.png
//    ss_01_en_classic.png  · ss_01_en_dark.png  · ss_01_en_nature.png
//
//  Cada imagen se asigna a una tarjeta del abanico:
//    izquierda → classic,  centro → dark,  derecha → nature
//  (intercambiable ajustando los parámetros de cada @Preview)
// ══════════════════════════════════════════════════════════════════════════════

// ── Fan — datos de una tarjeta ────────────────────────────────────────────────

internal data class FanCard(
    val screenshotRes: Int?,
    val palette: BoardPalette,
)

// ── Fan composable ────────────────────────────────────────────────────────────

/**
 * Captura de pantalla para Play Store con **abanico de 3 teléfonos**.
 *
 * ## Layout (texto siempre arriba — [TextPosition.TOP])
 * ```
 * ┌────────────────────────────────────┐
 * │          [título]                  │  ← ~24.5 % de altura
 * │        [subtítulo]                 │
 * │         ╱────────╲                 │
 * │   ╱─────│  gray  │─────╲           │  ← 3 teléfonos en abanico
 * │  │ clási│        │ dark │  nature  │    inclinados ±15°, ~90 % visible
 * │  │      │        │      │          │    cortados en el borde inferior
 * └────────────────────────────────────┘
 * ```
 *
 * Los tres teléfonos giran alrededor de un **pivot común** situado 30 dp
 * por debajo del borde inferior del canvas. La tarjeta central (0°) queda
 * en primer plano; las laterales (±[fanAngleDeg]) quedan detrás.
 *
 * @param left          Tarjeta izquierda: screenshot + paleta.
 * @param center        Tarjeta central: screenshot + paleta. Se dibuja al frente.
 * @param right         Tarjeta derecha:  screenshot + paleta.
 * @param title         Título principal. Máx. ~3 palabras.
 * @param subtitle      Subtítulo. Máx. 2 líneas.
 * @param bgPalette     Paleta del fondo del canvas (independiente de las tarjetas).
 * @param fanAngleDeg   Ángulo de apertura del abanico (por defecto 15°).
 */
@SuppressLint("LocalContextResourcesRead")
@Composable
internal fun PlayStoreScreenshotFan(
    left: FanCard,
    center: FanCard,
    right: FanCard,
    title: String,
    subtitle: String,
    bgPalette: BoardPalette = DarkPalette,
    fanAngleDeg: Float = 15f,
) {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()

    // Colores de cada tarjeta — @Composable, capturados fuera del Canvas.
    val leftColors = getBoardColors(left.palette)
    val centerColors = getBoardColors(center.palette)
    val rightColors = getBoardColors(right.palette)

    val (gradTop, gradBottom) = screenshotBackground(bgPalette)
    val textColor = if (gradTop.luminance() > 0.35f) Color(0xFF1A120A) else Color(0xFFF5F0EB)

    // Carga de bitmaps (puede ser null si el asset aún no existe).
    fun loadBitmap(res: Int?) = res?.let { r ->
        runCatching { BitmapFactory.decodeResource(context.resources, r)?.asImageBitmap() }.getOrNull()
    }

    val leftBitmap = remember(left.screenshotRes) { loadBitmap(left.screenshotRes) }
    val centerBitmap = remember(center.screenshotRes) { loadBitmap(center.screenshotRes) }
    val rightBitmap = remember(right.screenshotRes) { loadBitmap(right.screenshotRes) }

    Canvas(modifier = Modifier.size(STORE_W_DP.dp, STORE_H_DP.dp)) {

        // ── Geometría del abanico ─────────────────────────────────────────────
        val textAreaH = size.height * TEXT_AREA_FRACTION
        val fanPhoneW = 175.dp.toPx()
        val fanPhoneH = fanPhoneW * PHONE_ASPECT_RATIO
        val pivotX = size.width / 2f
        val pivotY = size.height + 30.dp.toPx()
        val basePhoneX = pivotX - fanPhoneW / 2f
        val basePhoneY = pivotY - fanPhoneH

        // Helper para aplicar rotación solo cuando hay ángulo
        fun DrawScope.withFanRotation(angle: Float, block: DrawScope.() -> Unit) {
            if (angle != 0f) rotate(angle, Offset(pivotX, pivotY), block) else block()
        }

        // Configuración de los 3 dispositivos: ángulo + contenido
        data class PhoneSlot(
            val angle: Float,
            val bitmap: ImageBitmap?,
            val colors: BoardColors
        )

        val phones = listOf(
            PhoneSlot(-fanAngleDeg, leftBitmap, leftColors),
            PhoneSlot(+fanAngleDeg, rightBitmap, rightColors),
            PhoneSlot(0f, centerBitmap, centerColors) // último = primer plano
        )

        // ── Fondo ─────────────────────────────────────────────────────────────
        drawBackgroundLayer(gradTop, gradBottom, TOP, size, centerColors)

        // ── Teléfonos (en orden: laterales detrás, centro al frente) ─────────
        phones.forEach { phone ->
            withFanRotation(phone.angle) {
                drawPhoneUnitShadow(basePhoneX, basePhoneY, fanPhoneW, fanPhoneH, phone.colors)
                drawPhoneUnit(basePhoneX, basePhoneY, fanPhoneW, fanPhoneH, phone.bitmap, phone.colors)
            }
        }

        // ── Texto ─────────────────────────────────────────────────────────────
        drawTextBlock(textMeasurer, title, subtitle, TOP, textAreaH, textColor)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  VARIANTE TELÉFONO CENTRADO COMPLETO
//
//  Diseñada para capturas donde la UI tiene controles relevantes tanto en la
//  zona superior (top bar) como inferior (bottom bar / botones de acción),
//  como el editor de tablero (ss_05).
//
//  Layout:
//    ┌──────────────────────────────┐
//    │   [título]                   │  ← ~13 % de altura
//    │                              │
//    │   ┌──────────────────────┐   │
//    │   │ ● STATUS BAR      🔋 │   │  top bar visible
//    │   │ ║                  ║ │   │
//    │   │ ║    SCREENSHOT    ║ │   │  teléfono al 100 %, sin corte
//    │   │ ║                  ║ │   │
//    │   │ ─────  HOME BAR ───  │   │  controles inferiores visibles
//    │   └──────────────────────┘   │
//    │                              │
//    │   [subtítulo]                │  ← ~13 % de altura
//    └──────────────────────────────┘
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Captura de pantalla para Play Store con **teléfono centrado completo**.
 *
 * El teléfono se escala para encajar en el espacio vertical disponible entre
 * los dos bloques de texto, con margen vertical proporcional. Al mostrarse
 * completo (sin corte) se ven tanto la top bar como la bottom bar del editor.
 *
 * @param screenshotRes ID del drawable, o `null` para usar el tablero como placeholder.
 * @param title         Título corto (máx. ~3 palabras), mostrado arriba.
 * @param subtitle      Subtítulo descriptivo (máx. 2 líneas), mostrado abajo.
 * @param palette       Paleta del fondo y del placeholder.
 */
@SuppressLint("LocalContextResourcesRead")
@Composable
internal fun PlayStoreScreenshotCentered(
    screenshotRes: Int? = null,
    title: String,
    subtitle: String,
    palette: BoardPalette = ClassicPalette,
) {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()
    val boardColors = getBoardColors(palette)

    val (gradTop, gradBottom) = screenshotBackground(palette)
    val textColor = if (gradTop.luminance() > 0.35f) Color(0xFF1A120A) else Color(0xFFF5F0EB)

    val screenshotBitmap: ImageBitmap? = remember(screenshotRes) {
        screenshotRes?.let { res ->
            runCatching {
                BitmapFactory.decodeResource(context.resources, res)?.asImageBitmap()
            }.getOrNull()
        }
    }

    Canvas(modifier = Modifier.size(STORE_W_DP.dp, STORE_H_DP.dp)) {

        // ── Áreas de texto (arriba y abajo) ──────────────────────────────────
        val topTextH = size.height * CENTERED_TEXT_FRACTION
        val botTextH = size.height * CENTERED_TEXT_FRACTION

        // ── Espacio disponible para el teléfono ───────────────────────────────
        // Margen vertical interno: 4 % del canvas a cada lado del teléfono.
        val vMargin = size.height * 0.04f
        val phoneAreaTop = topTextH + vMargin
        val phoneAreaBot = size.height - botTextH - vMargin
        val phoneAreaH = phoneAreaBot - phoneAreaTop

        // El ancho del teléfono se deduce de la altura disponible para que quepa
        // completo. Se limita además al 65 % del ancho del canvas.
        val phoneHFromArea = phoneAreaH
        val phoneWFromH = phoneHFromArea / PHONE_ASPECT_RATIO
        val phoneW = minOf(phoneWFromH, size.width * 0.65f)
        val phoneH = phoneW * PHONE_ASPECT_RATIO

        // Centrado horizontal y vertical dentro del área disponible.
        val phoneX = (size.width - phoneW) / 2f
        val phoneY = phoneAreaTop + (phoneAreaH - phoneH) / 2f

        // Borde exterior del teléfono (rim light)
        val scale = phoneW / PHONE_W_DP.dp.toPx()

        // ── Dibujo ────────────────────────────────────────────────────────────
        drawBackgroundLayer(gradTop, gradBottom, TOP, size, boardColors)
        drawPhoneUnitShadow(phoneX, phoneY, phoneW, phoneH, boardColors)
        drawPhoneUnit(phoneX, phoneY, phoneW, phoneH, screenshotBitmap, boardColors)
        drawRimLight(phoneX, phoneY, phoneW, phoneH, scale)

        listOf(
            // ── Título arriba ─────────────────────────────────────────────────
            TextBlockSpec(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                ),
                areaTopDp = 0f,
                areaHDp = topTextH
            ),
            // ── Subtítulo abajo ───────────────────────────────────────────────
            TextBlockSpec(
                text = subtitle,
                style = TextStyle(
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp,
                    color = textColor.copy(alpha = 0.74f),
                    textAlign = TextAlign.Center
                ),
                areaTopDp = size.height - botTextH,
                areaHDp = botTextH
            )
        ).forEach { block ->
            drawSingleTextBlock(
                textMeasurer = textMeasurer,
                text = block.text,
                style = block.style,
                areaTop = block.areaTopDp,
                areaH = block.areaHDp
            )
        }
    }
}