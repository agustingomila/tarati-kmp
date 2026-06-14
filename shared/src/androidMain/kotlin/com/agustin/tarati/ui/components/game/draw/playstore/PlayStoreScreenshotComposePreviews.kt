package com.agustin.tarati.ui.components.game.draw.playstore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.BOTTOM
import com.agustin.tarati.ui.components.game.draw.playstore.TextPosition.TOP
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.getBoardColors

// ══════════════════════════════════════════════════════════════════════════════
//  CAPTURAS CON CONTENIDO COMPOSE
//
//  Variantes que aceptan un composable como contenido de la pantalla del
//  teléfono simulado. Permiten capturar UI real de la app — con estado,
//  temas y animaciones — directamente dentro del frame del teléfono.
// ══════════════════════════════════════════════════════════════════════════════

// ══════════════════════════════════════════════════════════════════════════════
// ══════════════════════════════════════════════════════════════════════════════
//  VARIANTE CON CONTENIDO COMPOSE
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Variante de [PlayStoreComposeScreenshot] que acepta un composable como contenido
 * de la pantalla del teléfono en lugar de un drawable estático.
 *
 * La pantalla se construye con capas [Box] en lugar de un [Canvas] puro,
 * permitiendo que cualquier UI de Compose se renderice dentro del marco:
 *
 *   Capa 1 Canvas — fondo degradado + texto
 *   Capa 2 Canvas — sombra + cuerpo del teléfono
 *   Capa 3 Box    — [content] recortado al rectángulo de pantalla
 *   Capa 4 Canvas — reflejo de vidrio + borde + cámara + home bar + rim
 *
 * @param title         Título principal (máx. ~3 palabras).
 * @param subtitle      Subtítulo (máx. 2 líneas).
 * @param textPosition  [TextPosition.TOP] o [TextPosition.BOTTOM].
 * @param palette       Paleta de colores del fondo y chrome del teléfono.
 * @param content       UI de Compose que se renderiza dentro de la pantalla.
 */
