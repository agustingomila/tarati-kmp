package com.agustin.tarati.features.settings

import androidx.compose.runtime.Stable
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.ui.theme.AppTheme

@Stable
interface SettingsEvents {
    fun onThemeChange(theme: AppTheme)

    fun onUserNameChange(userName: String)

    fun onLanguageChange(language: AppLanguage)

    fun onLabelsVisibilityChange(visible: Boolean)

    fun onVerticesVisibilityChange(visible: Boolean)

    fun onEdgesVisibilityChange(visible: Boolean)

    fun onRegionsVisibilityChange(visible: Boolean)

    fun onPerimeterVisibilityChange(visible: Boolean)

    fun onAnimateEffectsChange(animate: Boolean)

    fun onPaletteChange(paletteName: String)

    fun onSoundEnabledChange(enabled: Boolean)

    fun onSoundVolumeChange(volume: Float)
}