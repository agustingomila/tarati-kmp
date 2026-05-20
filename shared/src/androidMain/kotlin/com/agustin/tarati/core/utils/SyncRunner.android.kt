package com.agustin.tarati.core.utils

import kotlinx.coroutines.runBlocking

actual fun <T> runSync(block: suspend () -> T?): T? = runBlocking { block() }