@Composable
internal fun PlayStoreComposeScreenshot(
    title: String,
    subtitle: String,
    textPosition: TextPosition,
    palette: BoardPalette = ClassicPalette,
    content: @Composable () -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val (gradTop, gradBottom) = screenshotBackground(palette)
    val textColor = if (gradTop.luminance() > 0.35f) Color(0xFF1A120A) else Color(0xFFF5F0EB)
    val boardColors = getBoardColors(palette)

    // Geometría en dp — mismas constantes que el modo bitmap, sin conversión px
    val phoneWDp = PHONE_W_DP.dp
    val phoneHDp = (PHONE_W_DP * PHONE_ASPECT_RATIO).dp
    val phoneXDp = ((STORE_W_DP - PHONE_W_DP) / 2f).dp
    val textAreaHDp = (STORE_H_DP * TEXT_AREA_FRACTION).dp
    val phoneYDp = when (textPosition) {
        TOP -> textAreaHDp
        BOTTOM -> STORE_H_DP.dp - textAreaHDp - phoneHDp
    }
    val screenXDp = phoneXDp + BEZEL_SIDE_DP.dp
    val screenYDp = phoneYDp + BEZEL_TOP_DP.dp
    val screenWDp = phoneWDp - BEZEL_SIDE_DP.dp * 2
    val screenHDp = phoneHDp - BEZEL_TOP_DP.dp - BEZEL_BOTTOM_DP.dp

    Box(
        modifier = Modifier
            .size(STORE_W_DP.dp, STORE_H_DP.dp)
            .clipToBounds()
    ) {
        // Capa 1: fondo + tablero tenue + texto
        Canvas(modifier = Modifier.matchParentSize()) {
            drawBackgroundLayer(gradTop, gradBottom, textPosition, size, boardColors)
            drawTextBlock(
                textMeasurer, title, subtitle, textPosition,
                size.height * TEXT_AREA_FRACTION, textColor
            )
        }

        // Capa 2: sombra + cuerpo del teléfono + rim light
        // El rim light va aquí (no en Capa 4) porque el Layout de Capa 3 renderiza
        // encima de la Capa 4, tapando el borde del teléfono que coincide con la pantalla.
        Canvas(modifier = Modifier.matchParentSize()) {
            val px = phoneXDp.toPx()
            val py = phoneYDp.toPx()
            val pw = phoneWDp.toPx()
            val ph = phoneHDp.toPx()
            drawPhoneShadow(px, py, pw, ph, 1f, boardColors)
            drawPhoneBody(Offset(px, py), Size(pw, ph))
            drawRimLight(px, py, pw, ph)
        }

        // Capa 3: contenido Compose a tamaño de teléfono real, escalado al frame.
        //
        // POSICIONAMIENTO: absoluteOffset { IntOffset } (versión lambda) es un
        // LayoutModifier — cambia la posición real en el layout, no solo la capa
        // visual. La versión Dp de absoluteOffset es graphicsLayer-only, lo que
        // hace que el clip() se aplique en (0,0) del canvas en vez de en la
        // posición de la pantalla del teléfono, causando desplazamiento.
        //
        // ESCALA: Layout mide el contenido a CONTENT_W × CONTENT_H (Pixel 5) y lo
        // coloca con placeWithLayer(scaleX, scaleY, TransformOrigin.TopStart) para
        // que se vea como una captura de pantalla de alta densidad.
        // Capa 3 — el contenido Compose se desplaza hacia abajo STATUS_BAR_CONTENT_OFFSET_DP
        // para que la UI de la app no quede solapada con la barra de estado simulada,
        // replicando el efecto de showSystemUi = true + WindowInsets.statusBars.
        Layout(
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = STATUS_BAR_CONTENT_OFFSET_DP.dp)
                ) { content() }
            },
            modifier = Modifier
                .absoluteOffset { IntOffset(screenXDp.roundToPx(), screenYDp.roundToPx()) }
                .size(screenWDp, screenHDp)
                .clip(RoundedCornerShape(SCREEN_CORNER_DP.dp)),
        ) { measurables, constraints ->
            val contentWPx = (CONTENT_W_DP.dp).roundToPx()
            val contentHPx = (CONTENT_H_DP.dp).roundToPx()
            val placeables = measurables.map {
                it.measure(Constraints.fixed(contentWPx, contentHPx))
            }
            val scaleX = constraints.maxWidth.toFloat() / contentWPx
            val scaleY = constraints.maxHeight.toFloat() / contentHPx
            layout(constraints.maxWidth, constraints.maxHeight) {
                placeables.forEach { placeable ->
                    placeable.placeWithLayer(x = 0, y = 0) {
                        this.scaleX = scaleX
                        this.scaleY = scaleY
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                }
            }
        }

        // Capa 4: cristal + chrome (reflejo, borde, cámara, home bar, rim light)
        PhoneChromeLayer(
            phoneX = phoneXDp, phoneY = phoneYDp,
            phoneW = phoneWDp, phoneH = phoneHDp,
            screenX = screenXDp, screenY = screenYDp,
            screenW = screenWDp, screenH = screenHDp,
            topBezel = BEZEL_TOP_DP.dp, botBezel = BEZEL_BOTTOM_DP.dp,
            phoneCorner = PHONE_CORNER_DP.dp, screenCorner = SCREEN_CORNER_DP.dp,
            boardColors = boardColors, textMeasurer = textMeasurer
        )
    }
}


