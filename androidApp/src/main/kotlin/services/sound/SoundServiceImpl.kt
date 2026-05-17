package com.agustin.tarati.services.sound

/**
 * Implementación Android de [ISoundService] usando [SoundManager] y MediaPlayer.
 */
class SoundServiceImpl(
    private val soundManager: SoundManager,
) : ISoundService {
    override fun playMoveSound() = soundManager.playSound(SoundType.MOVE)
    override fun playCaptureSound() = soundManager.playSound(SoundType.CAPTURE)
    override fun playUpgradeSound() = soundManager.playSound(SoundType.UPGRADE)
    override fun playIllegalMoveSound() = soundManager.playSound(SoundType.ILLEGAL_MOVE)
    override fun playNewGameSound() = soundManager.playSound(SoundType.NEW_GAME)
    override fun playGameOverSound() = soundManager.playSound(SoundType.GAME_OVER)
    override fun playTutorialStepSound() = soundManager.playSound(SoundType.TUTORIAL)
    override fun setSoundEnabled(enabled: Boolean) = soundManager.setSoundEnabled(enabled)
    override fun setVolume(volume: Float) = soundManager.setVolume(volume)
}