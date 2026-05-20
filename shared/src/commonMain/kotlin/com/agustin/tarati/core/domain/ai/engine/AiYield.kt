package com.agustin.tarati.core.domain.ai.engine

/**
 * Platform-specific yield used inside the Minimax search to prevent the AI
 * computation from starving the animation system.
 *
 * - WASM (single-threaded): [delay](1) schedules a macrotask via setTimeout,
 *   which allows requestAnimationFrame to fire before the search resumes.
 * - Android / Desktop (JVM): [yield] is a no-cost coroutine checkpoint on the
 *   background thread; the UI thread is never blocked regardless.
 */
expect suspend fun yieldForAnimation()