// ══════════════════════════════════════════════════════════════════════════════
//  VARIANTE ABANICO CON CONTENIDO COMPOSE
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Abanico de 3 teléfonos con contenido Compose real en cada tarjeta.
 *
 * Misma geometría que [PlayStoreScreenshotFan] (bitmaps): teléfonos de 145dp,
 * pivot compartido 30dp bajo el canvas, rotaciones ±[fanAngleDeg].
 *
 * Arquitectura de cada tarjeta:
 *   Canvas (fondo negro + gradiente lateral)
 *   Layout  (contenido Compose escalado de CONTENT_W×CONTENT_H → screenW×screenH)
 *   Canvas  (reflejo de cristal + borde + cámara + home bar + rim light)
 * Los tres grupos se posicionan con [absoluteOffset] + [graphicsLayer] rotationZ.
 *
 * @param leftContent   Composable de la tarjeta izquierda.
 * @param centerContent Composable de la tarjeta central (primer plano).
 * @param rightContent  Composable de la tarjeta derecha.
 * @param title         Título principal.
 * @param subtitle      Subtítulo (máx. 2 líneas).
 * @param bgPalette     Paleta del fondo del canvas.
 * @param fanAngleDeg   Ángulo de apertura del abanico (por defecto 15°).
 */
@Composable
internal fun PlayStoreScreenshotFanCompose(
    leftContent: @Composable () -> Unit,
    centerContent: @Composable () -> Unit,
    rightContent: @Composable () -> Unit,
    leftPalette: BoardPalette,
    centerPalette: BoardPalette,
    rightPalette: BoardPalette,
    title: String,
    subtitle: String,
    bgPalette: BoardPalette = DarkPalette,
    fanAngleDeg: Float = 15f,
) {
    val textMeasurer = rememberTextMeasurer()
    val (gradTop, gradBottom) = screenshotBackground(bgPalette)
    val textColor = if (gradTop.luminance() > 0.35f) Color(0xFF1A120A) else Color(0xFFF5F0EB)
    val boardColors = getBoardColors(bgPalette)

    // ── Geometría del abanico (mismos valores que PlayStoreScreenshotFan) ──────
    val fanPhoneWDp = 175f
    val fanPhoneHDp = fanPhoneWDp * PHONE_ASPECT_RATIO
    val pivotXDp = STORE_W_DP / 2f
    val pivotYDp = STORE_H_DP + 30f
    val basePhoneXDp = pivotXDp - fanPhoneWDp / 2f
    val basePhoneYDp = pivotYDp - fanPhoneHDp
    val textAreaHDp = STORE_H_DP * TEXT_AREA_FRACTION

    // Bezeles proporcionales al teléfono reducido
    val scale = fanPhoneWDp / PHONE_W_DP
    val sideBezelDp = BEZEL_SIDE_DP * scale
    val topBezelDp = BEZEL_TOP_DP * scale
    val botBezelDp = BEZEL_BOTTOM_DP * scale
    val phoneCornerDp = PHONE_CORNER_DP * scale
    val screenCornerDp = SCREEN_CORNER_DP * scale

    val screenXDp = basePhoneXDp + sideBezelDp
    val screenYDp = basePhoneYDp + topBezelDp
    val screenWDp = fanPhoneWDp - sideBezelDp * 2f
    val screenHDp = fanPhoneHDp - topBezelDp - botBezelDp

    @Composable
    fun PhoneCard(
        rotationDeg: Float,
        palette: BoardPalette,
        composableContent: @Composable () -> Unit,
    ) {
        // Cada tarjeta es un Box de tamaño completo del canvas con graphicsLayer para
        // la rotación. Los children se posicionan con offset() en coordenadas locales
        // del box rotado — evita el conflicto entre size(360×640) + absoluteOffset +
        // size(screenW×screenH) que causaba escala y recorte incorrectos.
        val pivotFracX = pivotXDp / STORE_W_DP
        val pivotFracY = pivotYDp / STORE_H_DP

        Box(
            modifier = Modifier
                .size(STORE_W_DP.dp, STORE_H_DP.dp)
                .graphicsLayer(
                    rotationZ = rotationDeg,
                    transformOrigin = TransformOrigin(pivotFracX, pivotFracY),
                ),
        ) {
            // Capa A: cuerpo del teléfono (dibujado en coordenadas absolutas del canvas)
            Canvas(Modifier.matchParentSize()) {
                val px = basePhoneXDp.dp.toPx()
                val py = basePhoneYDp.dp.toPx()
                val pw = fanPhoneWDp.dp.toPx()
                val ph = fanPhoneHDp.dp.toPx()

                drawPhoneBody(
                    topLeft = Offset(px, py),
                    size = Size(pw, ph)
                )
            }

            // Capa B: contenido Compose posicionado con offset() en coordenadas locales
            // del box rotado, luego escalado de CONTENT_W×CONTENT_H → screenW×screenH.
            // offset() (Dp, layout modifier) posiciona correctamente dentro del Box rotado,
            // a diferencia de absoluteOffset que opera en coordenadas del padre sin rotar.
            Box(
                modifier = Modifier
                    .offset(screenXDp.dp, screenYDp.dp)
                    .size(screenWDp.dp, screenHDp.dp)
                    .clip(RoundedCornerShape(screenCornerDp.dp)),
            ) {
                ScaledContentLayout {
                    composableContent()
                }
            }

            // Capa C: cristal + chrome sobre la pantalla
            PhoneChromeLayer(
                phoneX = basePhoneXDp.dp, phoneY = basePhoneYDp.dp,
                phoneW = fanPhoneWDp.dp, phoneH = fanPhoneHDp.dp,
                screenX = screenXDp.dp, screenY = screenYDp.dp,
                screenW = screenWDp.dp, screenH = screenHDp.dp,
                topBezel = topBezelDp.dp, botBezel = botBezelDp.dp,
                phoneCorner = phoneCornerDp.dp, screenCorner = screenCornerDp.dp,
                boardColors = boardColors, textMeasurer = textMeasurer
            )
        }
    }

    Box(
        modifier = Modifier
            .size(STORE_W_DP.dp, STORE_H_DP.dp)
            .clipToBounds()
    ) {
        // Capa 1: fondo + texto
        Canvas(modifier = Modifier.matchParentSize()) {
            drawBackgroundLayer(gradTop, gradBottom, TOP, size, boardColors)
            drawTextBlock(textMeasurer, title, subtitle, TOP, size.height * TEXT_AREA_FRACTION, textColor)
        }

        data class PhoneSlot(
            val angle: Float,
            val palette: BoardPalette,
            val content: @Composable () -> Unit,
            val shadowModifier: Modifier
        )

        // Tarjetas y Sombras
        listOf(
            PhoneSlot(
                angle = -fanAngleDeg,
                palette = leftPalette,
                content = leftContent,
                shadowModifier = Modifier
                    .size(STORE_W_DP.dp, STORE_H_DP.dp)
                    .graphicsLayer(
                        rotationZ = -fanAngleDeg,
                        transformOrigin = TransformOrigin(pivotXDp / STORE_W_DP, pivotYDp / STORE_H_DP)
                    )
            ),
            PhoneSlot(
                angle = +fanAngleDeg,
                palette = rightPalette,
                content = rightContent,
                shadowModifier = Modifier
                    .size(STORE_W_DP.dp, STORE_H_DP.dp)
                    .graphicsLayer(
                        rotationZ = +fanAngleDeg,
                        transformOrigin = TransformOrigin(pivotXDp / STORE_W_DP, pivotYDp / STORE_H_DP)
                    )
            ),
            PhoneSlot(
                angle = 0f,
                palette = centerPalette,
                content = centerContent,
                shadowModifier = Modifier.matchParentSize()
            )
        ).forEach { slot ->
            // Sombra
            Canvas(modifier = slot.shadowModifier) {
                drawPhoneUnitShadow(
                    basePhoneXDp.dp.toPx(), basePhoneYDp.dp.toPx(),
                    fanPhoneWDp.dp.toPx(), fanPhoneHDp.dp.toPx(),
                    boardColors
                )
            }

            // Tarjeta
            PhoneCard(slot.angle, slot.palette, slot.content)
        }
    }
}


