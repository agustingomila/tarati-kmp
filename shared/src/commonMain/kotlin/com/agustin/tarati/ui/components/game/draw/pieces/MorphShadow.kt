package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agustin.tarati.ui.components.game.draw.common.MorphShape

/**
 * Sombra que sigue el contorno exacto de una [MorphShape].
 *
 * Esta función tiene implementaciones específicas de plataforma:
 * - **Android**: sombra con blur usando BlurMaskFilter (alta calidad)
 * - **Desktop**: sombra simplificada sin blur (fallback)
 *
 * @param shape Forma del polígono
 * @param elevation Elevación que determina el radio del blur
 * @param color Color de la sombra (típicamente negro semitransparente)
 * @param offsetX Desplazamiento horizontal de la sombra
 * @param offsetY Desplazamiento vertical de la sombra
 */
expect fun Modifier.morphShadow(
    shape: MorphShape,
    elevation: Dp = 4.dp,
    color: Color = Color.Black.copy(alpha = 0.28f),
    offsetX: Dp = 0.dp,
    offsetY: Dp = 2.dp,
): Modifier