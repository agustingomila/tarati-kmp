package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Dibuja la sombra de una pieza poligonal durante animación de volteo.
 *
 * Esta función tiene implementaciones específicas de plataforma:
 * - **Android**: Sombra compleja con Matrix transform + doble blur (umbra + penumbra)
 * - **Desktop**: Sombra simplificada sin blur ni transformaciones complejas
 *
 * ## Algoritmo (Android)
 * 1. Transforma el path con Matrix afín direccional
 * 2. Dibuja umbra (sombra principal) con BlurMaskFilter
 * 3. Si sinA > 0.05, dibuja penumbra (halo exterior) con blur mayor
 *
 * ## Algoritmo (Desktop)
 * 1. Dibuja el path sin transformación
 * 2. Usa múltiples capas con alpha decreciente para simular blur
 *
 * @param params Parámetros pre-calculados de la sombra (ver [MorphFlipShadowParams])
 */
expect fun DrawScope.drawMorphFlipShadow(params: MorphFlipShadowParams)