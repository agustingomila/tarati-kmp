package com.agustin.tarati.desktop.services.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.agustin.tarati.features.settings.DesktopSettingsViewModel
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.services.localization.LocalAppLanguage
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

/**
 * Desktop equivalent of Android's LanguageAwareApp.
 *
 * **How it works**:
 * - Listens to language changes in SettingsViewModel
 * - Updates JVM's default Locale when language changes
 * - Provides LocalAppLanguage (String) to the composition tree
 *
 * **Differences from Android**:
 * - Android: Uses Context.createConfigurationContext() to localize resources
 * - Desktop: Uses Locale.setDefault() to set JVM-wide locale
 */
@Composable
fun DesktopLanguageAwareApp(
    viewModel: ISettingsViewModel = koinViewModel<DesktopSettingsViewModel>(),
    content: @Composable () -> Unit,
) {
    val settingsState by viewModel.settingsState.collectAsState()

    // Determine current locale based on selected language
    val currentLocale = remember(settingsState.language) {
        when (settingsState.language) {
            AppLanguage.SPANISH -> Locale.forLanguageTag("es")
            AppLanguage.ENGLISH -> Locale.ENGLISH
        }
    }

    // Language code for LocalAppLanguage (String for KMP)
    val languageCode = currentLocale.language

    // Update JVM default locale when language changes
    LaunchedEffect(languageCode) {
        Locale.setDefault(currentLocale)
    }

    // Provide the current language code to the composition tree
    CompositionLocalProvider(
        LocalAppLanguage provides languageCode,  // "es" or "en"
    ) {
        content()
    }
}