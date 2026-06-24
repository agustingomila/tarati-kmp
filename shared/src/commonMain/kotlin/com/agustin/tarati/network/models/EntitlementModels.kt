package com.agustin.tarati.network.models

import kotlinx.serialization.Serializable

/**
 * Origen de un entitlement — de dónde proviene la titularidad de un producto.
 *
 * [key] es el valor almacenado en la columna `entitlements.source`. Sigue la
 * convención del proyecto (`TournamentType.key` / `TournamentStatus.key`): el
 * valor persistido se deriva de `name.lowercase()`, sin literales sueltos.
 *
 * - GOOGLE_PLAY — compra validada contra la Google Play Developer API.
 * - STRIPE      — pago validado vía webhook de Stripe (fase C3).
 * - GRANT       — concesión manual de admin (cuentas de prueba, soporte).
 */
enum class EntitlementSource {
    GOOGLE_PLAY, STRIPE, GRANT;

    val key: String get() = name.lowercase()

    companion object {
        /** Resuelve un [EntitlementSource] desde su [key]; null si no coincide. */
        fun fromKey(key: String): EntitlementSource? = entries.firstOrNull { it.key == key }
    }
}

/**
 * Cuerpo de POST /api/entitlements/google-play.
 *
 * El cliente Android envía el `purchaseToken` del recibo de Google tras el
 * acknowledge; el servidor lo valida contra la Developer API y graba el
 * entitlement.
 */
@Serializable
data class GooglePlayPurchaseRequest(
    val productId: String,
    val purchaseToken: String,
)

/**
 * Respuesta de GET /api/entitlements — lista cruda de `productId` activos del
 * usuario autenticado. El cliente aplica la regla "supporter desbloquea todo"
 * (la lógica vive en el cliente, no en el servidor).
 */
@Serializable
data class EntitlementsResponse(
    val productIds: List<String>,
)
