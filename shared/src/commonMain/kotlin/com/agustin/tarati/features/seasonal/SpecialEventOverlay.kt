package com.agustin.tarati.features.seasonal

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.special_event_celebrate_button
import com.agustin.tarati.shared.generated.resources.special_event_gift_icon_desc
import com.agustin.tarati.shared.generated.resources.special_event_palette_available_in_settings
import com.agustin.tarati.shared.generated.resources.special_event_palette_unlocked_desc
import com.agustin.tarati.shared.generated.resources.special_event_palette_unlocked_title
import com.agustin.tarati.shared.generated.resources.special_event_understood
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Overlay completo de eventos especiales para la pantalla de juego.
 *
 * Maneja tres estados visuales para cada evento activo:
 * 1. **Ícono animado** — el usuario no ha tocado el regalo aún. Pulso suave de escala
 *    para captar la atención sin molestar.
 * 2. **Ícono estático** — el usuario tocó el regalo. La animación para; el ícono
 *    permanece hasta que se desbloquee o termine la ventana de días.
 * 3. **Sin ícono** — el evento fue desbloqueado. El ícono desaparece de [activeEvents].
 *
 * Además, gestiona el [InfoBubble] (tocando el ícono) y el [CelebrationDialog]
 * (al completar el logro).
 *
 * @param manager [SpecialEventManager] inyectado desde
 * [GameEffects.MainContent].
 * @param modifier Posiciona el overlay dentro del Box del tablero (TopStart).
 */
@Composable
fun SpecialEventOverlay(
    manager: ISpecialEventManager,
    modifier: Modifier = Modifier,
) {
    val activeEvents by manager.activeEvents.collectAsState()
    val pendingCelebration by manager.pendingCelebration.collectAsState()

    // Estado local: qué evento está mostrando su info bubble actualmente
    var expandedEventId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Refrescar al entrar en composición: cubre el caso donde la fecha cambió
    // sin reiniciar la app (ej. unlock del día anterior dejó activeEvents vacío).
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) { manager.refreshIfNeeded() }
    }

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        activeEvents.forEach { event ->
            // key() asegura identidad por evento, no por posición en la lista.
            key(event.id) {
                // isSeen arranca en false para que el ícono siempre pulse al aparecer.
                // LaunchedEffect consulta DataStore en IO sin bloquear composición:
                // si el usuario ya tocó el ícono antes (DataStore = true), isSeen se
                // actualiza al siguiente frame y el pulso se detiene limpiamente.
                // Esto evita el bug donde manager.isGiftSeen() en composición bloqueaba
                // el hilo principal y devolvía true de sesiones anteriores.
                var isSeen by remember(event.id) { mutableStateOf(false) }
                LaunchedEffect(event.id) {
                    isSeen = withContext(Dispatchers.Default) { manager.isGiftSeen(event) }
                }

                val isExpanded = expandedEventId == event.id

                GiftButton(
                    isPulsing = !isSeen,
                    onClick = {
                        if (!isSeen) {
                            scope.launch(Dispatchers.Default) { manager.markGiftSeen(event) }
                            isSeen = true
                        }
                        expandedEventId = if (isExpanded) null else event.id
                    },
                )

                if (isExpanded) {
                    InfoBubble(
                        event = event,
                        onDismiss = { expandedEventId = null },
                    )
                }
            }
        }
    }

    // Dialog de celebración — aparece tras desbloquear el logro
    pendingCelebration?.let { event ->
        CelebrationDialog(
            event = event,
            onDismiss = {
                manager.dismissCelebration()
                expandedEventId = null
            },
        )
    }
}

// ── Gift icon ─────────────────────────────────────────────────────────────────

@Composable
private fun GiftButton(
    isPulsing: Boolean,
    onClick: () -> Unit,
) {
    val scale = if (isPulsing) {
        val transition = rememberInfiniteTransition(label = "gift_pulse")
        transition.animateFloat(
            initialValue = 1.00f,
            targetValue = 1.14f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "gift_scale",
        ).value
    } else {
        1.0f
    }

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .scale(scale),
    ) {
        Icon(
            imageVector = TaratiIcons.CardGiftcard,
            contentDescription = localizedString(Res.string.special_event_gift_icon_desc),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )
    }
}

// ── Info bubble ───────────────────────────────────────────────────────────────

/**
 * Popup informativo. [PopupProperties.dismissOnClickOutside] = true hace que
 * tocar fuera sea equivalente a "Entendido".
 */
@Composable
private fun InfoBubble(
    event: SpecialEvent,
    onDismiss: () -> Unit,
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(dismissOnClickOutside = true, focusable = true),
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier.width(220.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = localizedString(event.titleRes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = localizedString(event.descRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = localizedString(Res.string.special_event_understood),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}

// ── Celebration dialog ────────────────────────────────────────────────────────

/**
 * Dialog de celebración que aparece al desbloquear el logro.
 * Aquí sí se revela el nombre de la paleta desbloqueada.
 */
@Composable
private fun CelebrationDialog(
    event: SpecialEvent,
    onDismiss: () -> Unit,
) {
    val paletteName = localizedString(event.reward.displayNameRes)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = TaratiIcons.CardGiftcard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
            )
        },
        title = {
            Text(
                text = localizedString(Res.string.special_event_palette_unlocked_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = localizedString(
                        Res.string.special_event_palette_unlocked_desc,
                        paletteName,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = localizedString(Res.string.special_event_palette_available_in_settings),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text = localizedString(Res.string.special_event_celebrate_button),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    )
}