/**
 * Variante de [PlayStoreScreenshotCentered] que acepta un composable como
 * contenido de la pantalla. El teléfono se muestra completo (sin corte),
 * visible tanto la top bar como la bottom bar.
 *
 * Geometría idéntica a la versión bitmap: el ancho se deriva de la altura
 * disponible entre los dos bloques de texto, limitado al 65 % del canvas.
 *
 * @param title         Título corto, mostrado arriba (máx. ~3 palabras).
 * @param subtitle      Subtítulo descriptivo (máx. 2 líneas), mostrado abajo.
 * @param palette       Paleta del fondo del canvas.
 * @param content       UI de Compose que se renderiza dentro de la pantalla.
 */
@Composable
internal fun PlayStoreScreenshotCentered(
    title: String,
    subtitle: String,
    palette: BoardPalette = ClassicPalette,
    content: @Composable () -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val (gradTop, gradBottom) = screenshotBackground(palette)
    val textColor = if (gradTop.luminance() > 0.35f) Color(0xFF1A120A) else Color(0xFFF5F0EB)
    val boardColors = getBoardColors(palette)

    // ── Geometría en dp — espejea los cálculos del modo bitmap ────────────────
    val topTextHDp = STORE_H_DP * CENTERED_TEXT_FRACTION
    val botTextHDp = STORE_H_DP * CENTERED_TEXT_FRACTION
    val vMarginDp = STORE_H_DP * 0.04f
    val phoneAreaTopDp = topTextHDp + vMarginDp
    val phoneAreaBotDp = STORE_H_DP - botTextHDp - vMarginDp
    val phoneAreaHDp = phoneAreaBotDp - phoneAreaTopDp

    val phoneWDp = minOf(phoneAreaHDp / PHONE_ASPECT_RATIO, STORE_W_DP * 0.65f)
    val phoneHDp = phoneWDp * PHONE_ASPECT_RATIO

    val phoneXDp = (STORE_W_DP - phoneWDp) / 2f
    val phoneYDp = phoneAreaTopDp + (phoneAreaHDp - phoneHDp) / 2f

    val scale = phoneWDp / PHONE_W_DP
    val sideBezelDp = BEZEL_SIDE_DP * scale
    val topBezelDp = BEZEL_TOP_DP * scale
    val botBezelDp = BEZEL_BOTTOM_DP * scale
    val phoneCornerDp = PHONE_CORNER_DP * scale
    val screenCornerDp = SCREEN_CORNER_DP * scale

    val screenXDp = phoneXDp + sideBezelDp
    val screenYDp = phoneYDp + topBezelDp
    val screenWDp = phoneWDp - sideBezelDp * 2f
    val screenHDp = phoneHDp - topBezelDp - botBezelDp

    Box(
        modifier = Modifier
            .size(STORE_W_DP.dp, STORE_H_DP.dp)
            .clipToBounds(),
    ) {
        // Capa 1: fondo + textos arriba y abajo
        Canvas(modifier = Modifier.matchParentSize()) {
            drawBackgroundLayer(gradTop, gradBottom, TOP, size, boardColors)
            drawPhoneUnitShadow(
                phoneXDp.dp.toPx(), phoneYDp.dp.toPx(),
                phoneWDp.dp.toPx(), phoneHDp.dp.toPx(), boardColors
            )

            // Cuerpo del teléfono
            drawPhoneBody(
                topLeft = Offset(phoneXDp.dp.toPx(), phoneYDp.dp.toPx()),
                size = Size(phoneWDp.dp.toPx(), phoneHDp.dp.toPx())
            )

            listOf(
                // Texto superior (Título)
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
                    areaHDp = topTextHDp
                ),
                // Texto inferior (Subtítulo)
                TextBlockSpec(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 18.sp,
                        color = textColor.copy(alpha = 0.74f),
                        textAlign = TextAlign.Center
                    ),
                    areaTopDp = STORE_H_DP - botTextHDp,
                    areaHDp = botTextHDp
                )
            ).forEach { spec ->
                drawSingleTextBlock(
                    textMeasurer = textMeasurer,
                    text = spec.text,
                    style = spec.style,
                    areaTop = spec.areaTopDp.dp.toPx(),
                    areaH = spec.areaHDp.dp.toPx()
                )
            }
        }

        // Capa 2: contenido Compose escalado, posicionado en el recuadro de pantalla
        ScaledContentLayout(
            modifier = Modifier
                .absoluteOffset { IntOffset(screenXDp.dp.roundToPx(), screenYDp.dp.roundToPx()) }
                .size(screenWDp.dp, screenHDp.dp)
                .clip(RoundedCornerShape(screenCornerDp.dp))
        ) {
            content()
        }

        // Capa 3: cristal + chrome (reflejo, borde, cámara, home bar, rim light)
        PhoneChromeLayer(
            phoneX = phoneXDp.dp, phoneY = phoneYDp.dp,
            phoneW = phoneWDp.dp, phoneH = phoneHDp.dp,
            screenX = screenXDp.dp, screenY = screenYDp.dp,
            screenW = screenWDp.dp, screenH = screenHDp.dp,
            topBezel = topBezelDp.dp, botBezel = botBezelDp.dp,
            phoneCorner = phoneCornerDp.dp, screenCorner = screenCornerDp.dp,
            boardColors = boardColors, textMeasurer = textMeasurer
        )
    }
}

