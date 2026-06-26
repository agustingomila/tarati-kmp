package com.agustin.tarati.features.online.supporter

import androidx.compose.runtime.Composable

/**
 * Acción de compra del tier supporter en la tienda nativa de la plataforma (Google Play en Android),
 * o `null` si la plataforma no tiene compra nativa (Desktop/Web usan Stripe; iOS aún no soportado).
 *
 * Se resuelve vía `expect/actual` para que el `SupporterScreen` (commonMain) no dependa del billing
 * específico de Android. La implementación Android inyecta `IBillingManager` y lanza el flujo de Play.
 */
@Composable
expect fun rememberSupporterPurchaseAction(): (() -> Unit)?
