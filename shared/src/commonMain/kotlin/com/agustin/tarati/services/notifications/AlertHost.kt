package com.agustin.tarati.services.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject

/**
 * Host de alertas modales.
 *
 * Observa [UIMessageBus.alertContent] y renderiza el contenido composable cuando
 * hay una alerta activa. El contenido recibe un lambda `dismiss` — al llamarlo
 * se limpia el estado del bus y la alerta desaparece.
 *
 * Debe colocarse en [AppContent] junto a [ToastHost].
 */
@Composable
fun AlertHost(bus: UIMessageBus = koinInject()) {
    val content by bus.alertContent.collectAsState()
    content?.invoke { bus.clearAlert() }
}
