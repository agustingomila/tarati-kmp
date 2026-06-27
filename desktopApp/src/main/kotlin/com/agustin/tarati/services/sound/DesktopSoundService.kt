package com.agustin.tarati.services.sound

import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.shared.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import kotlin.math.log10

/**
 * Implementación Desktop de [ISoundService] con javax.sound.sampled.
 *
 * Los MP3 viven en composeResources (`files/sounds/`) y se decodifican a PCM con el
 * SPI de mp3spi (JLayer) registrado en el classpath. Cada [SoundType] mantiene un
 * [Clip] precargado; al reproducir se reinicia (`framePosition = 0`). Distintos tipos
 * usan clips distintos, por lo que pueden solaparse.
 */
class DesktopSoundService : ISoundService {
    private val logger = getLogger(javaClass.simpleName)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val clips = mutableMapOf<SoundType, Clip>()

    @Volatile
    private var soundEnabled: Boolean = true

    @Volatile
    private var volume: Float = 1.0f

    init {
        scope.launch { preload() }
    }

    private suspend fun preload() {
        SoundType.entries.forEach { type ->
            try {
                clips[type] = createClip(Res.readBytes(type.resourcePath))
            } catch (e: Exception) {
                logger.error("❌ Error loading sound: $type", e)
            }
        }
    }

    /** Decodifica el MP3 a PCM 16-bit y lo abre en un [Clip] reutilizable. */
    private fun createClip(mp3: ByteArray): Clip {
        AudioSystem.getAudioInputStream(ByteArrayInputStream(mp3)).use { mp3Stream ->
            val base = mp3Stream.format
            val pcmFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                base.sampleRate,
                16,
                base.channels,
                base.channels * 2,
                base.sampleRate,
                false,
            )
            AudioSystem.getAudioInputStream(pcmFormat, mp3Stream).use { pcmStream ->
                return AudioSystem.getClip().apply { open(pcmStream) }
            }
        }
    }

    private fun play(type: SoundType) {
        if (!soundEnabled) return
        val clip = clips[type] ?: return
        try {
            clip.stop()
            clip.framePosition = 0
            applyVolume(clip)
            clip.start()
        } catch (e: Exception) {
            logger.error("❌ Error playing sound: $type", e)
        }
    }

    /** Mapea volumen lineal [0,1] al control de ganancia en dB del clip. */
    private fun applyVolume(clip: Clip) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return
        val control = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        control.value = if (volume <= 0f) {
            control.minimum
        } else {
            (20f * log10(volume)).coerceIn(control.minimum, control.maximum)
        }
    }

    override fun playMoveSound(): Unit = play(SoundType.MOVE)
    override fun playCaptureSound(): Unit = play(SoundType.CAPTURE)
    override fun playUpgradeSound(): Unit = play(SoundType.UPGRADE)
    override fun playIllegalMoveSound(): Unit = play(SoundType.ILLEGAL_MOVE)
    override fun playNewGameSound(): Unit = play(SoundType.NEW_GAME)
    override fun playGameOverSound(): Unit = play(SoundType.GAME_OVER)
    override fun playTutorialStepSound(): Unit = play(SoundType.TUTORIAL)

    override fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    override fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0.0f, 1.0f)
    }
}
