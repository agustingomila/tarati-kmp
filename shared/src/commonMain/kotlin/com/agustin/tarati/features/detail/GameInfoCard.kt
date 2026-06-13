@file:OptIn(ExperimentalMaterial3Api::class)

package com.agustin.tarati.features.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.data.database.dto.PGNHeader
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.black
import com.agustin.tarati.shared.generated.resources.date
import com.agustin.tarati.shared.generated.resources.event
import com.agustin.tarati.shared.generated.resources.game_information
import com.agustin.tarati.shared.generated.resources.game_type
import com.agustin.tarati.shared.generated.resources.observations
import com.agustin.tarati.shared.generated.resources.players
import com.agustin.tarati.shared.generated.resources.result
import com.agustin.tarati.shared.generated.resources.round
import com.agustin.tarati.shared.generated.resources.rules
import com.agustin.tarati.shared.generated.resources.site
import com.agustin.tarati.shared.generated.resources.termination
import com.agustin.tarati.shared.generated.resources.time_control
import com.agustin.tarati.shared.generated.resources.toggle_details
import com.agustin.tarati.shared.generated.resources.white
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

private const val EXPAND_DURATION_MS = 300
private const val FADE_DURATION_MS = 250

/**
 * Panel superior con la información de cabecera de la partida (PGN header).
 *
 * Al igual que [CollapsibleMoveHistoryCard], la sección expandible utiliza
 * [AnimatedVisibility] para suavizar la aparición/desaparición de los campos
 * adicionales, y el ícono chevron rota 180° con [animateFloatAsState].
 *
 * @param isEditing         Si está en modo edición (oculta el botón de expandir).
 * @param header            Cabecera PGN con los metadatos de la partida.
 * @param onHeaderChange    Callback invocado cuando el usuario modifica algún campo.
 * @param onExpandedChange  Callback opcional que notifica al padre cuando el panel
 *                          se expande o colapsa. Usado por [PortraitLayout] para
 *                          coordinar la inclinación inercial del tablero.
 */
@Composable
fun GameInfoCard(
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    header: PGNHeader,
    onExpandedChange: (Boolean) -> Unit = {},
    onHeaderChange: (PGNHeader) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
        ) {
            HeaderTitleSection(
                isEditing = isEditing,
                header = header,
                expanded = expanded,
            ) {
                expanded = it
                onExpandedChange(it)
            }

            // Transición suave entre modo edición y visualización.
            AnimatedContent(
                targetState = isEditing,
                transitionSpec = {
                    fadeIn(tween(EXPAND_DURATION_MS)) togetherWith
                            fadeOut(tween(EXPAND_DURATION_MS))
                },
                label = "edit_view_transition",
            ) { editing ->
                if (editing) {
                    // En edición siempre visible, con su propio padding
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 20.dp)
                    ) {
                        EditingGameInfoContent(
                            initialHeader = header,
                            onHeaderChange = onHeaderChange,
                        )
                    }
                } else {
                    ViewingGameInfoContent(
                        header = header,
                        expanded = expanded,
                    )
                }
            }
        }
    }
}

@Composable
private fun EditingGameInfoContent(
    initialHeader: PGNHeader,
    onHeaderChange: (PGNHeader) -> Unit,
) {
    // Estado mutable independiente
    var localHeader by remember { mutableStateOf(initialHeader) }

    // Efecto para enviar cambios al padre con debounce
    LaunchedEffect(localHeader) {
        // Pequeño delay para evitar sobrecarga
        delay(300.milliseconds)
        onHeaderChange(localHeader)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Jugadores
        EditableGameInfoRow(
            isEditing = true,
            label = stringResource(Res.string.players),
            value = localHeader.white to localHeader.black,
            onWhiteChange = { localHeader = localHeader.copy(white = it) },
            onBlackChange = { localHeader = localHeader.copy(black = it) },
        )

        // Campos básicos
        listOf(
            BasicField(Res.string.result, { it.result }, { h, v -> h.copy(result = v) }),
            BasicField(Res.string.date, { it.date }, { h, v -> h.copy(date = v) }),
            BasicField(Res.string.event, { it.event }, { h, v -> h.copy(event = v) }),
            BasicField(Res.string.site, { it.site }, { h, v -> h.copy(site = v) }),
            BasicField(Res.string.round, { it.round }, { h, v -> h.copy(round = v) }),
            BasicField(Res.string.game_type, { it.gameType }, { h, v -> h.copy(gameType = v) }),
            BasicField(Res.string.rules, { it.rules }, { h, v -> h.copy(rules = v) }),
            BasicField(Res.string.time_control, { it.timeControl }, { h, v -> h.copy(timeControl = v) }),
            BasicField(Res.string.termination, { it.termination }, { h, v -> h.copy(termination = v) }),
        ).forEach { field ->
            EditableGameInfoRow(
                isEditing = true,
                label = stringResource(field.labelRes),
                value = field.getValue(localHeader),
            ) { newValue ->
                localHeader = field.setValue(localHeader, newValue)
            }
        }

        // Observaciones (multilínea)
        EditableGameInfoRow(
            isEditing = true,
            label = stringResource(Res.string.observations),
            value = localHeader.observations,
            isMultiline = true,
            showInExpandible = false,
        ) { newValue ->
            localHeader = localHeader.copy(observations = newValue)
        }
    }
}

