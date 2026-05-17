package com.agustin.tarati.core.utils.logging

import platform.Foundation.NSLog

/**
 * Implementación iOS de Logger.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformLogger actual constructor(private val tag: String) {

    actual fun debug(message: String) {
        NSLog("[$tag] DEBUG: $message")
    }

    actual fun info(message: String) {
        NSLog("[$tag] INFO: $message")
    }

    actual fun warn(message: String) {
        NSLog("[$tag] WARN: $message")
    }

    actual fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            NSLog("[$tag] ERROR: $message - ${throwable.message}")
        } else {
            NSLog("[$tag] ERROR: $message")
        }
    }
}