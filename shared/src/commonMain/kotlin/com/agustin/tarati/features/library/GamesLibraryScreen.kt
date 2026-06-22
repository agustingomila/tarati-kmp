package com.agustin.tarati.features.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.repositories.SavedGame
import com.agustin.tarati.core.domain.game.play.GameState.Companion.parseBoardNotation
import com.agustin.tarati.core.utils.runSync
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.clear_search
import com.agustin.tarati.shared.generated.resources.confirm_delete_game
import com.agustin.tarati.shared.generated.resources.confirm_delete_games
import com.agustin.tarati.shared.generated.resources.date
import com.agustin.tarati.shared.generated.resources.delete
import com.agustin.tarati.shared.generated.resources.delete_game
import com.agustin.tarati.shared.generated.resources.delete_games
import com.agustin.tarati.shared.generated.resources.delete_selected
import com.agustin.tarati.shared.generated.resources.game_deleted
import com.agustin.tarati.shared.generated.resources.games_deleted
import com.agustin.tarati.shared.generated.resources.games_selected
import com.agustin.tarati.shared.generated.resources.moves
import com.agustin.tarati.shared.generated.resources.no_results_for_query
import com.agustin.tarati.shared.generated.resources.no_saves
import com.agustin.tarati.shared.generated.resources.result
import com.agustin.tarati.shared.generated.resources.saved_games
import com.agustin.tarati.shared.generated.resources.search_games
import com.agustin.tarati.shared.generated.resources.select_games
import com.agustin.tarati.ui.components.TooltipIconButton
import com.agustin.tarati.ui.components.library.StaticBoardRenderer
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesLibraryScreen(
    onGameSelected: (gameId: String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: IGamesLibraryViewModel = koinViewModel<GamesLibraryViewModel>(),
) {
    // ── Ambas propiedades son StateFlow: collectAsState() sin parámetro
    // usa la sobrecarga StateFlow<T>.collectAsState() que lee .value de forma
    // síncrona en la primera composición, sin depender de una corutina.
    // Esto garantiza que los datos aparezcan desde el primer frame tanto
    // en producción (sin flash de estado vacío) como en previews.
    val savedGames by viewModel.savedGames.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var selectedGames by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<String?>(null) }
    var gamesToDelete by remember { mutableStateOf<Set<String>>(emptySet()) }
    var removedGames by remember { mutableStateOf<Set<String>>(emptySet()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Rastrear si la barra de búsqueda tiene el foco para evitar que desaparezca
    // mientras el usuario está escribiendo, incluso durante el debounce del ViewModel.
    var searchBarFocused by remember { mutableStateOf(false) }

    // Reset multi-select mode when list becomes empty
    LaunchedEffect(savedGames) {
        if (savedGames.isEmpty()) {
            isMultiSelectMode = false
            selectedGames = emptySet()
        }
    }

    TaratiBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TaratiTopBar(
                    title =
                        if (isMultiSelectMode) {
                            localizedString(Res.string.games_selected, selectedGames.size)
                        } else {
                            localizedString(Res.string.saved_games)
                        },
                    navigationType =
                        if (isMultiSelectMode) {
                            TopBarNavigationType.Close
                        } else {
                            TopBarNavigationType.Back
                        },
                    onNavigationClick = {
                        if (isMultiSelectMode) {
                            isMultiSelectMode = false
                            selectedGames = emptySet()
                        } else {
                            onBack()
                        }
                    },
                    actions = {
                        if (isMultiSelectMode && selectedGames.isNotEmpty()) {
                            TooltipIconButton(
                                tooltip = localizedString(Res.string.delete_selected),
                                onClick = { gamesToDelete = selectedGames },
                            ) {
                                Icon(
                                    TaratiIcons.Delete,
                                    localizedString(Res.string.delete_selected),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        } else if (!isMultiSelectMode && savedGames.isNotEmpty()) {
                            TooltipIconButton(
                                tooltip = localizedString(Res.string.select_games),
                                onClick = { isMultiSelectMode = true },
                            ) {
                                Icon(
                                    TaratiIcons.Check,
                                    localizedString(Res.string.select_games),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->

            val visibleGames = savedGames.filter { it.id !in removedGames }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                // ── Barra de búsqueda ─────────────────────────────────────────────
                // Visible cuando hay partidas guardadas, hay query activa, O la barra
                // tiene el foco. El tercer caso evita el parpadeo al borrar: cuando la
                // query se vacía hay ~300ms de debounce donde savedGames todavía muestra
                // la lista filtrada (potencialmente vacía), y sin searchBarFocused la barra
                // desaparecería en ese intervalo obligando al usuario a volver a tocarla.
                if (!isMultiSelectMode && (savedGames.isNotEmpty() || searchQuery.isNotEmpty() || searchBarFocused)) {
                    LibrarySearchBar(
                        query = searchQuery,
                        onQueryChange = viewModel::setSearchQuery,
                        onFocusChange = { searchBarFocused = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                // ── Contenido principal ───────────────────────────────────────────
                when {
                    // Sin partidas guardadas en absoluto (query vacía)
                    savedGames.isEmpty() && searchQuery.isBlank() -> EmptySavedGames()

                    // Query activa sin resultados
                    visibleGames.isEmpty() && searchQuery.isNotEmpty() -> NoSearchResults(query = searchQuery)

                    // Lista vacía solo por animaciones de eliminación pendientes
                    visibleGames.isEmpty() -> EmptySavedGames()

                    // Lista con elementos
                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(visibleGames) { savedGame ->
                                AnimatedVisibility(
                                    visible = savedGame.id !in removedGames,
                                    exit = fadeOut() + shrinkVertically(
                                        animationSpec = tween(durationMillis = 300),
                                    ),
                                ) {
                                    SavedGameItem(
                                        savedGame = savedGame,
                                        isSelected = selectedGames.contains(savedGame.id),
                                        isMultiSelectMode = isMultiSelectMode,
                                        onSelect = {
                                            if (isMultiSelectMode) {
                                                selectedGames =
                                                    if (selectedGames.contains(savedGame.id)) {
                                                        selectedGames - savedGame.id
                                                    } else {
                                                        selectedGames + savedGame.id
                                                    }
                                            } else {
                                                onGameSelected(savedGame.id)
                                            }
                                        },
                                        onDelete = { gameToDelete = savedGame.id },
                                        onLongPress = {
                                            if (!isMultiSelectMode) {
                                                isMultiSelectMode = true
                                                selectedGames = setOf(savedGame.id)
                                            }
                                        },
                                    ) { viewModel.loadGame(it) }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Diálogo: eliminar un juego ────────────────────────────────────────────
        if (gameToDelete != null) {
            val snackbarText = localizedString(Res.string.game_deleted)

            AlertDialog(
                onDismissRequest = { gameToDelete = null },
                title = { Text(localizedString(Res.string.delete_game)) },
                text = { Text(localizedString(Res.string.confirm_delete_game)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteGame(gameToDelete!!)
                            removedGames = removedGames + gameToDelete!!
                            gameToDelete = null
                            scope.launch { snackbarHostState.showSnackbar(snackbarText) }
                        },
                    ) {
                        Text(localizedString(Res.string.delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { gameToDelete = null }) {
                        Text(localizedString(Res.string.cancel))
                    }
                },
            )
        }

        // ── Diálogo: eliminar múltiples juegos ────────────────────────────────────
        if (gamesToDelete.isNotEmpty()) {
            val snackbarText = localizedString(Res.string.games_deleted)

            AlertDialog(
                onDismissRequest = { gamesToDelete = emptySet() },
                title = { Text(localizedString(Res.string.delete_games)) },
                text = {
                    Text(
                        localizedString(Res.string.confirm_delete_games, gamesToDelete.size),
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            gamesToDelete.forEach { gameId -> viewModel.deleteGame(gameId) }
                            removedGames = removedGames + gamesToDelete
                            selectedGames = emptySet()
                            isMultiSelectMode = false
                            gamesToDelete = emptySet()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    snackbarText.replace("%d", "${removedGames.size}")
                                )
                            }
                        },
                    ) {
                        Text(localizedString(Res.string.delete), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { gamesToDelete = emptySet() }) {
                        Text(localizedString(Res.string.cancel))
                    }
                },
            )
        }
    }
}

// ── Barra de búsqueda ─────────────────────────────────────────────────────────

/**
 * Barra de búsqueda Material 3 en modo no-expandido (sin panel de sugerencias).
 * El filtrado es live; el debounce vive en el ViewModel, no aquí.
 *
 * ## windowInsets = WindowInsets(0)
 * Elimina el padding de status-bar que [SearchBar] aplica por defecto, pensado
 * para cuando la barra reemplaza la TopBar. Aquí va debajo de [TaratiTopBar]
 * dentro de un [Scaffold] que ya gestiona los insets.
 *
 * ## Foco al borrar
 * El botón de limpiar usa [FocusRequester.requestFocus] para devolver el foco
 * al campo de texto inmediatamente después de vaciar la query, evitando que el
 * teclado se oculte y que el usuario tenga que tocar la barra de nuevo.
 *
 * @param onFocusChange Callback que recibe `true` cuando la barra gana foco y
 *                      `false` cuando lo pierde. Usado por la pantalla padre para
 *                      mantener la barra visible durante el debounce del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibrarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    SearchBar(
        modifier = modifier,
        windowInsets = WindowInsets(0),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChange(it.isFocused) },
                placeholder = {
                    Text(
                        text = localizedString(Res.string.search_games),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = TaratiIcons.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                                // Devolver el foco al campo tras borrar para que el
                                // teclado permanezca visible y el usuario pueda seguir
                                // escribiendo sin necesidad de tocar la barra de nuevo.
                                focusRequester.requestFocus()
                            },
                        ) {
                            Icon(
                                imageVector = TaratiIcons.Close,
                                contentDescription = localizedString(Res.string.clear_search),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else null,
            )
        },
        expanded = false,
        onExpandedChange = {},
    ) {}
}

// ── Estados vacíos ────────────────────────────────────────────────────────────

@Composable
fun EmptySavedGames() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = localizedString(Res.string.no_saves),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun NoSearchResults(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = TaratiIcons.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
            Text(
                text = localizedString(Res.string.no_results_for_query, query),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }
    }
}

// ── Ítems de la lista ─────────────────────────────────────────────────────────

@Composable
fun SavedGameItem(
    savedGame: SavedGame,
    isSelected: Boolean = false,
    isMultiSelectMode: Boolean = false,
    onSelect: () -> Unit = {},
    onDelete: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onLoadGame: (gameId: String) -> Flow<MatchDto?> = { flowOf(null) },
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .combinedClickable(
                    onClick = onSelect,
                    onLongClick = onLongPress,
                ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isMultiSelectMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelect() },
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            GameThumbnail(
                modifier = Modifier.size(80.dp),
                gameId = savedGame.id,
                loadGame = onLoadGame,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${savedGame.whitePlayer} vs ${savedGame.blackPlayer}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                GameInfoRow(
                    label = localizedString(Res.string.result),
                    value = savedGame.result,
                )
                GameInfoRow(
                    label = localizedString(Res.string.date),
                    value = savedGame.date,
                )
                GameInfoRow(
                    label = localizedString(Res.string.moves),
                    value = savedGame.moveCount.toString(),
                )
            }

            if (!isMultiSelectMode) {
                Spacer(modifier = Modifier.width(16.dp))
                TooltipIconButton(
                    tooltip = localizedString(Res.string.delete_game),
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        TaratiIcons.Delete,
                        localizedString(Res.string.delete_game),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun GameInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun GameThumbnail(
    modifier: Modifier = Modifier,
    gameId: String,
    loadGame: (gameId: String) -> Flow<MatchDto?> = { flowOf(null) },
) {
    // En previews de Compose, LaunchedEffect no ejecuta sus coroutines:
    // matchDto quedaría siempre null y se mostraría el spinner en lugar del tablero.
    //
    // En modo inspection usamos `remember { runBlocking { flow.first() } }` para
    // recoger la primera emisión del flow de forma sincrónICA, antes de que la
    // composición se renderice. flowOf(dto) emite instantáneamente, así que
    // runBlocking retorna de inmediato sin bloquear nada en la práctica.
    val inPreview = LocalInspectionMode.current

    val matchDto by if (inPreview) {
        val syncDto = remember(gameId) {
            runSync { loadGame(gameId).firstOrNull() }
        }
        remember(syncDto) { mutableStateOf(syncDto) }
    } else {
        val state = remember(gameId) { mutableStateOf<MatchDto?>(null) }
        LaunchedEffect(gameId) {
            loadGame(gameId).collect { dto -> state.value = dto }
        }
        state
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val dto = matchDto
        if (dto != null) {
            StaticBoardRenderer(
                modifier = Modifier.fillMaxSize(),
                gameState = parseBoardNotation(dto.game.boardPosition),
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}