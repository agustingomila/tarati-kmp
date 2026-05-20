package com.agustin.tarati.ui.components.carditem


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Componente genérico de card para mostrar información de una partida.
 *
 * Usado tanto en [GamesLibraryScreen] (partidas guardadas) como en
 * [OnlineLobbyScreen] (partidas en vivo e historial online). El estilo
 * visual es compartido para que cualquier cambio futuro se propague a
 * todos los contextos automáticamente.
 *
 * ## Layout
 * ```
 * ┌────────────────────────────────────────────────────────┐
 * │  [leading]      título                       [badge]   │
 * │                 subtítulo                              │
 * │                 fila1label  ·  fila1value              │
 * │                 fila2label  ·  fila2value              │
 * └────────────────────────────────────────────────────────┘
 * ```
 *
 * @param title          Línea principal (p.ej. "Bot Medium vs Jugador1").
 * @param subtitle       Línea secundaria opcional (p.ej. "Blitz 3+2 · RATED").
 * @param rows           Pares (label, value) para las filas de datos. Máximo 3.
 * @param badge          Texto de badge opcional en la esquina superior derecha.
 * @param badgeColor     Color del badge. Default: [MaterialTheme.colorScheme.primary].
 * @param leadingIcon    Ícono vectorial opcional a la izquierda.
 * @param leadingContent Composable arbitrario a la izquierda. Tiene precedencia
 *                       sobre [leadingIcon] cuando ambos están presentes.
 *                       Usar para miniaturas del tablero ([StaticBoardRenderer]).
 * @param customRows     Composable renderizado después de [rows] — para filas con contenido visual.
 * @param isHighlighted  Si true, usa [primaryContainer] como fondo.
 * @param onClick        Callback al tocar la card. Null = no clickeable.
 */
@Composable
fun GameCardItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    rows: List<Pair<String, String>> = emptyList(),
    badge: String? = null,
    badgeColor: Color? = null,
    /** Composable opcional que se renderiza inmediatamente a la derecha del badge. */
    badgeTrailingContent: (@Composable () -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    /** Composable renderizado después de [rows] — para filas con contenido visual. */
    customRows: (@Composable () -> Unit)? = null,
    isHighlighted: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val effectiveBadgeColor = badgeColor ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            // Cuando hay leadingContent (miniatura del tablero), el padding del card
            // se aplica solo al contenido de texto; el tablero ocupa el alto completo.
            modifier = if (leadingContent != null)
                Modifier.padding(start = 4.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
            else
                Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Leading: composable arbitrario (p.ej. miniatura del tablero) o ícono vectorial
            when {
                leadingContent != null -> {
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center,
                    ) { leadingContent() }
                    Spacer(Modifier.width(10.dp))
                }

                leadingIcon != null -> {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 4.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (badge != null || badgeTrailingContent != null) {
                        Spacer(Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (badge != null) {
                                Text(
                                    text = badge,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = effectiveBadgeColor,
                                )
                            }
                            badgeTrailingContent?.invoke()
                        }
                    }
                }

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                rows.take(3).forEach { (label, value) ->
                    GameCardRow(label = label, value = value)
                }
                customRows?.invoke()
            }
        }
    }
}

@Composable
fun GameCardRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}