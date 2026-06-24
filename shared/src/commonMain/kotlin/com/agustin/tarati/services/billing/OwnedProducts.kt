package com.agustin.tarati.services.billing

/**
 * Wrapper estable de los IDs de productos comprados para uso en Compose.
 *
 * [Set<String>] es una interfaz cuya implementación concreta es desconocida para el
 * compilador de Compose, por lo que se trata como inestable y provoca recomposiciones
 * innecesarias cuando se pasa como parámetro. Envolver el conjunto en una clase
 * anotada con [@Immutable] le comunica al compilador que el contenido no cambiará
 * tras la construcción — cada cambio de estado crea una nueva instancia — y permite
 * saltar recomposiciones cuando la referencia no cambia.
 *
 * ## Uso en composables
 * ```kotlin
 * // En SettingsScreen, al recoger el StateFlow:
 * val rawIds by viewModel.purchasedProductIds.collectAsState()
 * val ownedProducts = remember(rawIds) { OwnedProducts(rawIds) }
 *
 * // En el composable receptor:
 * fun PieceTypeSetting(owned: OwnedProducts = OwnedProducts.None)
 * // Verificar si un producto está comprado:
 * if (productId in owned) { ... }
 * ```
 *
 * ## Capa de ViewModel/Manager
 * Los [kotlinx.coroutines.flow.StateFlow] del ViewModel y del [com.agustin.tarati.services.billing.IBillingManager]
 * siguen usando `Set<String>` sin cambios — la conversión ocurre sólo en la
 * frontera con Compose.
 */
data class OwnedProducts(val ids: Set<String>) {

    /** Devuelve `true` si [productId] está entre los productos comprados. */
    operator fun contains(productId: String): Boolean = productId in ids

    companion object {
        /** Instancia vacía reutilizable — sin productos comprados. */
        val None: OwnedProducts = OwnedProducts(emptySet())
    }
}