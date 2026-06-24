package com.agustin.tarati.services.billing

/**
 * Wrapper estable de los nombres de paletas bloqueadas (premium no compradas)
 * para uso en Compose.
 *
 * Análogo a [OwnedProducts]: [Set<String>] es inestable para el compilador de
 * Compose; envolver el conjunto en [@Immutable] evita recomposiciones innecesarias.
 *
 * ## Uso en composables
 * ```kotlin
 * // En SettingsScreen:
 * val locked = remember { LockedPalettes.None } // sin IAP de paletas todavía
 *
 * // En el composable receptor:
 * fun PaletteSelector(lockedPalettes: LockedPalettes = LockedPalettes.None)
 * // Verificar si una paleta está bloqueada:
 * if (palette.name in lockedPalettes) { ... }
 * ```
 */
data class LockedPalettes(val names: Set<String>) {

    /** Devuelve `true` si la paleta con [name] está bloqueada. */
    operator fun contains(name: String): Boolean = name in names

    companion object {
        /** Sin paletas bloqueadas — estado por defecto mientras no haya IAP de temas. */
        val None: LockedPalettes = LockedPalettes(emptySet())
    }
}