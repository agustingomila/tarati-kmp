package com.agustin.tarati.services.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Overlay que renderiza [UIMessage.Toast] sobre toda la navegación.
 *
 * Debe colocarse en la raíz de la app ([AppContent]). El [Modifier] que recibe
 * controla su posición: el caller elige `Alignment.TopCenter`, `BottomCenter`, etc.
 *
 * Las notificaciones se procesan secuencialmente desde [UIMessageBus.toasts].
 * Si el usuario pulsa un botón de acción, el toast se cierra inmediatamente
 * (la acción llama a [UIMessageBus.dismissCurrentToast]).
 */
@Composable
fun ToastHost(
    modifier: Modifier = Modifier,
    bus: UIMessageBus = koinInject(),
) {
    var current by remember { mutableStateOf<UIMessage.Toast?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (toast in bus.toasts) {
            if (current != null) {
                visible = false
                delay(200.milliseconds)
            }
            current = toast
            visible = true
            // Esperar la duración del toast O una señal de dismiss anticipado,
            // lo que ocurra primero. withTimeoutOrNull devuelve null en timeout.
            withTimeoutOrNull(toast.duration) {
                bus.dismissRequests.first()
            }
            visible = false
            delay(300.milliseconds)
            current = null
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        current?.let { toast ->
            ToastCard(toast, onDismiss = bus::dismissCurrentToast)
        }
    }
}

@Composable
private fun ToastCard(toast: UIMessage.Toast, onDismiss: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(
                start = 16.dp,
                end = if (toast.actions.isEmpty()) 16.dp else 4.dp,
                top = 12.dp,
                bottom = 12.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = toast.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            toast.actions.forEach { action ->
                // Al pulsar cualquier acción el toast se descarta inmediatamente,
                // igual que el comportamiento estándar de Material Snackbar.
                TextButton(onClick = {
                    action.onClick()
                    onDismiss()
                }) {
                    Text(
                        text = action.label,
                        color = when (action.style) {
                            ActionStyle.PRIMARY -> MaterialTheme.colorScheme.primary
                            ActionStyle.DESTRUCTIVE -> MaterialTheme.colorScheme.error
                            ActionStyle.SECONDARY -> MaterialTheme.colorScheme.onPrimaryContainer
                        },
                    )
                }
            }
        }
    }
}
