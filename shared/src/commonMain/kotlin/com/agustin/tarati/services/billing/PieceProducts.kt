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

    const val HEXAGON: String = "piece_hexagon"
    const val SQUARE: String = "piece_square"
    const val TRIANGLE: String = "piece_triangle"
    const val DIAMOND: String = "piece_diamond"
    const val PENTAGON: String = "piece_pentagon"
    const val CAPSULE: String = "piece_capsule"

    /** Lista completa de IDs — usada para pre-cargar ProductDetails en BillingManager. */
    val ALL_PRODUCT_IDS: List<String> = listOf(HEXAGON, SQUARE, TRIANGLE, DIAMOND, PENTAGON, CAPSULE)
}