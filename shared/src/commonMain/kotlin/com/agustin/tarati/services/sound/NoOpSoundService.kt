package com.agustin.tarati.services.sound

/**
 * Implementación no-op de ISoundService para Desktop y Web.
 * El audio con javax.sound.sampled puede agregarse en el futuro.
 */
class NoOpSoundService : ISoundService {
    override fun playMoveSound() = Unit
    override fun playCaptureSound() = Unit
    override fun playUpgradeSound() = Unit
    override fun playIllegalMoveSound() = Unit
    override fun playNewGameSound() = Unit
    override fun playGameOverSound() = Unit
    override fun playTutorialStepSound() = Unit
    override fun setSoundEnabled(enabled: Boolean) = Unit
    override fun setVolume(volume: Float) = Unit
}