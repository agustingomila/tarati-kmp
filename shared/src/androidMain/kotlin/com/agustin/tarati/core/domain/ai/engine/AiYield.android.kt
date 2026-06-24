package com.agustin.tarati.core.domain.ai.engine

import kotlinx.coroutines.yield

actual suspend fun yieldForAnimation(): Unit = yield()
