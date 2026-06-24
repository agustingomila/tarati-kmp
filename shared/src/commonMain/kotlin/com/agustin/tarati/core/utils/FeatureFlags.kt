package com.agustin.tarati.core.utils

/**
 * Feature flags de desarrollo.
 *
 * Todos los flags son `const val` — el compilador los inline en el call site,
 * por lo que el dead-code elimination elimina los bloques desactivados en release.
 */
object FeatureFlags {

    /**
     * Activa todas las funciones de juego online:
     * matchmaking, lobby, autenticación, torneos, desafíos y feed social.
     *
     * `false` → la app se comporta como un juego local puro: ningún control
     * online es visible ni accesible desde la UI.
     *
     * Cambiar a `true` para desarrollo con servidor activo.
     */
    const val ONLINE_ENABLED: Boolean = true
}
