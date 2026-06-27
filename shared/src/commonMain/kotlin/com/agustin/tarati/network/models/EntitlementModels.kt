package com.agustin.tarati.network.models

import kotlinx.serialization.Serializable

/**
 * `productId` del entitlement global que desbloquea todo el contenido supporter-only
 * (modelo Supporter unlock, fases C3/C4). Fuente única de verdad compartida por server
 * (validación/webhook/flair) y cliente (regla `isUnlocked`, gate, flair).
 */
const val SUPPORTER_PRODUCT_ID: String = "supporter"

/**
 * Origen de un entitlement — de dónde proviene la titularidad de un producto.
 *
 * [key] es el valor almacenado en la columna `entitlements.source`. Sigue la
 * convención del proyecto (`TournamentType.key` / `TournamentStatus.key`): el
 * valor persistido se deriva de `name.lowercase()`, sin literales sueltos.
 *
 * - GOOGLE_PLAY — compra validada contra la Google Play Developer API.
 * - STRIPE      — pago validado vía webhook de Stripe (fase C3, ahora dormido).
 * - POLAR       — pago validado vía webhook de Polar (Merchant of Record, activo Web/Desktop).
 * - GRANT       — concesión manual de admin (cuentas de prueba, soporte).
 */
enum class EntitlementSource {
    GOOGLE_PLAY, STRIPE, POLAR, GRANT;

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

/**
 * Cuerpo de POST /api/checkout/stripe (fase C3).
 *
 * El cliente (Desktop/Web) envía el monto elegido y el intervalo; el servidor crea
 * un Stripe Checkout Session y devuelve la URL de redirección. El monto se valida
 * (clamp) server-side — no se confía en el valor crudo del cliente.
 *
 * @param amountCents Monto en centavos de USD.
 * @param interval    "once" (pago único) | "month" (suscripción mensual).
 */
@Serializable
data class StripeCheckoutRequest(
    val amountCents: Int,
    val interval: String,
)

/**
 * Respuesta de POST /api/checkout/stripe — URL del Checkout Session de Stripe.
 * El cliente abre esta URL en el browser para completar el pago.
 */
@Serializable
data class StripeCheckoutResponse(
    val checkoutUrl: String,
)

/**
 * Cuerpo de POST /api/checkout/polar (proveedor activo Web/Desktop).
 *
 * Sin monto: Polar cobra el importe en su propia página de checkout (producto
 * pay-what-you-want o precio fijo). Solo se elige el intervalo.
 *
 * @param interval "once" (pago único) | "month" (suscripción mensual).
 */
@Serializable
data class PolarCheckoutRequest(
    val interval: String,
)

/**
 * Respuesta de POST /api/checkout/polar — URL del Checkout de Polar a abrir en el browser.
 */
@Serializable
data class PolarCheckoutResponse(
    val checkoutUrl: String,
)
