package com.agustin.tarati.core.utils.logging

/**
 * Logger multiplataforma usando expect/actual pattern.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class PlatformLogger(tag: String) {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}