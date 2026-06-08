package com.agustin.tarati.web

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.services.sound.LocalSoundService
import com.agustin.tarati.ui.AppContent
import com.agustin.tarati.web.di.webModules
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinApplication(
            configuration = koinConfiguration(declaration = { modules(webModules) }),
        ) {
            val settingsViewModel: ISettingsViewModel = koinViewModel<WasmSettingsViewModel>()
            val soundService: ISoundService = koinInject()

            CompositionLocalProvider(LocalSoundService provides soundService) {
                AppContent(settingsViewModel)
            }
        }
    }
}
