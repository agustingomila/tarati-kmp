package com.agustin.tarati.features.settings.previews

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.agustin.tarati.features.settings.LanguageSetting
import com.agustin.tarati.features.settings.PaletteSetting
import com.agustin.tarati.features.settings.SettingItem
import com.agustin.tarati.features.settings.SettingsCategory
import com.agustin.tarati.features.settings.ThemeSetting
import com.agustin.tarati.features.settings.ToggleSetting
import com.agustin.tarati.features.settings.UserNameSetting
import com.agustin.tarati.features.settings.VolumeSetting
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.animate_effects
import com.agustin.tarati.shared.generated.resources.animations
import com.agustin.tarati.shared.generated.resources.appearance
import com.agustin.tarati.shared.generated.resources.board_display
import com.agustin.tarati.shared.generated.resources.board_labels
import com.agustin.tarati.shared.generated.resources.board_perimeter
import com.agustin.tarati.shared.generated.resources.board_regions
import com.agustin.tarati.shared.generated.resources.board_vertices
import com.agustin.tarati.shared.generated.resources.dark_theme
import com.agustin.tarati.shared.generated.resources.general
import com.agustin.tarati.shared.generated.resources.language
import com.agustin.tarati.ui.theme.AppTheme
import com.agustin.tarati.ui.theme.PaletteList
import com.agustin.tarati.ui.theme.TaratiIcons
import com.agustin.tarati.ui.theme.TaratiTheme
import com.agustin.tarati.ui.theme.availablePalettes as allPalettes

@Preview(name = "Language Setting")
@Composable
fun LanguageSettingPreview() {
    TaratiTheme {
        Column {
            LanguageSetting(
                language = AppLanguage.SPANISH,
                onLanguageChange = {},
            )
            LanguageSetting(
                language = AppLanguage.ENGLISH,
                onLanguageChange = {},
            )
        }
    }
}

@Preview(name = "Theme Setting")
@Composable
fun ThemeSettingPreview() {
    TaratiTheme {
        Column {
            ThemeSetting(
                theme = AppTheme.MODE_AUTO,
                onThemeChange = {},
            )
            ThemeSetting(
                theme = AppTheme.MODE_NIGHT,
                onThemeChange = {},
            )
        }
    }
}

@Preview(name = "Palette Setting")
@Composable
fun PaletteSettingPreview() {
    TaratiTheme {
        PaletteSetting(
            paletteName = "Default Palette",
            availablePalettes = PaletteList(allPalettes),
            onPaletteSelected = {},
        )
    }
}

@Preview(name = "Toggle Settings")
@Composable
fun ToggleSettingsPreview() {
    TaratiTheme {
        Column {
            ToggleSetting(
                icon = TaratiIcons.Visibility,
                title = Res.string.board_labels,
                checked = true,
                onCheckedChange = {},
            )
            ToggleSetting(
                icon = TaratiIcons.Visibility,
                title = Res.string.board_vertices,
                checked = false,
                onCheckedChange = {},
            )
            ToggleSetting(
                icon = TaratiIcons.Animation,
                title = Res.string.animate_effects,
                checked = true,
                onCheckedChange = {},
            )
            ToggleSetting(
                icon = TaratiIcons.Visibility,
                title = Res.string.board_regions,
                checked = false,
                onCheckedChange = {},
            )
            ToggleSetting(
                icon = TaratiIcons.Visibility,
                title = Res.string.board_perimeter,
                checked = false,
                onCheckedChange = {},
            )
        }
    }
}

@Preview(name = "Setting Item")
@Composable
fun SettingItemPreview() {
    TaratiTheme {
        Column {
            SettingItem(
                icon = TaratiIcons.Language,
                title = Res.string.language,
                subtitle = Res.string.language,
                trailingContent = {
                    Text("ES", color = MaterialTheme.colorScheme.primary)
                },
            )
            SettingItem(
                icon = TaratiIcons.DarkMode,
                title = Res.string.dark_theme,
                subtitle = Res.string.dark_theme,
                trailingContent = {
                    Switch(checked = true, onCheckedChange = {})
                },
            )
        }
    }
}

@Preview(name = "User Name Setting")
@Composable
fun UserNameSettingPreview() {
    TaratiTheme {
        Column {
            UserNameSetting(
                userName = "Juan Pérez",
                onUserNameChange = {},
            )
            UserNameSetting(
                userName = "",
                onUserNameChange = {},
            )
        }
    }
}

@Preview(name = "Volume Setting")
@Composable
fun VolumeSettingPreview() {
    TaratiTheme {
        Column {
            VolumeSetting(
                volume = 0.5f,
                enabled = true,
                onVolumeChange = {},
            )
            VolumeSetting(
                volume = 0f,
                enabled = false,
                onVolumeChange = {},
            )
        }
    }
}

@Preview(name = "Settings Categories")
@Composable
fun SettingsCategoriesPreview() {
    TaratiTheme {
        Column {
            SettingsCategory(title = Res.string.general)
            SettingsCategory(title = Res.string.appearance)
            SettingsCategory(title = Res.string.board_display)
            SettingsCategory(title = Res.string.animations)
        }
    }
}
