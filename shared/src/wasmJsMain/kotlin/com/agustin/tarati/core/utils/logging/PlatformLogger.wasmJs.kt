@file:OptIn(ExperimentalWasmJsInterop::class)

package com.agustin.tarati.core.utils.logging

// En Kotlin/WASM, console no es accesible directamente.
// Se accede vía JS interop con @JsFun.
@JsFun("(msg) => { console.log(msg) }")
private external fun jsLog(msg: String)

@JsFun("(msg) => { console.info(msg) }")
private external fun jsInfo(msg: String)

@JsFun("(msg) => { console.warn(msg) }")
private external fun jsWarn(msg: String)

@JsFun("(msg) => { console.error(msg) }")
private external fun jsError(msg: String)

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformLogger actual constructor(private val tag: String) {
    actual fun debug(message: String) = jsLog("[$tag] $message")
    actual fun info(message: String) = jsInfo("[$tag] $message")
    actual fun warn(message: String) = jsWarn("[$tag] $message")
    actual fun error(message: String, throwable: Throwable?) {
        jsError("[$tag] $message")
        throwable?.let { jsError(it.toString()) }
    }
}
