package com.agustin.tarati.services.sound

/**
 * Implementación Android de [ISoundService] usando [SoundManager] y MediaPlayer.
 */
class SoundServiceImpl(
    private val soundManager: SoundManager,
) : ISoundService {
    override fun playMoveSound(): Unit = soundManager.playSound(SoundType.MOVE)
    override fun playCaptureSound(): Unit = soundManager.playSound(SoundType.CAPTURE)
    override fun playUpgradeSound(): Unit = soundManager.playSound(SoundType.UPGRADE)
    override fun playIllegalMoveSound(): Unit = soundManager.playSound(SoundType.ILLEGAL_MOVE)
    override fun playNewGameSound(): Unit = soundManager.playSound(SoundType.NEW_GAME)
    override fun playGameOverSound(): Unit = soundManager.playSound(SoundType.GAME_OVER)
    override fun playTutorialStepSound(): Unit = soundManager.playSound(SoundType.TUTORIAL)
    override fun setSoundEnabled(enabled: Boolean): Unit = soundManager.setSoundEnabled(enabled)
    override fun setVolume(volume: Float): Unit = soundManager.setVolume(volume)
}