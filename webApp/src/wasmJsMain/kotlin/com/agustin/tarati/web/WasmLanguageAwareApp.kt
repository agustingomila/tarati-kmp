package com.agustin.tarati.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.services.localization.LocalAppLanguage

/**
 * WASM equivalent of Android's LanguageAwareApp / Desktop's DesktopLanguageAwareApp.
 *
 * ## Problema
 * En WASM, compose-resources resuelve strings via DefaultComposeEnvironment que lee
 * window.navigator.languages. No hay API pública para overridear LocalComposeEnvironment
 * (es internal). La única forma de cambiar el locale que usa stringResource() en WASM
 * es modificar window.navigator.languages antes de que los hijos compongan.
 *
 * ## Solución
 * 1. remember(languageCode) { overrideNavigatorLanguages(lang) } ejecuta el override JS
 *    sincrónicamente ANTES de que los hijos compongan, aprovechando que remember() corre
 *    durante la fase de composición.
 * 2. CompositionLocalProvider(LocalAppLanguage provides languageCode) hace que key() en
 *    localizedString() invalide el árbol cuando cambia el idioma, forzando a stringResource()
 *    a re-leer el ResourceEnvironment con el nuevo navigator.languages.
 */
@Composable
fun WasmLanguageAwareApp(
    viewModel: ISettingsViewModel,
    content: @Composable () -> Unit,
) {
    val settingsState by viewModel.settingsState.collectAsState()

    val languageCode = when (settingsState.language) {
        AppLanguage.SPANISH -> "es"
        AppLanguage.ENGLISH -> "en"
    }

    // Override navigator.languages synchronously so compose-resources picks up
    // the correct locale on the same recomposition that changes LocalAppLanguage.
    @Suppress("RememberReturnType")
    remember(languageCode) {
        overrideNavigatorLanguages(languageCode)
    }

    CompositionLocalProvider(LocalAppLanguage provides languageCode) {
        content()
    }
}

/**
 * Overrides window.navigator.languages by adding an own property on the navigator
 * instance that shadows the prototype getter.
 *
 * navigator.languages is defined on Navigator.prototype (configurable: true), but not
 * on the navigator instance itself. Object.defineProperty on the instance adds a new
 * own property without touching the prototype, which always succeeds.
 * Subsequent calls re-define the (already configurable) own property.
 */
internal fun overrideNavigatorLanguages(lang: String) {
    js("try { Object.defineProperty(window.navigator, 'languages', { value: [lang], configurable: true, writable: true }) } catch(e) { console.warn('[Tarati] navigator.languages override failed:', e) }")
}
