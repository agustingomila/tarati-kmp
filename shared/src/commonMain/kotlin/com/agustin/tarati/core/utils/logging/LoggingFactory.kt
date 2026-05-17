package com.agustin.tarati.core.utils.logging

object LoggingFactory {
    fun getLogger(tag: String = "Tarati"): PlatformLogger {
        return PlatformLogger(tag)
    }
}

inline fun <reified T> T.logger(): PlatformLogger {
    return LoggingFactory.getLogger(T::class.simpleName ?: "Unknown")
}