/**
 * Contenido de visualización (no edición) del panel de información de partida.
 *
 * Los campos básicos (jugadores, resultado, fecha) son siempre visibles.
 * Los campos adicionales (evento, lugar, ronda, etc.) se revelan/ocultan con
 * [AnimatedVisibility] en lugar del `if` simple anterior, eliminando el cambio
 * brusco de layout al expandir o colapsar el panel superior.
 *
 * @param header   Cabecera PGN con los metadatos de la partida.
 * @param expanded Si los campos adicionales deben mostrarse.
 */
@Composable
private fun ViewingGameInfoContent(
    header: PGNHeader,
    expanded: Boolean,
) {
    // Todo el contenido está bajo AnimatedVisibility: colapsado = solo el header.
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(
            animationSpec = tween(
                durationMillis = EXPAND_DURATION_MS,
                easing = FastOutSlowInEasing,
            ),
            expandFrom = Alignment.Top,
        ) + fadeIn(
            animationSpec = tween(durationMillis = FADE_DURATION_MS),
        ),
        exit = shrinkVertically(
            animationSpec = tween(
                durationMillis = EXPAND_DURATION_MS - 50,
                easing = FastOutSlowInEasing,
            ),
            shrinkTowards = Alignment.Top,
        ) + fadeOut(
            animationSpec = tween(durationMillis = FADE_DURATION_MS - 50),
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Jugadores
            if (shouldShowField(header.white to header.black)) {
                EditableGameInfoRow(
                    isEditing = false,
                    label = stringResource(Res.string.players),
                    value = header.white to header.black,
                )
            }

            // Todos los campos básicos y opcionales
            listOf(
                BasicField(Res.string.result, { it.result }, { h, v -> h.copy(result = v) }),
                BasicField(Res.string.date, { it.date }, { h, v -> h.copy(date = v) }),
                BasicField(Res.string.event, { it.event }, { h, v -> h.copy(event = v) }),
                BasicField(Res.string.site, { it.site }, { h, v -> h.copy(site = v) }),
                BasicField(Res.string.round, { it.round }, { h, v -> h.copy(round = v) }),
                BasicField(Res.string.game_type, { it.gameType }, { h, v -> h.copy(gameType = v) }),
                BasicField(Res.string.rules, { it.rules }, { h, v -> h.copy(rules = v) }),
                BasicField(Res.string.time_control, { it.timeControl }, { h, v -> h.copy(timeControl = v) }),
                BasicField(Res.string.termination, { it.termination }, { h, v -> h.copy(termination = v) }),
            ).forEach { field ->
                val value = field.getValue(header)
                if (shouldShowField(value)) {
                    EditableGameInfoRow(
                        isEditing = false,
                        label = stringResource(field.labelRes),
                        value = value,
                    )
                }
            }

            // Observaciones solo si hay contenido
            if (shouldShowField(header.observations)) {
                EditableGameInfoRow(
                    isEditing = false,
                    label = stringResource(Res.string.observations),
                    value = header.observations,
                    isMultiline = true,
                )
            }
        }
    }
}

