package com.agustin.tarati.services.notifications

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// ── Toast ─────────────────────────────────────────────────────────────────────

data class MessageAction(
    val label: String,
    val style: ActionStyle = ActionStyle.SECONDARY,
    val onClick: () -> Unit,
)

enum class ActionStyle { PRIMARY, SECONDARY, DESTRUCTIVE }

sealed class UIMessage {

    /**
     * Notificación no bloqueante que se auto-descarta.
     * Renderizada por [ToastHost], posicionada por su caller vía Modifier.
     */
    data class Toast(
        val message: String,
        val duration: Duration = 4.seconds,
        val actions: List<MessageAction> = emptyList(),
    ) : UIMessage()
}
