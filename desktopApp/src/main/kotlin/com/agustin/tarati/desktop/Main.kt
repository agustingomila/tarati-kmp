package com.agustin.tarati.desktop

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.agustin.tarati.desktop.di.desktopModules
import com.agustin.tarati.desktop.services.localization.DesktopLanguageAwareApp
import com.agustin.tarati.features.settings.DesktopSettingsViewModel
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.services.sound.LocalSoundService
import com.agustin.tarati.ui.AppContent
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tarati",
    ) {
        KoinApplication(
            configuration = koinConfiguration(declaration = { modules(desktopModules) }),
            content = {
                val settingsViewModel: ISettingsViewModel = koinViewModel<DesktopSettingsViewModel>()

                // Inject ISoundService from Koin and provide it to the Compose tree
                // via CompositionLocal so GameScreen can access it with LocalSoundService.current
                val soundService: ISoundService = koinInject()

                // Language-aware wrapper - updates JVM Locale when language changes
                DesktopLanguageAwareApp(viewModel = settingsViewModel) {
                    CompositionLocalProvider(LocalSoundService provides soundService) {
                        AppContent(settingsViewModel)
                    }
                }
            })
    }
}