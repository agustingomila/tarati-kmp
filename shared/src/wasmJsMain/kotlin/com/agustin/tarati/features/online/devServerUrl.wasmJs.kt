@file:OptIn(ExperimentalWasmJsInterop::class)

package com.agustin.tarati.features.online

// En producción → https://<window.location.host> (mismo dominio que sirve el bundle).
// En desarrollo → http://<__TARATI_SERVER__> (override desde index.html, sin protocolo).
@JsFun("() => { const override = typeof globalThis.__TARATI_SERVER__ !== 'undefined' ? String(globalThis.__TARATI_SERVER__) : null; if (override) return 'http://' + override; return window.location.protocol + '//' + window.location.host; }")
private external fun resolveServerUrl(): JsString

actual val devServerUrl: String = resolveServerUrl().toString()
