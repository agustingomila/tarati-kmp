package com.agustin.tarati.core.utils

// Previews de Compose no existen en el browser — este path nunca se ejecuta en WASM.
actual fun <T> runSync(block: suspend () -> T?): T? = null
