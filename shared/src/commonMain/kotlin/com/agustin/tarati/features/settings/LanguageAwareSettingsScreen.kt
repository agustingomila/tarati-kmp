package com.agustin.tarati.features.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import com.agustin.tarati.services.localization.LocalAppLanguage

/**
 * Wrapper para SettingsScreen que fuerza recomposición cuando cambia el idioma.
 *
 * ## Problema
 * `localizedString()` usa `@NonRestartableComposable`, lo que significa que solo se
 * recompone cuando su composable padre se recompone.
 *
 * ## Solución
 * Observar LocalAppLanguage.current (String) y usar `key()` para forzar que todo
 * el árbol de SettingsScreen se reconstruya cuando cambie el código de idioma.
 */
@Composable
fun LanguageAwareSettingsScreen(
    viewModel: ISettingsViewModel,
    events: SettingsEvents,
    onNavigateBack: () -> Unit = {},
    isGameActive: Boolean = false,
    onLogout: (() -> Unit)? = null,
    loggedInUsername: String? = null,
    onNavigateToOnlineSettings: (() -> Unit)? = null,
    onNavigateToAchievements: (() -> Unit)? = null,
    onNavigateToSupporter: (() -> Unit)? = null,
    onNavigateToStore: (() -> Unit)? = null,
) {
    // Observar el código de idioma actual (String: "es", "en", etc.)
    val currentLanguage = LocalAppLanguage.current

    // DEBUG: Ver cuando cambia el idioma en SettingsScreen
    LaunchedEffect(currentLanguage) {
        println("LanguageAwareSettings: Language detected: $currentLanguage")
        println("LanguageAwareSettings: Forcing recomposition with key()")
    }

    // Forzar reconstrucción completa cuando cambie el idioma
    // key() hace que todo el árbol de SettingsScreen se descarte y reconstruya
    key(currentLanguage) {
        SettingsScreen(
            viewModel = viewModel,
            events = events,
            onNavigateBack = onNavigateBack,
            isGameActive = isGameActive,
            onLogout = onLogout,
            loggedInUsername = loggedInUsername,
            onNavigateToOnlineSettings = onNavigateToOnlineSettings,
            onNavigateToAchievements = onNavigateToAchievements,
            onNavigateToSupporter = onNavigateToSupporter,
            onNavigateToStore = onNavigateToStore,
        )
    }
}