@Composable
private fun EditableGameInfoRow(
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    label: String,
    value: String,
    isMultiline: Boolean = false,
    showInExpandible: Boolean = true,
    onValueChange: (String) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = if (isMultiline) Alignment.Top else Alignment.CenterVertically,
    ) {
        if (isEditing || showInExpandible) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(120.dp),
            )
        }

        if (isEditing) {
            // Campo de edición SIMPLE sin estado complejo
            SimpleEditableField(
                value = value,
                onValueChange = onValueChange,
                isMultiline = isMultiline,
                modifier = Modifier.weight(1f),
            )
        } else {
            if (showInExpandible) {
                Text(
                    text = value.ifEmpty { "-" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                    maxLines = if (isMultiline) 3 else 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SimpleEditableField(
    value: String,
    onValueChange: (String) -> Unit,
    isMultiline: Boolean,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf(value) }

    // Sincronizar con el valor inicial
    LaunchedEffect(value) {
        text = value
    }

    // Actualizar al padre cuando cambie
    LaunchedEffect(text) {
        if (text != value) {
            onValueChange(text)
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        textStyle = MaterialTheme.typography.bodyMedium,
        colors =
            outlinedTextFieldColors(
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        modifier = modifier.padding(start = 8.dp),
        singleLine = !isMultiline,
        maxLines = if (isMultiline) 3 else 1,
    )
}

@Composable
private fun EditableGameInfoRow(
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    label: String,
    value: Pair<String, String>,
    onWhiteChange: (String) -> Unit = {},
    onBlackChange: (String) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp),
        )

        if (isEditing) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SimplePlayerField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.first,
                    onValueChange = onWhiteChange,
                )
                SimplePlayerField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value.second,
                    onValueChange = onBlackChange,
                )
            }
        } else {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = value.first.ifEmpty { "-" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value.second.ifEmpty { "-" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SimplePlayerField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    var text by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        text = value
    }

    LaunchedEffect(text) {
        if (text != value) {
            onValueChange(text)
        }
    }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                text = if (text == value) localizedString(Res.string.white) else localizedString(Res.string.black),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        colors =
            outlinedTextFieldColors(
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        modifier = modifier,
        singleLine = true,
    )
}

/**
 * Fila del encabezado del panel de información: título + botón de expandir/colapsar.
 *
 * El ícono chevron rota 180° con [animateFloatAsState] en lugar de hacer swap
 * entre [Icons.Filled.ExpandMore] e [Icons.Filled.ExpandLess], manteniendo
 * coherencia visual con el chevron del panel inferior.
 */
@Composable
private fun HeaderTitleSection(
    isEditing: Boolean,
    header: PGNHeader,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
) {
    // Ángulo de rotación del chevron: 0° = cerrado (ExpandMore), 180° = abierto (ExpandLess).
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = EXPAND_DURATION_MS, easing = FastOutSlowInEasing),
        label = "info_chevron_rotation",
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = stringResource(Res.string.game_information),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (!isEditing) {
            // Resumen compacto visible solo cuando el panel está colapsado:
            // jugadores, resultado y fecha en una sola línea.
            if (!expanded) {
                val summary = buildList {
                    val white = header.white.takeIf { isValidValue(it) }
                    val black = header.black.takeIf { isValidValue(it) }
                    if (white != null && black != null) add("$white vs $black")
                    if (isValidValue(header.result)) add(header.result)
                    if (isValidValue(header.date)) add(header.date)
                }.joinToString("  ·  ")
                if (summary.isNotEmpty()) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            IconButton(
                onClick = { onExpandedChange(!expanded) },
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = TaratiIcons.ExpandMore,
                    contentDescription = stringResource(Res.string.toggle_details),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.graphicsLayer { rotationZ = chevronRotation },
                )
            }
        }
    }
}

// Funciones de utilidad
private fun hasAdditionalInfo(header: PGNHeader): Boolean {
    val additionalFields =
        listOf(
            header.event,
            header.site,
            header.round,
            header.gameType,
            header.rules,
            header.timeControl,
            header.termination,
        )
    return additionalFields.any { isValidValue(it) }
}

private fun shouldShowField(value: Any): Boolean =
    when (value) {
        is String -> isValidValue(value)
        is Pair<*, *> -> isValidValue(value.first as String) && isValidValue(value.second as String)
        else -> false
    }

private fun isValidValue(value: String?): Boolean = !value.isNullOrEmpty() && !PGNHeader.invalidValues.contains(value)

private data class BasicField(
    val labelRes: StringResource,
    val getValue: (PGNHeader) -> String,
    val setValue: (PGNHeader, String) -> PGNHeader,
    val isMultiline: Boolean = false,
    val showInExpanded: Boolean = true,
)