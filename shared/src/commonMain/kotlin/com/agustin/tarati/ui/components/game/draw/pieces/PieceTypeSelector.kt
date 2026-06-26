package com.agustin.tarati.ui.components.game.draw.pieces

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.services.billing.OwnedProducts
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// PieceTypeSelector
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Selector visual de tipo de pieza para Settings.
 *
 * Cada tile rota continuamente alternando WHITE↔BLACK con las transiciones
 * de color siempre en el canto (nunca de frente). La vuelta completa se
 * implementa como dos medias vueltas de [drawMorphFlip] consecutivas:
 * la primera con [CobColor.WHITE], la segunda con [CobColor.BLACK].
 * Con desfases de `1/N` por índice, el carrusel mantiene piezas en
 * ángulos distintos a la vez.
 */
@Composable
fun PieceTypeSelector(
    selectedId: String,
    boardColors: BoardColors,
    onSelect: (PieceType) -> Unit,
    onPurchase: (PieceType) -> Unit = {},
    purchasedIds: OwnedProducts = OwnedProducts.None,
    pieceTypes: PieceTypeList = PieceTypeList.All,
    tileSize: Dp = 80.dp,
    flipDurationMs: Int = 3_600,
) {
    // Un único animador compartido — todos los tiles derivan su progreso de aquí.
    val transition = rememberInfiniteTransition(label = "piece_type_flip")
    val masterProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = flipDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "master_progress",
    )

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch { listState.scrollBy(-dragAmount.x) }
                }
            },
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(pieceTypes.items) { index, pieceType ->
            val isSelected = pieceType.id == selectedId
            // Gate supporter (C4): las piezas premium quedan bloqueadas salvo supporter / compra.
            // purchasedIds ya es el ownership efectivo (supporter incluye todos los piece_*).
            val isLocked = pieceType.isPremium && pieceType.productId.orEmpty() !in purchasedIds

            // Desfase uniforme entre tiles: con N=7, cada pieza arranca a 1/7 de
            // ciclo del anterior. Ningún par de piezas coincide en el mismo ángulo.
            val phaseOffset = index.toFloat() / pieceTypes.items.size
            val raw = (masterProgress + phaseOffset) % 1f
            // raw ∈ [0.0, 0.5): WHITE  raw ∈ [0.5, 1.0): BLACK
            // q ∈ [0.0, 0.25): emergencia del canto → cara (tp 0.5→0, axis=270)
            // q ∈ [0.25, 0.5): cara → canto          (tp 0→0.5, axis=90)
            //
            // axisAngleDeg=270 con tp decreciente = misma dirección visual que
            // axisAngleDeg=90 con tp creciente (dos inversiones se cancelan).
            // El color solo cambia en el canto (scale=0), donde la guarda es invisible.
            val tileCobColor = if (raw < 0.5f) CobColor.WHITE else CobColor.BLACK
            val q = raw % 0.5f
            val tileProgress = kotlin.math.abs(q - 0.25f) * 2f  // 0.5→0→0.5 (V shape)
            val tileAxisAngle = if (q < 0.25f) 270f else 90f

            PieceTypeTile(
                pieceType = pieceType,
                flipProgress = tileProgress,
                cobColor = tileCobColor,
                axisAngleDeg = tileAxisAngle,
                isSelected = isSelected,
                isLocked = isLocked,
                boardColors = boardColors,
                tileSize = tileSize,
                onClick = { if (isLocked) onPurchase(pieceType) else onSelect(pieceType) },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PieceTypeTile
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tile individual: pieza animada + etiqueta.
 *
 * @param flipProgress  Progreso en [0, 0.5] — controlado por [PieceTypeSelector].
 * @param cobColor      Color de la cara frontal del segmento actual.
 * @param axisAngleDeg  Eje de rotación. Alternando 270° (emergencia) y 90° (rotación al
 *                      canto) produce movimiento siempre en el mismo sentido visual.
 */
@Composable
fun PieceTypeTile(
    pieceType: PieceType,
    flipProgress: Float,
    cobColor: CobColor = CobColor.WHITE,
    axisAngleDeg: Float = 90f,
    isSelected: Boolean,
    isLocked: Boolean = false,
    boardColors: BoardColors,
    tileSize: Dp = 80.dp,
    onClick: () -> Unit,
) {
    // El selector muestra siempre la guarda y el motivo central del tipo de pieza,
    // incluyendo el motivo de Rok (centerMotif), para que el usuario vea la apariencia
    // premium completa antes de comprar. En partida real el centro solo aparece en Roks.
    val cobShape = remember(pieceType) {
        CobShape(
            shape = pieceType.shape,
            borderPattern = pieceType.borderPattern,
            centerMotif = pieceType.centerMotif,
        )
    }
    val accentColor = MaterialTheme.colorScheme.primary
    val borderModifier = when {
        isSelected -> Modifier.border(width = 2.dp, color = accentColor, shape = RoundedCornerShape(12.dp))
        isLocked -> Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            shape = RoundedCornerShape(12.dp)
        )

        else -> Modifier
    }
    val labelStyle = when {
        isSelected -> MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = accentColor)
        isLocked -> MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.5f
            )
        )

        else -> MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .then(borderModifier)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            ShapeFlipCobAnimated(
                cobShape = cobShape,
                cobColor = cobColor,
                boardColors = boardColors,
                axisAngleDeg = axisAngleDeg,
                size = tileSize,
                staticProgress = flipProgress,
                modifier = if (isLocked) Modifier.alpha(0.35f) else Modifier,
            )
            if (isLocked) {
                Icon(
                    imageVector = TaratiIcons.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    modifier = Modifier.size(tileSize * 0.4f),
                )
            }
        }
        Text(
            text = localizedString(pieceType.nameRes),
            style = labelStyle,
        )
    }
}