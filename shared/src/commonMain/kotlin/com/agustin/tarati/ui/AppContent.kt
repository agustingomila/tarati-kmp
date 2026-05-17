package com.agustin.tarati.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.features.settings.SettingsViewModel
import com.agustin.tarati.ui.components.navigation.NavGraph
import com.agustin.tarati.ui.theme.AppTheme
import com.agustin.tarati.ui.theme.PaletteManager
import com.agustin.tarati.ui.theme.TaratiTheme
import com.agustin.tarati.ui.theme.availablePalettes
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable raíz compartido entre Android y Desktop.
 *
 * ## KMP
 * Usa koinViewModel<ISettingsViewModel>() — Koin resuelve la implementación
 * concreta según el módulo registrado en cada plataforma:
 * - Android → SettingsViewModel (con DataStore + Billing + Achievements)
 * - Desktop → DesktopSettingsViewModel (in-memory, sin billing)
 */
@Composable
fun AppContent(
    settingsViewModel: ISettingsViewModel = koinViewModel<SettingsViewModel>(),
) {
    val settings by settingsViewModel.settingsState.collectAsState()

    setCurrentPalette(settings.palette)

    val useDarkTheme = when (settings.appTheme) {
        AppTheme.MODE_AUTO -> isSystemInDarkTheme()
        AppTheme.MODE_DAY -> false
        AppTheme.MODE_NIGHT -> true
    }

    TaratiTheme(darkTheme = useDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavGraph(settingsViewModel = settingsViewModel)
        }
    }
}

fun setCurrentPalette(paletteName: String) {
    val currentPalette =
        availablePalettes.find { it.name == paletteName }
            ?: availablePalettes.first()
    PaletteManager.setPalette(currentPalette)
}