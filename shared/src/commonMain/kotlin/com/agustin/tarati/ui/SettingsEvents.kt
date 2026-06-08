package com.agustin.tarati.ui

import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.features.settings.SettingsEvents
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.ui.theme.AppTheme


fun settingsEvents(viewModel: ISettingsViewModel): SettingsEvents =
    object : SettingsEvents {
        override fun onThemeChange(theme: AppTheme) = viewModel.setAppTheme(theme)

        override fun onUserNameChange(userName: String) = viewModel.setUserName(userName)

        override fun onLanguageChange(language: AppLanguage) = viewModel.setLanguage(language)

        override fun onLabelsVisibilityChange(visible: Boolean) = viewModel.setLabelsVisibility(visible)

        override fun onVerticesVisibilityChange(visible: Boolean) = viewModel.setVerticesVisibility(visible)

        override fun onEdgesVisibilityChange(visible: Boolean) = viewModel.setEdgesVisibility(visible)

        override fun onRegionsVisibilityChange(visible: Boolean) = viewModel.setRegionsVisibility(visible)

        override fun onPerimeterVisibilityChange(visible: Boolean) = viewModel.setPerimeterVisibility(visible)

        override fun onAnimateEffectsChange(animate: Boolean) = viewModel.setAnimateEffects(animate)

        override fun onPaletteChange(paletteName: String) = setCurrentPalette(paletteName)

        override fun onSoundEnabledChange(enabled: Boolean) = viewModel.setSoundEnabled(enabled)

        override fun onSoundVolumeChange(volume: Float) = viewModel.setSoundVolume(volume)
    }