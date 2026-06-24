package com.agustin.tarati.core.domain.ai.engine

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

actual suspend fun yieldForAnimation(): Unit = delay(1.milliseconds)
