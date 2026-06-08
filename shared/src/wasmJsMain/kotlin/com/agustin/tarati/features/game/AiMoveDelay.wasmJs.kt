package com.agustin.tarati.features.game

// In WASM (single-threaded), AI computation runs on the main thread and blocks
// requestAnimationFrame. This delay gives the animation system time to render
// the current move before the next AI search occupies the thread.
actual val AI_MOVE_DELAY_MS: Long = 150L
