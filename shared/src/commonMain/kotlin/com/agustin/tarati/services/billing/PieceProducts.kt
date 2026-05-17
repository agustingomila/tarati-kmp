package com.agustin.tarati.services.billing

/**
 * Catálogo de IDs de productos de Google Play para los tipos de pieza premium.
 *
 * Estos IDs deben coincidir exactamente con los definidos en Google Play Console
 * (sección Monetización → Productos in-app).
 *
 * ## KMP
 * Las constantes son strings puros — no dependen de APIs de Android.
 * En Desktop, [IBillingManager] retorna siempre desbloqueado para estos IDs.
 */
object PieceProducts {

    const val HEXAGON = "piece_hexagon"
    const val SQUARE = "piece_square"
    const val TRIANGLE = "piece_triangle"
    const val DIAMOND = "piece_diamond"
    const val PENTAGON = "piece_pentagon"
    const val CAPSULE = "piece_capsule"

    /** Lista completa de IDs — usada para pre-cargar ProductDetails en BillingManager. */
    val ALL_PRODUCT_IDS = listOf(HEXAGON, SQUARE, TRIANGLE, DIAMOND, PENTAGON, CAPSULE)
}