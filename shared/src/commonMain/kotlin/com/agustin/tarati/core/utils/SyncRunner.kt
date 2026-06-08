package com.agustin.tarati.core.utils

/** Ejecuta un bloque suspend de forma sincrónica. En WASM retorna null (sin previews). */
expect fun <T> runSync(block: suspend () -> T?): T?
