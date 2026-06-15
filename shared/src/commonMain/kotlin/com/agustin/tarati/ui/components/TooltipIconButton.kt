package com.agustin.tarati.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * [IconButton] con [PlainTooltip] de Material 3.
 *
 * El tooltip aparece en hover (desktop/web) y en long-press (touch).
 * Usar en lugar de [IconButton] cuando el botón no tiene etiqueta de texto
 * visible junto al ícono — mejora descubribilidad y accesibilidad.
 *
 * El [content] típicamente es un [androidx.compose.material3.Icon] con
 * `contentDescription` ya asignado; el [tooltip] reutiliza ese mismo texto.
 *
 * @param tooltip Texto del tooltip (normalmente el mismo que contentDescription).
 * @param onClick Acción del botón.
 * @param modifier Modifier aplicado al [IconButton].
 * @param enabled Si false, el botón está deshabilitado y el tooltip no aparece.
 * @param content Slot interior del botón (Icon).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TooltipIconButton(
    tooltip: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(tooltip) } },
        state = rememberTooltipState(),
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
        ) {
            content()
        }
    }
}
