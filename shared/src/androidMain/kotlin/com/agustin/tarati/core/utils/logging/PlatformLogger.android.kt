package com.agustin.tarati.core.utils.logging

import android.util.Log

/**
 * Implementación Android de Logger.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformLogger actual constructor(private val tag: String) {

    actual fun debug(message: String) {
        Log.d(tag, message)
    }

    actual fun info(message: String) {
        Log.i(tag, message)
    }

    actual fun warn(message: String) {
        Log.w(tag, message)
    }

    actual fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}