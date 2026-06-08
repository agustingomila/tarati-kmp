@file:OptIn(ExperimentalWasmJsInterop::class)

package com.agustin.tarati.web

import com.agustin.tarati.services.clipboard.IClipboardService
import kotlinx.browser.window
import kotlinx.coroutines.await

/**
 * Implementación web de [IClipboardService] usando la Clipboard API del navegador.
 *
 * Requiere contexto seguro (HTTPS o localhost) y permiso del usuario en algunos browsers.
 */
class WasmClipboardService : IClipboardService {

    override suspend fun copyText(label: String, text: String): Boolean {
        return try {
            window.navigator.clipboard.writeText(text).await()
            true
        } catch (_: Throwable) {
            false
        }
    }

    override suspend fun getText(): String? {
        return try {
            window.navigator.clipboard.readText().await().toString()
        } catch (_: Throwable) {
            null
        }
    }
}
