package com.agustin.tarati.features.game

/**
 * Delay in milliseconds inserted before each AI move computation.
 *
 * In WASM (single-threaded), [withContext(Dispatchers.Default)] runs on the
 * main thread and blocks requestAnimationFrame for the duration of the AI
 * search (~130–200ms). Setting a pre-think delay gives the animation system
 * time to render the previous move before the thread is occupied again.
 *
 * On Android and Desktop, the AI runs on a background thread in parallel
 * with the UI, so no delay is needed.
 */
expect val AI_MOVE_DELAY_MS: Long
