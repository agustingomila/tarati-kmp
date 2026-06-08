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
import kotlinx.browser.window
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinConfiguration

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Apply the saved language BEFORE the first composition to avoid a flash of wrong content.
    // WasmSettingsRepository.K_LANGUAGE = "w_language", values are AppLanguage enum names.
    val langCode = when (window.localStorage.getItem("w_language")) {
        "ENGLISH" -> "en"
        "SPANISH" -> "es"
        else -> null
    }
    if (langCode != null) {
        overrideNavigatorLanguages(langCode)
    }

    ComposeViewport(document.body!!) {
        KoinApplication(
            configuration = koinConfiguration(declaration = { modules(webModules) }),
        ) {
            val settingsViewModel: ISettingsViewModel = koinViewModel<WasmSettingsViewModel>()
            val soundService: ISoundService = koinInject()

            WasmLanguageAwareApp(settingsViewModel) {
                CompositionLocalProvider(LocalSoundService provides soundService) {
                    AppContent(settingsViewModel)
                }
            }
        }
    }
}
