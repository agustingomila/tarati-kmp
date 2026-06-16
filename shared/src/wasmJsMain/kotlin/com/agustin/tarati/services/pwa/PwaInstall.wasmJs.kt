@file:OptIn(ExperimentalWasmJsInterop::class)

package com.agustin.tarati.services.pwa

@JsFun("() => !!(typeof globalThis.__pwaCanInstall === 'function' && globalThis.__pwaCanInstall())")
private external fun jsPwaCanInstall(): JsBoolean

@JsFun("() => { if (typeof globalThis.__pwaInstall === 'function') globalThis.__pwaInstall(); }")
private external fun jsPwaInstall(): Unit

actual fun pwaInstallAvailable(): Boolean = jsPwaCanInstall().toBoolean()

actual fun pwaInstall(): Unit = jsPwaInstall()
