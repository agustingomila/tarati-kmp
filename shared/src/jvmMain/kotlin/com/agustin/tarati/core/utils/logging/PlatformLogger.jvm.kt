package com.agustin.tarati.core.utils.logging

import java.util.logging.Level
import java.util.logging.Logger

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformLogger actual constructor(private val tag: String) {
    private val logger = Logger.getLogger(tag)

    actual fun debug(message: String) {
        logger.fine("[$tag] $message")
    }

    actual fun info(message: String) {
        logger.info("[$tag] $message")
    }

    actual fun warn(message: String) {
        logger.warning("[$tag] $message")
    }

    actual fun error(message: String, throwable: Throwable?) {
        logger.log(Level.SEVERE, "[$tag] $message", throwable)
    }
}