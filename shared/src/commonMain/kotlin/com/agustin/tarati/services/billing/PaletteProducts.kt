package com.agustin.tarati.services.billing

/**
 * Catálogo de IDs de productos de Google Play para las paletas premium.
 *
 * Estos IDs deben coincidir con los definidos en Google Play Console
 * (sección Monetización → Productos in-app).
 */
object PaletteProducts {

    const val GILDED: String = "palette_gilded"

    val ALL_PRODUCT_IDS: List<String> = listOf(GILDED)
}