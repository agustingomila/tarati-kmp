package com.agustin.tarati.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.billing.LockedPalettes
import com.agustin.tarati.services.billing.OwnedProducts
import com.agustin.tarati.services.billing.PaletteProducts
import com.agustin.tarati.services.localization.AppLanguage
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.animate_effects
import com.agustin.tarati.shared.generated.resources.animations
import com.agustin.tarati.shared.generated.resources.appearance
import com.agustin.tarati.shared.generated.resources.auto_theme
import com.agustin.tarati.shared.generated.resources.board_display
import com.agustin.tarati.shared.generated.resources.board_edges
import com.agustin.tarati.shared.generated.resources.board_labels
import com.agustin.tarati.shared.generated.resources.board_perimeter
import com.agustin.tarati.shared.generated.resources.board_regions
import com.agustin.tarati.shared.generated.resources.board_vertices
import com.agustin.tarati.shared.generated.resources.color_palette
import com.agustin.tarati.shared.generated.resources.conversion_animation
import com.agustin.tarati.shared.generated.resources.conversion_animation_flip
import com.agustin.tarati.shared.generated.resources.conversion_animation_surprise
import com.agustin.tarati.shared.generated.resources.conversion_animation_transform
import com.agustin.tarati.shared.generated.resources.dark_theme
import com.agustin.tarati.shared.generated.resources.disabled
import com.agustin.tarati.shared.generated.resources.enabled
import com.agustin.tarati.shared.generated.resources.english
import com.agustin.tarati.shared.generated.resources.gameplay
import com.agustin.tarati.shared.generated.resources.general
import com.agustin.tarati.shared.generated.resources.language
import com.agustin.tarati.shared.generated.resources.light_theme
import com.agustin.tarati.shared.generated.resources.login_account
import com.agustin.tarati.shared.generated.resources.login_logged_in_as
import com.agustin.tarati.shared.generated.resources.login_logout
import com.agustin.tarati.shared.generated.resources.piece_type
import com.agustin.tarati.shared.generated.resources.pre_moves
import com.agustin.tarati.shared.generated.resources.save
import com.agustin.tarati.shared.generated.resources.select_color_palette
import com.agustin.tarati.shared.generated.resources.settings
import com.agustin.tarati.shared.generated.resources.sound
import com.agustin.tarati.shared.generated.resources.sound_disabled
import com.agustin.tarati.shared.generated.resources.sound_effects
import com.agustin.tarati.shared.generated.resources.spanish
import com.agustin.tarati.shared.generated.resources.user_name
import com.agustin.tarati.shared.generated.resources.user_name_empty
import com.agustin.tarati.shared.generated.resources.volume
import com.agustin.tarati.shared.generated.resources.volume_high
import com.agustin.tarati.shared.generated.resources.volume_low
import com.agustin.tarati.shared.generated.resources.volume_medium
import com.agustin.tarati.shared.generated.resources.volume_muted
import com.agustin.tarati.ui.components.game.draw.pieces.ConversionAnimationStyle
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypeSelector
import com.agustin.tarati.ui.components.game.draw.pieces.PieceTypes
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.theme.AppTheme
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.GildedPalette
import com.agustin.tarati.ui.theme.PaletteList
import com.agustin.tarati.ui.theme.TaratiIcons
import com.agustin.tarati.ui.theme.getBoardColors
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ISettingsViewModel = koinViewModel<SettingsViewModel>(),
    events: SettingsEvents,
    onNavigateBack: () -> Unit = {},
    isGameActive: Boolean = false,
    onLogout: (() -> Unit)? = null,
    loggedInUsername: String? = null,
) {
    val settingsState by viewModel.settingsState.collectAsState()
    // Los colores de tablero se leen desde el CompositionLocal activo, igual que
    // en el resto de la app. Se pasan explícitamente al selector de piezas para
    // que el preview de las piezas use siempre la paleta activa en tiempo real.
    val boardColors = getBoardColors()
    val rawPurchasedIds by viewModel.purchasedProductIds.collectAsState()
    val purchasedProductIds = remember(rawPurchasedIds) { OwnedProducts(rawPurchasedIds) }
    val allPalettesForSelector by viewModel.allPalettesForSelector.collectAsState()
    val lockedPalettes by viewModel.lockedPalettes.collectAsState()

    Scaffold(
        topBar = {
            TaratiTopBar(
                title = localizedString(Res.string.settings),
                navigationType = TopBarNavigationType.Back,
                onNavigationClick = onNavigateBack,
            )
        },
    ) { padding ->
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                SettingsCategory(title = Res.string.general)
                UserNameSetting(
                    userName = settingsState.userName,
                    onUserNameChange = { name ->
                        viewModel.setUserName(name)
                        events.onUserNameChange(name)
                    },
                )
                LanguageSetting(
                    language = settingsState.language,
                    onLanguageChange = events::onLanguageChange,
                )

                SettingsCategory(title = Res.string.gameplay)
                TimeControlSetting(
                    mode = settingsState.timeControl,
                    isGameActive = isGameActive,
                    onModeSelected = { mode -> viewModel.setTimeControl(mode) },
                )
                ToggleSetting(
                    icon = TaratiIcons.Speed,
                    title = Res.string.pre_moves,
                    checked = settingsState.preMovesEnabled,
                    onCheckedChange = { enabled -> viewModel.setPreMovesEnabled(enabled) },
                )

                SettingsCategory(title = Res.string.appearance)
                ThemeSetting(
                    theme = settingsState.appTheme,
                    onThemeChange = events::onThemeChange,
                )
                PaletteSetting(
                    paletteName = settingsState.palette,
                    availablePalettes = allPalettesForSelector,
                    lockedPalettes = lockedPalettes,
                    onPaletteSelected = { palette ->
                        viewModel.setPalette(palette)
                        events.onPaletteChange(palette)
                    },
                    onPurchasePalette = { productId ->
                        viewModel.launchPurchaseFlow(productId)
                    },
                )
                PieceTypeSetting(
                    selectedPieceTypeId = settingsState.pieceTypeId,
                    boardColors = boardColors,
                    purchasedProductIds = purchasedProductIds,
                    onPieceTypeSelected = { pieceTypeId ->
                        viewModel.setPieceType(pieceTypeId)
                    },
                    onPurchasePieceType = { productId ->
                        viewModel.launchPurchaseFlow(productId)
                    },
                )

                SettingsCategory(title = Res.string.board_display)
                ToggleSetting(
                    icon = TaratiIcons.Visibility,
                    title = Res.string.board_labels,
                    checked = settingsState.boardVisualState.labelsVisibles,
                    onCheckedChange = { visible ->
                        viewModel.setLabelsVisibility(visible)
                        events.onLabelsVisibilityChange(visible)
                    },
                )
                ToggleSetting(
                    icon = TaratiIcons.Visibility,
                    title = Res.string.board_vertices,
                    checked = settingsState.boardVisualState.verticesVisibles,
                    onCheckedChange = { visible ->
                        viewModel.setVerticesVisibility(visible)
                        events.onVerticesVisibilityChange(visible)
                    },
                )
                ToggleSetting(
                    icon = TaratiIcons.Visibility,
                    title = Res.string.board_edges,
                    checked = settingsState.boardVisualState.edgesVisibles,
                    onCheckedChange = { visible ->
                        viewModel.setEdgesVisibility(visible)
                        events.onEdgesVisibilityChange(visible)
                    },
                )
                ToggleSetting(
                    icon = TaratiIcons.Visibility,
                    title = Res.string.board_regions,
                    checked = settingsState.boardVisualState.regionsVisibles,
                    onCheckedChange = { visible ->
                        viewModel.setRegionsVisibility(visible)
                        events.onRegionsVisibilityChange(visible)
                    },
                )
                ToggleSetting(
                    icon = TaratiIcons.Visibility,
                    title = Res.string.board_perimeter,
                    checked = settingsState.boardVisualState.perimeterVisible,
                    onCheckedChange = { visible ->
                        viewModel.setPerimeterVisibility(visible)
                        events.onPerimeterVisibilityChange(visible)
                    },
                )

                SettingsCategory(title = Res.string.animations)
                ToggleSetting(
                    icon = TaratiIcons.Animation,
                    title = Res.string.animate_effects,
                    checked = settingsState.boardVisualState.animateEffects,
                    onCheckedChange = { animate ->
                        viewModel.setAnimateEffects(animate)
                        events.onAnimateEffectsChange(animate)
                    },
                )
                ConversionAnimationSetting(
                    style = settingsState.boardVisualState.conversionAnimationStyle,
                    onStyleSelected = { style ->
                        viewModel.setConversionAnimationStyle(style)
                    },
                )

                SettingsCategory(title = Res.string.sound)
                ToggleSetting(
                    icon = TaratiIcons.VolumeUp,
                    title = Res.string.sound_effects,
                    checked = settingsState.soundState.soundEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.setSoundEnabled(enabled)
                        events.onSoundEnabledChange(enabled)
                    },
                )

                VolumeSetting(
                    volume = settingsState.soundState.soundVolume,
                    enabled = settingsState.soundState.soundEnabled,
                    onVolumeChange = { volume ->
                        viewModel.setSoundVolume(volume)
                        events.onSoundVolumeChange(volume)
                    },
                )

                if (onLogout != null) {
                    AccountSection(
                        username = loggedInUsername,
                        onLogout = onLogout,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AccountSection(
    username: String?,
    onLogout: () -> Unit,
) {
    SettingsCategory(title = Res.string.login_account)

    if (!username.isNullOrBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = TaratiIcons.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = localizedString(Res.string.login_logged_in_as).replace($$"%1$s", username),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLogout)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = TaratiIcons.Logout,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = localizedString(Res.string.login_logout),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
fun SettingsCategory(
    title: StringResource,
) {
    Text(
        text = localizedString(title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

@Composable
fun UserNameSetting(
    userName: String,
    onUserNameChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }

    SettingItem(
        icon = TaratiIcons.Person,
        title = localizedString(Res.string.user_name),
        subtitle = userName.ifBlank { localizedString(Res.string.user_name_empty) },
    ) {
        Box {
            Row(
                modifier =
                    Modifier
                        .clickable { expanded = true }
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = userName.ifBlank { localizedString(Res.string.user_name_empty) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = TaratiIcons.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                // Campo de texto para editar el nombre
                DropdownMenuItem(
                    text = {
                        TextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text(localizedString(Res.string.user_name)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    onClick = { },
                )
                // Botón para guardar
                DropdownMenuItem(
                    text = {
                        Text(
                            text = localizedString(Res.string.save),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    onClick = {
                        onUserNameChange(tempName)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun LanguageSetting(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    SettingItem(
        icon = TaratiIcons.Language,
        title =
            when (language) {
                AppLanguage.SPANISH -> Res.string.spanish
                AppLanguage.ENGLISH -> Res.string.english
            },
        subtitle = Res.string.language,
    ) {
        Box {
            Row(
                modifier =
                    Modifier
                        .clickable { expanded = true }
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        localizedString(
                            when (language) {
                                AppLanguage.SPANISH -> Res.string.spanish
                                AppLanguage.ENGLISH -> Res.string.english
                            },
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = TaratiIcons.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(localizedString(Res.string.spanish)) },
                    onClick = {
                        onLanguageChange(AppLanguage.SPANISH)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(localizedString(Res.string.english)) },
                    onClick = {
                        onLanguageChange(AppLanguage.ENGLISH)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun ThemeSetting(
    theme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
) {
    SettingItem(
        icon = TaratiIcons.DarkMode,
        title =
            when (theme) {
                AppTheme.MODE_AUTO -> Res.string.auto_theme
                AppTheme.MODE_DAY -> Res.string.light_theme
                AppTheme.MODE_NIGHT -> Res.string.dark_theme
            },
        subtitle = Res.string.appearance,
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            Row(
                modifier =
                    Modifier
                        .clickable { expanded = true }
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text =
                        localizedString(
                            when (theme) {
                                AppTheme.MODE_AUTO -> Res.string.auto_theme
                                AppTheme.MODE_DAY -> Res.string.light_theme
                                AppTheme.MODE_NIGHT -> Res.string.dark_theme
                            },
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = TaratiIcons.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(localizedString(Res.string.auto_theme)) },
                    onClick = {
                        onThemeChange(AppTheme.MODE_AUTO)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(localizedString(Res.string.light_theme)) },
                    onClick = {
                        onThemeChange(AppTheme.MODE_DAY)
                        expanded = false
                    },
                )
                DropdownMenuItem(
                    text = { Text(localizedString(Res.string.dark_theme)) },
                    onClick = {
                        onThemeChange(AppTheme.MODE_NIGHT)
                        expanded = false
                    },
                )
            }
        }
    }
}

/**
 * Selector visual de paleta: cabecera con nombre activo + minitableros para cada paleta disponible.
 *
 * Cada paleta se renderiza con sus propios colores reales, aislada del estado global de paleta.
 *
 * @param paletteName       Nombre de la paleta activa (para la etiqueta del subtítulo).
 * @param availablePalettes Paletas actualmente disponibles para el usuario.
 * @param onPaletteSelected Callback que recibe el nombre de la paleta elegida.
 */
@Composable
fun PaletteSetting(
    paletteName: String,
    availablePalettes: PaletteList,
    onPaletteSelected: (String) -> Unit,
    lockedPalettes: LockedPalettes = LockedPalettes.None,
    onPurchasePalette: (productId: String) -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SettingItem(
            icon = TaratiIcons.Palette,
            title = Res.string.color_palette,
            subtitle = Res.string.select_color_palette,
        ) {
            Text(
                text = paletteName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp),
            )
        }

        PaletteSelector(
            selectedPaletteName = paletteName,
            palettes = availablePalettes,
            lockedPalettes = lockedPalettes,
            onSelect = { palette -> onPaletteSelected(palette.name) },
            onPurchase = { paletteName ->
                // Buscar el product ID correspondiente al nombre de paleta.
                // Por ahora solo Gilded tiene IAP; cuando haya más se puede externalizar.
                val productId = when (paletteName) {
                    GildedPalette.name ->
                        PaletteProducts.GILDED

                    else -> return@PaletteSelector
                }
                onPurchasePalette(productId)
            },
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PieceTypeSetting
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Setting de tipo de pieza: cabecera con nombre del tipo activo + selector visual.
 *
 * Se ubica en la categoría "Apariencia", justo debajo de [PaletteSetting].
 * El selector muestra todas las piezas girando con los colores de la paleta activa,
 * permitiendo al usuario ver exactamente cómo lucirán sus piezas antes de confirmar.
 *
 * @param selectedPieceTypeId  Id del tipo actualmente seleccionado.
 * @param boardColors          Paleta activa — las piezas del selector la usan en tiempo real.
 * @param onPieceTypeSelected  Callback que recibe el id del tipo elegido.
 */
@Composable
fun PieceTypeSetting(
    selectedPieceTypeId: String,
    boardColors: BoardColors,
    onPieceTypeSelected: (String) -> Unit,
    purchasedProductIds: OwnedProducts = OwnedProducts.None,
    onPurchasePieceType: (productId: String) -> Unit = {},
) {
    val selectedName = localizedString(PieceTypes.findById(selectedPieceTypeId).nameRes)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabecera: icono + título + nombre del tipo activo (igual que otros SettingItem)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = TaratiIcons.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = localizedString(Res.string.piece_type),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = selectedName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Selector visual: LazyRow de piezas animadas
        PieceTypeSelector(
            selectedId = selectedPieceTypeId,
            boardColors = boardColors,
            purchasedIds = purchasedProductIds,
            onSelect = { pieceType -> onPieceTypeSelected(pieceType.id) },
            onPurchase = { pieceType ->
                pieceType.productId?.let { onPurchasePieceType(it) }
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
        )
    }
}

@Composable
fun ToggleSetting(
    icon: ImageVector,
    title: StringResource,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingItem(
        icon = icon,
        title = title,
        subtitle = if (checked) Res.string.enabled else Res.string.disabled,
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
fun ConversionAnimationSetting(
    style: ConversionAnimationStyle,
    onStyleSelected: (ConversionAnimationStyle) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val labelRes = { s: ConversionAnimationStyle ->
        when (s) {
            ConversionAnimationStyle.TRANSFORMATION -> Res.string.conversion_animation_transform
            ConversionAnimationStyle.FLIP -> Res.string.conversion_animation_flip
            ConversionAnimationStyle.SURPRISE -> Res.string.conversion_animation_surprise
        }
    }

    SettingItem(
        icon = TaratiIcons.Animation,
        title = Res.string.conversion_animation,
        subtitle = labelRes(style),
    ) {
        Box {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = localizedString(labelRes(style)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = TaratiIcons.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                ConversionAnimationStyle.entries.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(localizedString(labelRes(s))) },
                        onClick = {
                            onStyleSelected(s)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun VolumeSetting(
    volume: Float,
    enabled: Boolean,
    onVolumeChange: (Float) -> Unit,
) {
    SettingItem(
        icon = TaratiIcons.VolumeDown,
        title = Res.string.volume,
        subtitle =
            if (enabled) {
                when {
                    volume == 0f -> Res.string.volume_muted
                    volume < 0.3f -> Res.string.volume_low
                    volume < 0.7f -> Res.string.volume_medium
                    else -> Res.string.volume_high
                }
            } else {
                Res.string.sound_disabled
            },
    ) {
        val volumeIcon =
            when {
                !enabled -> TaratiIcons.VolumeOff
                volume == 0f -> TaratiIcons.VolumeMute
                volume < 0.5f -> TaratiIcons.VolumeDown
                else -> TaratiIcons.VolumeUp
            }

        Icon(
            imageVector = volumeIcon,
            contentDescription = null,
            tint =
                if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            modifier = Modifier.padding(end = 16.dp),
        )
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 56.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = TaratiIcons.VolumeMute,
            contentDescription = null,
            tint =
                if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                },
        )

        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            enabled = enabled,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            valueRange = 0f..1f,
        )

        Icon(
            imageVector = TaratiIcons.VolumeUp,
            contentDescription = null,
            tint =
                if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                },
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: StringResource,
    subtitle: StringResource,
    trailingContent: @Composable () -> Unit = {},
) {
    SettingItem(
        icon = icon,
        title = localizedString(title),
        subtitle = localizedString(subtitle),
        trailingContent = trailingContent,
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 16.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        trailingContent()
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
    )
}