@Composable
private fun ScaledContentLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = STATUS_BAR_CONTENT_OFFSET_DP.dp)
            ) {
                content()
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val cWPx = CONTENT_W_DP.dp.roundToPx()
        val cHPx = CONTENT_H_DP.dp.roundToPx()
        val placeables = measurables.map { it.measure(Constraints.fixed(cWPx, cHPx)) }
        val sx = constraints.maxWidth.toFloat() / cWPx
        val sy = constraints.maxHeight.toFloat() / cHPx

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach {
                it.placeWithLayer(0, 0) {
                    scaleX = sx
                    scaleY = sy
                    transformOrigin = TransformOrigin(0f, 0f)
                }
            }
        }
    }
}

@Composable
private fun PhoneChromeLayer(
    phoneX: Dp, phoneY: Dp, phoneW: Dp, phoneH: Dp,
    screenX: Dp, screenY: Dp, screenW: Dp, screenH: Dp,
    topBezel: Dp, botBezel: Dp, phoneCorner: Dp, screenCorner: Dp,
    boardColors: BoardColors, textMeasurer: TextMeasurer
) {
    Canvas(Modifier.fillMaxSize()) {
        val px = phoneX.toPx()
        val py = phoneY.toPx()
        val pw = phoneW.toPx()
        val ph = phoneH.toPx()
        val sc = pw / PHONE_W_DP.dp.toPx()
        val sx = screenX.toPx()
        val sy = screenY.toPx()
        val sw = screenW.toPx()
        val sh = screenH.toPx()

        drawStatusBarOverlay(
            screenX = sx, screenY = sy, screenW = sw,
            textMeasurer = textMeasurer, boardColors = boardColors, scale = sc
        )

        drawScreenGlassOverlay(
            screenX = sx, screenY = sy, screenW = sw, screenH = sh,
            cornerRadius = screenCorner.toPx()
        )

        // Chrome: cámara, home bar, rim light
        drawCameraLens(px, py, pw, sc)
        drawHomeIndicator(px, py, pw, ph, sc)
        drawRimLight(px, py, pw, ph, sc)
    }
}