@file:OptIn(ExperimentalWasmJsInterop::class, ExperimentalEncodingApi::class)

package com.agustin.tarati.web

import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.services.sound.SoundType
import com.agustin.tarati.services.sound.resourcePath
import com.agustin.tarati.shared.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@JsFun("(url, volume) => { const a = new Audio(url); a.volume = volume; a.play().catch(() => {}); }")
private external fun jsPlayAudio(url: String, volume: Double)

/**
 * Implementación Web de [ISoundService]. Los MP3 (composeResources) se precargan como
 * data URLs base64 y se reproducen con un elemento Audio nativo del browser, que
 * decodifica MP3 sin dependencias extra. Cada play crea un Audio nuevo, permitiendo
 * solapamiento de efectos.
 */
class WebSoundService : ISoundService {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val dataUrls = mutableMapOf<SoundType, String>()

    private var soundEnabled: Boolean = true
    private var volume: Float = 1.0f

    init {
        scope.launch { preload() }
    }

    private suspend fun preload() {
        SoundType.entries.forEach { type ->
            try {
                val bytes = Res.readBytes(type.resourcePath)
                dataUrls[type] = "data:audio/mpeg;base64," + Base64.encode(bytes)
            } catch (_: Exception) {
                // Sin sonido para ese tipo si falla la carga; no es crítico.
            }
        }
    }

    private fun play(type: SoundType) {
        if (!soundEnabled) return
        val url = dataUrls[type] ?: return
        jsPlayAudio(url, volume.toDouble())
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
