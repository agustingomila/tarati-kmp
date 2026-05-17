package com.agustin.tarati.services.localization

import android.content.res.Configuration
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.agustin.tarati.features.settings.ISettingsViewModel
import features.settings.AndroidSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import java.util.*

/**
 * Wrapper que actualiza el contexto de Android cuando cambia el idioma.
 *
 * ## IMPORTANTE
 * Provee LocalAppLanguage (String) importado de LocalizedText.kt
 * NO declarar LocalAppLanguage aquí - debe ser la misma instancia de shared.
 */
@Composable
fun LanguageAwareApp(
    viewModel: ISettingsViewModel = koinViewModel<AndroidSettingsViewModel>(),
    content: @Composable () -> Unit,
) {
    val settingsState by viewModel.settingsState.collectAsState()
    val context = LocalContext.current
    val deviceConfig = LocalConfiguration.current

    // Determinar el locale actual basado en el idioma seleccionado
    val currentLocale = when (settingsState.language) {
        AppLanguage.SPANISH -> Locale.forLanguageTag("es")
        AppLanguage.ENGLISH -> Locale.ENGLISH
    }

    // Código de idioma para LocalAppLanguage (String para KMP)
    val languageCode = currentLocale.language

    // DEBUG: Ver cuando cambia el locale
    LaunchedEffect(languageCode) {
        Locale.setDefault(currentLocale)
        Log.d("LanguageAwareApp", "Language changed to: $languageCode")
    }

    // Crear contexto con configuración localizada
    val mergedConfig = Configuration(deviceConfig).apply {
        setLocale(currentLocale)
    }
    val localizedContext = context.createConfigurationContext(mergedConfig)

    // CRÍTICO: Proveer String (código de idioma) a LocalAppLanguage
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalAppLanguage provides languageCode,  // "es" o "en"
    ) {
        content()
    }
}