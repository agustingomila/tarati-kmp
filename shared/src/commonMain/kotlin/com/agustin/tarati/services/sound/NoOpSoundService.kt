package com.agustin.tarati.services.sound

/**
 * Implementación no-op de ISoundService para Desktop y Web.
 * El audio con javax.sound.sampled puede agregarse en el futuro.
 */
class NoOpSoundService : ISoundService {
    override fun playMoveSound(): Unit = Unit
    override fun playCaptureSound(): Unit = Unit
    override fun playUpgradeSound(): Unit = Unit
    override fun playIllegalMoveSound(): Unit = Unit
    override fun playNewGameSound(): Unit = Unit
    override fun playGameOverSound(): Unit = Unit
    override fun playTutorialStepSound(): Unit = Unit
    override fun setSoundEnabled(enabled: Boolean): Unit = Unit
    override fun setVolume(volume: Float): Unit = Unit
}