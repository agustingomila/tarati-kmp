package com.agustin.tarati.services.sound

enum class SoundType {
    MOVE,
    CAPTURE,
    UPGRADE,
    ILLEGAL_MOVE,
    NEW_GAME,
    GAME_OVER,
    TUTORIAL,
}

/**
 * Ruta del clip en composeResources (`shared/src/commonMain/composeResources/files/sounds/`),
 * accesible vía `Res.readBytes(...)` en las plataformas que no usan recursos nativos.
 * Android no la usa (carga desde `res/raw` con SoundPool).
 */
val SoundType.resourcePath: String
    get() = "files/sounds/" + when (this) {
        SoundType.MOVE -> "move_sound.mp3"
        SoundType.CAPTURE -> "capture_sound.mp3"
        SoundType.UPGRADE -> "upgrade_move.mp3"
        SoundType.ILLEGAL_MOVE -> "illegal_move.mp3"
        SoundType.NEW_GAME -> "new_game.mp3"
        SoundType.GAME_OVER -> "game_over.mp3"
        SoundType.TUTORIAL -> "tutorial_step.mp3"
    }