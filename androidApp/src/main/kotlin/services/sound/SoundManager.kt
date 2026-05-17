package com.agustin.tarati.services.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.agustin.tarati.R
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger

class SoundManager(
    private val context: Context,
) {
    private val logger = getLogger(javaClass.simpleName)

    private var soundEnabled: Boolean = true
    private var volume: Float = 1.0f

    private val soundPool: SoundPool by lazy {
        val audioAttributes =
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

        SoundPool
            .Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    private val sounds = mutableMapOf<SoundType, Int>()

    init {
        loadSounds()
    }

    private fun loadSounds() {
        try {
            sounds[SoundType.MOVE] = soundPool.load(context, R.raw.move_sound, 1)
            sounds[SoundType.CAPTURE] = soundPool.load(context, R.raw.capture_sound, 1)
            sounds[SoundType.UPGRADE] = soundPool.load(context, R.raw.upgrade_move, 1)
            sounds[SoundType.ILLEGAL_MOVE] = soundPool.load(context, R.raw.illegal_move, 1)
            sounds[SoundType.NEW_GAME] = soundPool.load(context, R.raw.new_game, 1)
            sounds[SoundType.GAME_OVER] = soundPool.load(context, R.raw.game_over, 1)
            sounds[SoundType.TUTORIAL] = soundPool.load(context, R.raw.tutorial_step, 1)
        } catch (e: Exception) {
            logger.error(e.message.toString())
        }
    }

    fun playSound(soundType: SoundType) {
        if (!soundEnabled) return

        val soundId = sounds[soundType] ?: return
        soundPool.play(soundId, volume, volume, 1, 0, 1.0f)
    }

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0.0f, 1.0f)
    }

    fun release() {
        soundPool.release()
    }
}

