package com.agustin.tarati.features.online.lobby


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.cobColorByDescription
import com.agustin.tarati.core.domain.game.play.GameState.Companion.parseBoardNotation
import com.agustin.tarati.core.domain.game.time.TimeControl
import com.agustin.tarati.features.library.StaticBoardRenderer
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.auth.UserInfo
import com.agustin.tarati.features.online.connection.ConnectionState
import com.agustin.tarati.features.online.connection.IConnectionViewModel
import com.agustin.tarati.features.online.devServerUrl
import com.agustin.tarati.features.online.game.IOnlineGameViewModel
import com.agustin.tarati.features.online.tournament.ITournamentViewModel
import com.agustin.tarati.features.online.tournament.TournamentViewModel
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.network.models.CreateTournamentRequest
import com.agustin.tarati.network.models.GameHistoryDto
import com.agustin.tarati.network.models.GameTimeControl
import com.agustin.tarati.network.models.LiveGameDto
import com.agustin.tarati.network.models.MatchmakingState
import com.agustin.tarati.network.models.MatchmakingTicket
import com.agustin.tarati.network.models.OnlineGameStatus
import com.agustin.tarati.network.models.OnlineUserDto
import com.agustin.tarati.network.models.OnlineUserStatus
import com.agustin.tarati.network.models.OpenSearchDto
import com.agustin.tarati.network.models.TournamentStatus
import com.agustin.tarati.network.models.TournamentSummaryDto
import com.agustin.tarati.network.models.TournamentType
import com.agustin.tarati.services.localization.LocalizedText
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.allow_spectators
import com.agustin.tarati.shared.generated.resources.auth_guest_banner_title
import com.agustin.tarati.shared.generated.resources.auth_guest_description
import com.agustin.tarati.shared.generated.resources.auth_logout
import com.agustin.tarati.shared.generated.resources.auth_logout_confirm
import com.agustin.tarati.shared.generated.resources.auth_sign_in
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.casual_info_card
import com.agustin.tarati.shared.generated.resources.social_challenge
import com.agustin.tarati.shared.generated.resources.social_challenge_dialog_title
import com.agustin.tarati.shared.generated.resources.clear_filters
import com.agustin.tarati.shared.generated.resources.confirm
import com.agustin.tarati.shared.generated.resources.connect_to_server_first
import com.agustin.tarati.shared.generated.resources.lobby_connected_tab
import com.agustin.tarati.shared.generated.resources.could_not_connect
import com.agustin.tarati.shared.generated.resources.create
import com.agustin.tarati.shared.generated.resources.create_tournament
import com.agustin.tarati.shared.generated.resources.draw
import com.agustin.tarati.shared.generated.resources.error
import com.agustin.tarati.shared.generated.resources.social_feed
import com.agustin.tarati.shared.generated.resources.social_feed_player_context
import com.agustin.tarati.shared.generated.resources.lobby_filter_all
import com.agustin.tarati.shared.generated.resources.lobby_filter_live_games
import com.agustin.tarati.shared.generated.resources.lobby_filter_open_searches
import com.agustin.tarati.shared.generated.resources.lobby_filter_registered_only
import com.agustin.tarati.shared.generated.resources.lobby_in_live
import com.agustin.tarati.shared.generated.resources.join
import com.agustin.tarati.shared.generated.resources.profile_leaderboard
import com.agustin.tarati.shared.generated.resources.loss
import com.agustin.tarati.shared.generated.resources.max_players
import com.agustin.tarati.shared.generated.resources.min_players
import com.agustin.tarati.shared.generated.resources.move
import com.agustin.tarati.shared.generated.resources.moves
import com.agustin.tarati.shared.generated.resources.lobby_my_games
import com.agustin.tarati.shared.generated.resources.lobby_new_search
import com.agustin.tarati.shared.generated.resources.social_no_feed_games
import com.agustin.tarati.shared.generated.resources.no_games_found
import com.agustin.tarati.shared.generated.resources.lobby_no_players_match_filters
import com.agustin.tarati.shared.generated.resources.no_tournaments_available
import com.agustin.tarati.shared.generated.resources.no_tournaments_match_filters
import com.agustin.tarati.shared.generated.resources.lobby_not_connected_to_server
import com.agustin.tarati.shared.generated.resources.online_lobby
import com.agustin.tarati.shared.generated.resources.lobby_online_users_section
import com.agustin.tarati.shared.generated.resources.rated
import com.agustin.tarati.shared.generated.resources.rated_info_card
import com.agustin.tarati.shared.generated.resources.rating
import com.agustin.tarati.shared.generated.resources.result
import com.agustin.tarati.shared.generated.resources.retry
import com.agustin.tarati.shared.generated.resources.search_no_longer_available
import com.agustin.tarati.shared.generated.resources.sort
import com.agustin.tarati.shared.generated.resources.tournament_sort_most_players
import com.agustin.tarati.shared.generated.resources.sort_newest
import com.agustin.tarati.shared.generated.resources.lobby_sort_oldest
import com.agustin.tarati.shared.generated.resources.lobby_sort_rating
import com.agustin.tarati.shared.generated.resources.lobby_status_in_lobby
import com.agustin.tarati.shared.generated.resources.lobby_status_playing
import com.agustin.tarati.shared.generated.resources.lobby_no_live_games
import com.agustin.tarati.shared.generated.resources.time_control
import com.agustin.tarati.shared.generated.resources.tournament
import com.agustin.tarati.shared.generated.resources.tournament_filter_all
import com.agustin.tarati.shared.generated.resources.tournament_format
import com.agustin.tarati.shared.generated.resources.tournament_players_of
import com.agustin.tarati.shared.generated.resources.tournament_recent_only
import com.agustin.tarati.shared.generated.resources.tournament_registering_section
import com.agustin.tarati.shared.generated.resources.tournament_status_active
import com.agustin.tarati.shared.generated.resources.tournament_status_cancelled
import com.agustin.tarati.shared.generated.resources.tournament_status_finished
import com.agustin.tarati.shared.generated.resources.tournament_status_registering
import com.agustin.tarati.shared.generated.resources.tournament_type_round_robin
import com.agustin.tarati.shared.generated.resources.tournament_type_swiss
import com.agustin.tarati.shared.generated.resources.tournaments
import com.agustin.tarati.shared.generated.resources.tournaments_finished_section
import com.agustin.tarati.shared.generated.resources.turn
import com.agustin.tarati.shared.generated.resources.user_name
import com.agustin.tarati.shared.generated.resources.validation_max_gte_min
import com.agustin.tarati.shared.generated.resources.validation_max_players_count
import com.agustin.tarati.shared.generated.resources.validation_min_players_count
import com.agustin.tarati.shared.generated.resources.validation_players_number
import com.agustin.tarati.shared.generated.resources.lobby_waiting_time
import com.agustin.tarati.shared.generated.resources.spectator_unavailable
import com.agustin.tarati.shared.generated.resources.watch_game
import com.agustin.tarati.shared.generated.resources.win
import com.agustin.tarati.shared.generated.resources.you
import com.agustin.tarati.ui.components.TooltipIconButton
import com.agustin.tarati.ui.components.carditem.GameCardItem
import com.agustin.tarati.ui.components.game.CobColorIndicator
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.layout.CompanionPanelHeader
import com.agustin.tarati.ui.layout.DisplayMode
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

/**
 * Pantalla de lobby online.
 *
 * ## Tabs
 *
 * ### En Vivo
 * Lista intercalada de partidas en curso y búsquedas abiertas, refrescada cada 5 s.
 * Filtros (chips): "En Vivo" / "Buscando". Ordenamiento: Más recientes / Más antiguos / Rating.
 * Botón 🔍 en la TopBar abre el [NewSearchSheet] para crear una búsqueda propia.
 *
 * ### Mis Partidas
 * Historial paginado con filtros por time control, resultado y tipo.
 *
 * @param onBack          Navega hacia atrás. También usado para ir al GameScreen cuando la partida se forma.
 * @param viewModel       ViewModel inyectado via Koin.
 * @param connectionViewModel    Singleton de conexión inyectado via Koin.
 * @param onlineGameViewModel    Singleton de juego online inyectado via Koin.
 * @param authViewModel          ViewModel de autenticación inyectado via Koin.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineLobbyScreen(
    onBack: () -> Unit,
    displayMode: DisplayMode = DisplayMode.FullScreen,
    /** Abre el modal de login/registro. */
    onShowLogin: () -> Unit = {},
    /**
     * Llamado cuando se forma una partida propia. Por defecto usa [onBack].
     * En [DisplayMode.CompanionPanel] se puede pasar un no-op para que el panel
     * permanezca abierto — el tablero ya muestra la partida en el panel primario.
     */
    onMatchFound: (() -> Unit)? = null,
    /** Callback al tocar "Ver" en una partida en vivo. Null = feature no disponible en este contexto. */
    onSpectateGame: ((gameId: String) -> Unit)? = null,
    onLeaderboard: (() -> Unit)? = null,
    /** Callback al tocar un perfil de usuario en línea. Null = sin navegación al perfil. */
    onNavigateToProfile: ((userId: String) -> Unit)? = null,
    /** Callback al tocar una partida en el historial. Null = sin navegación al detalle. */
    onNavigateToGameDetails: ((gameId: String) -> Unit)? = null,
    /** Callback al tocar un torneo en el tab Torneos. Null = sin navegación al detalle. */
    onNavigateToTournament: ((tournamentId: String) -> Unit)? = null,
    /** Tab inicial a mostrar al entrar. Útil en CompanionPanel para restaurar el tab activo. */
    initialTab: Int = 0,
    viewModel: IOnlineLobbyViewModel = koinViewModel<OnlineLobbyViewModel>(),
    connectionViewModel: IConnectionViewModel = koinInject(),
    onlineGameViewModel: IOnlineGameViewModel = koinInject(),
    authViewModel: IAuthViewModel = koinInject(),
    settings: SettingsRepository = koinInject(),
) {
    val authState by authViewModel.authState.collectAsState()
    val connectionState by connectionViewModel.connectionState.collectAsState()
    val matchmakingState by onlineGameViewModel.matchmakingState.collectAsState()
    val currentGame by onlineGameViewModel.currentGame.collectAsState()
    val hasActiveGame = currentGame?.status == OnlineGameStatus.InProgress
    val scope = rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }
    var showMatchmakingSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    val spectatorUnavailableMsg = localizedString(Res.string.spectator_unavailable)
    // True mientras el usuario inició una búsqueda desde este lobby (nueva o uniéndose a otra).
    // Evita que el LaunchedEffect de MatchFound dispare si el estado viene de una partida anterior.
    var searchStartedInLobby by remember { mutableStateOf(false) }

    // Rastrea si en esta sesión del lobby llegamos a estar Online alguna vez.
    // Cuando volvemos a Offline habiendo estado Online → el usuario hizo logout o la sesión expiró:
    // cerrar el panel en lugar de mostrar el mensaje "no conectado".
    var hasBeenOnline by remember { mutableStateOf(connectionState is ConnectionState.Online) }
    LaunchedEffect(connectionState) {
        if (connectionState is ConnectionState.Online) hasBeenOnline = true
        if (connectionState is ConnectionState.Offline && hasBeenOnline) onBack()
    }

    val tabScrollState = rememberScrollState()

    // Inicializa en true si no hay sesión activa — evita flashear el mensaje "no conectado"
    // mientras el auto-connect está en progreso en el primer frame.
    var isAutoConnecting by remember {
        mutableStateOf(authState !is com.agustin.tarati.features.online.auth.AuthState.Authenticated)
    }

    // Auto-conexión como invitado al primer acceso al Lobby.
    LaunchedEffect(Unit) {
        if (authViewModel.authState.value !is com.agustin.tarati.features.online.auth.AuthState.Authenticated) {
            val settingsName = settings.userName.first().trim()
                .takeIf { n -> n.length in 3..20 && n.matches(Regex("[A-Za-z0-9_]+")) }
            val result = authViewModel.loginAsGuest(settingsName)
            if (result.isFailure && settingsName != null) {
                // Nombre tomado o inválido — reintentar con nombre aleatorio
                authViewModel.loginAsGuest(null)
            }
        }
        val token = authViewModel.accessToken
        if (token != null && !connectionViewModel.isConnected && !connectionViewModel.isConnecting) {
            connectionViewModel.connectToServer(devServerUrl, token)
        }
        isAutoConnecting = false
    }

    val connectToServerFirstMsg = localizedString(Res.string.connect_to_server_first)
    val couldNotConnectMsg = localizedString(Res.string.could_not_connect)
    val searchNoLongerAvailableMsg = localizedString(Res.string.search_no_longer_available)

    /**
     * Garantiza conexión WS activa antes de intentar matchmaking.
     * Espera hasta 5 s si hay una conexión en progreso.
     */
    val ensureConnected: suspend () -> Result<Unit> = {
        when {
            connectionViewModel.isConnected -> Result.success(Unit)
            connectionViewModel.isConnecting -> {
                try {
                    withTimeout(5_000L.milliseconds) {
                        connectionViewModel.connectionState
                            .first {
                                it is ConnectionState.Online ||
                                        it is ConnectionState.Error ||
                                        it is ConnectionState.Offline
                            }
                    }
                    if (connectionViewModel.isConnected) Result.success(Unit)
                    else Result.failure(Exception(couldNotConnectMsg))
                } catch (e: Exception) {
                    Result.failure(Exception(couldNotConnectMsg))
                }
            }

            else -> {
                if (authViewModel.isTokenExpiringSoon()) authViewModel.refreshToken()
                val token = authViewModel.accessToken ?: authViewModel.getStoredToken()
                if (token == null) Result.failure(Exception(connectToServerFirstMsg))
                else connectionViewModel.connectToServer(devServerUrl, token).map { }
            }
        }
    }

    /**
     * Navegar al GameScreen cuando la partida está formada.
     * Unifica los dos caminos: nueva búsqueda propia y unirse a búsqueda ajena.
     */
    LaunchedEffect(matchmakingState) {
        if (matchmakingState is MatchmakingState.MatchFound && searchStartedInLobby) {
            searchStartedInLobby = false
            (onMatchFound ?: onBack).invoke()
        }
    }

    // Volver atrás si el lobby falla al cargar por primera vez (sin datos previos).
    // Un error con lista vacía indica un problema de conexión o configuración que
    // impide usar el lobby — no tiene sentido mostrar una pantalla vacía con error.
    val liveGamesError by viewModel.liveGames.collectAsState()
    LaunchedEffect(liveGamesError.error) {
        if (liveGamesError.error != null && liveGamesError.games.isEmpty()) {
            onBack()
        }
    }

    /**
     * Acepta directamente la búsqueda abierta de [targetUserId] (lógica estilo Lichess seek board).
     *
     * En lugar de entrar al queue general con el mismo TC y esperar al matchmaking worker,
     * envía [ClientMessage.JoinOpenSearch] para que el servidor cree la partida de inmediato
     * con ese jugador específico. Ambos reciben [ServerMessage.MatchFound] directamente.
     *
     * Si la búsqueda ya no existe (el jugador se fue o fue emparejado por el worker),
     * el servidor devuelve Error("search_not_found") y se resetea la búsqueda.
     */
    val handleJoinExistingSearch: (String, String, Boolean) -> Unit = { targetUserId, tc, rated ->
        scope.launch {
            val connResult = ensureConnected()
            if (connResult.isFailure) {
                snackbarHostState.showSnackbar(
                    message = connResult.exceptionOrNull()?.message ?: couldNotConnectMsg,
                    duration = SnackbarDuration.Long,
                )
                return@launch
            }
            // Marcar ANTES del suspend: MatchFound puede llegar durante el RTT del servidor.
            searchStartedInLobby = true
            val joinResult = onlineGameViewModel.joinOpenSearch(targetUserId, tc, rated)
            if (joinResult.isFailure) {
                searchStartedInLobby = false
                snackbarHostState.showSnackbar(
                    message = searchNoLongerAvailableMsg,
                    duration = SnackbarDuration.Short,
                )
                viewModel.refreshOpenSearches()
            }
        }
    }

    val isGuest = authViewModel.currentUser?.isGuest == true
    val isAuthenticated = authState is com.agustin.tarati.features.online.auth.AuthState.Authenticated

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(localizedString(Res.string.auth_logout)) },
            text = { Text(localizedString(Res.string.auth_logout_confirm)) },
            confirmButton = {
                Button(onClick = {
                    showLogoutConfirm = false
                    scope.launch {
                        authViewModel.logout()
                        connectionViewModel.disconnect()
                    }
                }) { Text(localizedString(Res.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(localizedString(Res.string.cancel))
                }
            },
        )
    }

    TaratiBackground {
        // Acciones compartidas entre TopBar (FullScreen) y CompanionPanelHeader (CompanionPanel).
        val topBarActions: @Composable RowScope.() -> Unit = {
            if (onLeaderboard != null) {
                TooltipIconButton(
                    tooltip = localizedString(Res.string.profile_leaderboard),
                    onClick = onLeaderboard,
                ) {
                    Icon(
                        imageVector = TaratiIcons.Leaderboard,
                        contentDescription = localizedString(Res.string.profile_leaderboard),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            // Botón Login / Logout
            val loginLogoutLabel = localizedString(
                if (isAuthenticated && !isGuest) Res.string.auth_logout else Res.string.auth_sign_in
            )
            TooltipIconButton(
                tooltip = loginLogoutLabel,
                onClick = {
                    when {
                        !isAuthenticated || isGuest -> onShowLogin()
                        else -> showLogoutConfirm = true
                    }
                },
            ) {
                Icon(
                    imageVector = if (isAuthenticated && !isGuest) TaratiIcons.Logout else TaratiIcons.AccountCircle,
                    contentDescription = loginLogoutLabel,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            // Botón 🔍 — visible en el tab "En Vivo" salvo partida online en curso.
            if (selectedTab == 1 && !hasActiveGame) {
                TooltipIconButton(
                    tooltip = localizedString(Res.string.lobby_new_search),
                    onClick = { showMatchmakingSheet = true },
                ) {
                    Icon(
                        imageVector = TaratiIcons.Search,
                        contentDescription = localizedString(Res.string.lobby_new_search),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                when (displayMode) {
                    DisplayMode.FullScreen -> TaratiTopBar(
                        title = localizedString(Res.string.online_lobby),
                        navigationType = TopBarNavigationType.Back,
                        onNavigationClick = onBack,
                        actions = topBarActions,
                    )

                    DisplayMode.CompanionPanel -> CompanionPanelHeader(
                        title = localizedString(Res.string.online_lobby),
                        onClose = onBack,
                        actions = topBarActions,
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                // Mostrar loader durante el auto-connect inicial
                if (isAutoConnecting) {
                    CenteredLoader()
                    return@Scaffold
                }

                when (val state = connectionState) {
                    is ConnectionState.Offline -> {
                        // Si ya estuvimos Online, el LaunchedEffect llama onBack() — no mostrar nada.
                        if (!hasBeenOnline) {
                            CenteredMessage(text = localizedString(Res.string.lobby_not_connected_to_server))
                        }
                        return@Scaffold
                    }

                    is ConnectionState.Connecting -> {
                        CenteredLoader()
                        return@Scaffold
                    }

                    is ConnectionState.Error -> {
                        CenteredMessage(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                        return@Scaffold
                    }

                    is ConnectionState.Reconnecting -> {
                        CenteredLoader()
                        return@Scaffold
                    }

                    is ConnectionState.Online -> Unit
                }

                // Banner de sesión invitado
                if (isGuest) {
                    GuestSessionBanner(onSignIn = onShowLogin)
                }

                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    scrollState = tabScrollState,
                    edgePadding = 0.dp,
                    modifier = Modifier.pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            tabScrollState.dispatchRawDelta(-dragAmount.x)
                        }
                    },
                ) {
                    // 0 — Conectados
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(localizedString(Res.string.lobby_connected_tab)) },
                        icon = {
                            Icon(
                                TaratiIcons.Group,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    // 1 — En Vivo
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { LocalizedText(Res.string.lobby_in_live) },
                        icon = {
                            Icon(
                                TaratiIcons.Public,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    // 2 — Torneos
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(localizedString(Res.string.tournaments)) },
                        icon = {
                            Icon(
                                TaratiIcons.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    // 3 — Mis Partidas
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { LocalizedText(Res.string.lobby_my_games) },
                        icon = {
                            Icon(
                                TaratiIcons.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    // 4 — Seguidos
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = { LocalizedText(Res.string.social_feed) },
                        icon = {
                            Icon(
                                TaratiIcons.Group,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }

                when (selectedTab) {
                    0 -> ConnectedUsersTab(
                        viewModel = viewModel,
                        currentUserId = authViewModel.currentUser?.userId,
                        isCurrentUserGuest = isGuest,
                        onlineGameViewModel = onlineGameViewModel,
                        onNavigateToProfile = onNavigateToProfile,
                    )

                    1 -> LobbyTab(
                        viewModel = viewModel,
                        onJoinSearch = { userId, tc, rated -> handleJoinExistingSearch(userId, tc, rated) },
                        matchmakingState = matchmakingState,
                        currentUser = authViewModel.currentUser,
                        onCancelMatchmaking = {
                            scope.launch { onlineGameViewModel.cancelMatchmaking() }
                            searchStartedInLobby = false
                        },
                        onSpectateGame = if (onSpectateGame != null) { gameId ->
                            scope.launch {
                                val success = onlineGameViewModel.spectateGame(gameId)
                                if (success) {
                                    onSpectateGame(gameId)
                                } else {
                                    snackbarHostState.showSnackbar(spectatorUnavailableMsg)
                                }
                            }
                        } else null,
                    )

                    2 -> TournamentsTab(onNavigateToTournament = onNavigateToTournament)
                    3 -> GameHistoryTab(viewModel = viewModel, onNavigateToGameDetails = onNavigateToGameDetails)
                    4 -> FeedTab(viewModel = viewModel, onNavigateToGameDetails = onNavigateToGameDetails)
                }
            }
        }
    }

    // Modal de creación de búsqueda — inicia matchmaking en el lobby y muestra OwnSearchCard
    if (showMatchmakingSheet) {
        NewSearchSheet(
            onStartSearch = { tc, rated, spectatingAllowed ->
                showMatchmakingSheet = false
                scope.launch {
                    // Cancelar búsqueda previa si la hay, antes de iniciar la nueva.
                    if (matchmakingState is MatchmakingState.Searching) {
                        onlineGameViewModel.cancelMatchmaking()
                        searchStartedInLobby = false
                    }
                    val connResult = ensureConnected()
                    if (connResult.isFailure) {
                        snackbarHostState.showSnackbar(
                            message = connResult.exceptionOrNull()?.message ?: couldNotConnectMsg,
                            duration = SnackbarDuration.Long,
                        )
                        return@launch
                    }
                    searchStartedInLobby = true
                    val result = onlineGameViewModel.startMatchmaking(tc, rated, spectatingAllowed)
                    if (result.isFailure) {
                        searchStartedInLobby = false
                        snackbarHostState.showSnackbar(
                            message = couldNotConnectMsg,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            },
            onDismiss = { showMatchmakingSheet = false },
        )
    }
}

// ── Tab: Lobby (En Vivo + Búsquedas) ──────────────────────────────────────────

private sealed class LobbyItem {
    data class Game(val dto: LiveGameDto) : LobbyItem()
    data class Search(val dto: OpenSearchDto) : LobbyItem()

    /** Búsqueda propia del usuario en curso — se muestra sin botón Unirse y con botón Cancelar. */
    data class OwnSearch(val ticket: MatchmakingTicket) : LobbyItem()
}

@Composable
private fun LobbyTab(
    viewModel: IOnlineLobbyViewModel,
    onJoinSearch: (targetUserId: String, timeControl: String, rated: Boolean) -> Unit,
    matchmakingState: MatchmakingState,
    currentUser: UserInfo?,
    onCancelMatchmaking: () -> Unit,
    onSpectateGame: ((String) -> Unit)? = null,
) {
    val gamesState by viewModel.liveGames.collectAsState()
    val searchesState by viewModel.openSearches.collectAsState()
    val filters by viewModel.lobbyFilters.collectAsState()

    DisposableEffect(Unit) {
        viewModel.startLivePolling()
        onDispose { viewModel.stopLivePolling() }
    }

    val ownTicket = (matchmakingState as? MatchmakingState.Searching)?.ticket

    // Lista intercalada ordenada según filtros
    val combinedItems = remember(gamesState.games, searchesState.searches, filters, ownTicket) {
        buildList {
            if (filters.showLiveGames) gamesState.games.forEach { add(LobbyItem.Game(it)) }
            if (filters.showOpenSearches) {
                searchesState.searches.forEach { add(LobbyItem.Search(it)) }
                if (ownTicket != null) add(LobbyItem.OwnSearch(ownTicket))
            }
        }.sortedWith(
            when (filters.sort) {
                LobbySort.NEWEST -> compareByDescending {
                    when (it) {
                        is LobbyItem.Game -> it.dto.startedAtMs
                        is LobbyItem.Search -> it.dto.waitingSinceMs
                        is LobbyItem.OwnSearch -> it.ticket.joinedAt
                    }
                }

                LobbySort.OLDEST -> compareBy {
                    when (it) {
                        is LobbyItem.Game -> it.dto.startedAtMs
                        is LobbyItem.Search -> it.dto.waitingSinceMs
                        is LobbyItem.OwnSearch -> it.ticket.joinedAt
                    }
                }

                LobbySort.RATING_DESC -> compareByDescending {
                    when (it) {
                        is LobbyItem.Game -> maxOf(it.dto.whiteRating, it.dto.blackRating)
                        is LobbyItem.Search -> it.dto.playerRating
                        is LobbyItem.OwnSearch -> currentUser?.rating ?: 0
                    }
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LobbyFilterBar(filters = filters, viewModel = viewModel)
        Box(modifier = Modifier.weight(1f)) {
            when {
                gamesState.isLoading && combinedItems.isEmpty() -> CenteredLoader()
                combinedItems.isEmpty() -> CenteredMessage(
                    text = localizedString(Res.string.lobby_no_live_games),
                )

                else -> LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(combinedItems, key = { item ->
                        when (item) {
                            is LobbyItem.Game -> "game_${item.dto.gameId}"
                            // searchId ya es compuesto (userId:tc:rated), pero agregamos
                            // el prefijo para evitar colisión si gameId y searchId coincidieran.
                            is LobbyItem.Search -> "search_${item.dto.searchId}"
                            is LobbyItem.OwnSearch -> "own_search"
                        }
                    }) { item ->
                        when (item) {
                            is LobbyItem.Game -> LiveGameCard(
                                game = item.dto,
                                onSpectate = if (item.dto.spectatingAllowed && onSpectateGame != null) {
                                    { onSpectateGame(item.dto.gameId) }
                                } else null,
                            )

                            is LobbyItem.Search -> OpenSearchCard(
                                search = item.dto,
                                onJoin = {
                                    onJoinSearch(
                                        item.dto.userId,
                                        item.dto.timeControl.type.key,
                                        item.dto.rated,
                                    )
                                },
                            )

                            is LobbyItem.OwnSearch -> OwnSearchCard(
                                ticket = item.ticket,
                                currentUser = currentUser,
                                onCancel = onCancelMatchmaking,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Filter / sort bar ──────────────────────────────────────────────────────────

@Composable
private fun LobbyFilterBar(filters: LobbyFilters, viewModel: IOnlineLobbyViewModel) {
    var showSortMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            selected = filters.showLiveGames,
            onClick = { viewModel.setShowLiveGames(!filters.showLiveGames) },
            label = { LocalizedText(Res.string.lobby_filter_live_games) },
            leadingIcon = { Icon(TaratiIcons.Timer, null, Modifier.size(14.dp)) },
        )
        FilterChip(
            selected = filters.showOpenSearches,
            onClick = { viewModel.setShowOpenSearches(!filters.showOpenSearches) },
            label = { LocalizedText(Res.string.lobby_filter_open_searches) },
            leadingIcon = { Icon(TaratiIcons.Search, null, Modifier.size(14.dp)) },
        )
        Spacer(Modifier.weight(1f))
        Box {
            IconButton(onClick = { showSortMenu = true }, modifier = Modifier.size(32.dp)) {
                Icon(
                    TaratiIcons.Sort,
                    contentDescription = localizedString(Res.string.sort),
                    modifier = Modifier.size(18.dp),
                    tint = if (filters.sort != LobbySort.NEWEST)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                listOf(
                    LobbySort.NEWEST to Res.string.sort_newest,
                    LobbySort.OLDEST to Res.string.lobby_sort_oldest,
                    LobbySort.RATING_DESC to Res.string.lobby_sort_rating,
                ).forEach { (sort, stringRes) ->
                    DropdownMenuItem(
                        text = { LocalizedText(stringRes) },
                        onClick = { viewModel.setLobbySort(sort); showSortMenu = false },
                        leadingIcon = if (filters.sort == sort) ({
                            Icon(TaratiIcons.Check, null, Modifier.size(16.dp))
                        }) else null,
                    )
                }
            }
        }
    }
}

// ── Cards ──────────────────────────────────────────────────────────────────────

@Composable
private fun LiveGameCard(game: LiveGameDto, onSpectate: (() -> Unit)? = null) {
    val whiteTimeFmt = formatMs(game.whiteTimeMs)
    val blackTimeFmt = formatMs(game.blackTimeMs)
    val activeSide = cobColorByDescription(game.currentTurn)

    // Parsear la notación de posición para renderizar una miniatura del tablero.
    // Fallback al ícono Timer si la notación está vacía o es inválida.
    val boardState = remember(game.positionNotation) {
        if (game.positionNotation.isNotEmpty())
            runCatching { parseBoardNotation(game.positionNotation) }.getOrNull()
        else null
    }

    GameCardItem(
        title = "${game.whiteUsername} (${game.whiteRating}) vs ${game.blackUsername} (${game.blackRating})",
        subtitle = "${game.timeControl.toDisplayString()} · ${
            if (game.rated) localizedString(Res.string.rated_info_card)
            else localizedString(Res.string.casual_info_card)
        }",
        leadingContent = boardState?.let { state ->
            { StaticBoardRenderer(modifier = Modifier.fillMaxSize(), gameState = state) }
        },
        leadingIcon = if (boardState == null) TaratiIcons.Timer else null,
        badge = localizedString(Res.string.lobby_in_live),
        badgeColor = MaterialTheme.colorScheme.error,
        badgeTrailingContent = if (onSpectate != null) {
            {
                TextButton(
                    onClick = onSpectate,
                    contentPadding = PaddingValues(
                        horizontal = 6.dp, vertical = 0.dp,
                    ),
                ) {
                    Icon(
                        TaratiIcons.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(2.dp))
                    LocalizedText(
                        Res.string.watch_game,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        } else null,
        rows = listOf(
            localizedString(Res.string.move) to "${game.moveCount}",
        ),
        customRows = {
            LiveGameTurnRow(
                activeSide = activeSide,
                whiteTimeFmt = whiteTimeFmt,
                blackTimeFmt = blackTimeFmt,
            )
            if (game.tournamentId != null) {
                TournamentContextRow(
                    name = game.tournamentName ?: localizedString(Res.string.tournament),
                    round = game.tournamentRound,
                    totalRounds = game.tournamentTotalRounds,
                )
            }
        },
    )
}

@Composable
private fun TournamentContextRow(name: String, round: Int?, totalRounds: Int?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            TaratiIcons.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = buildString {
                append(name)
                if (round != null && totalRounds != null) {
                    append(" · R$round/$totalRounds")
                }
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LiveGameTurnRow(
    activeSide: CobColor?,
    whiteTimeFmt: String,
    blackTimeFmt: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${localizedString(Res.string.turn)}:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Blancas
            CobColorIndicator(
                color = CobColor.WHITE,
                size = if (activeSide == CobColor.WHITE) 14.dp else 10.dp,
            )
            Text(
                text = whiteTimeFmt,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (activeSide == CobColor.WHITE) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(4.dp))
            // Negras
            CobColorIndicator(
                color = CobColor.BLACK,
                size = if (activeSide == CobColor.BLACK) 14.dp else 10.dp,
            )
            Text(
                text = blackTimeFmt,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (activeSide == CobColor.BLACK) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Card de búsqueda abierta.
 *
 * Visualmente distinta de [LiveGameCard]: usa [MaterialTheme.colorScheme.tertiaryContainer]
 * como fondo (vs. [surfaceVariant] de las partidas en vivo) y tiene un botón [Unirse].
 *
 * @param search   DTO de la búsqueda.
 * @param onJoin   Callback al tocar [Unirse]. Null = botón deshabilitado (propia búsqueda).
 */
@Composable
private fun OpenSearchCard(search: OpenSearchDto, onJoin: (() -> Unit)?) {
    val waitingSecs = (Clock.System.now().toEpochMilliseconds() - search.waitingSinceMs) / 1000
    val waitingFmt = when {
        waitingSecs < 60 -> "${waitingSecs}s"
        else -> "${waitingSecs / 60}m ${waitingSecs % 60}s"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = TaratiIcons.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${search.playerUsername} (${search.playerRating})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Text(
                    text = "${search.timeControl.toDisplayString()} · ${
                        if (search.rated) localizedString(Res.string.rated_info_card)
                        else localizedString(Res.string.casual_info_card)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                )
                Text(
                    text = localizedString(Res.string.lobby_waiting_time).replace($$"%1$s", waitingFmt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                )
            }
            if (onJoin != null) {
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onJoin,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    LocalizedText(
                        Res.string.join,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

/**
 * Card de la búsqueda propia del usuario.
 *
 * Visualmente distinta de [OpenSearchCard]: usa [MaterialTheme.colorScheme.primaryContainer]
 * como fondo y tiene un botón [Cancelar] en lugar de [Unirse].
 *
 * @param ticket     Ticket de matchmaking activo.
 * @param currentUser Información del usuario autenticado (nombre y rating).
 * @param onCancel   Callback al tocar [Cancelar].
 */
@Composable
private fun OwnSearchCard(
    ticket: MatchmakingTicket,
    currentUser: UserInfo?,
    onCancel: () -> Unit,
) {
    val waitingSecs = (Clock.System.now().toEpochMilliseconds() - ticket.joinedAt) / 1000
    val waitingFmt = when {
        waitingSecs < 60 -> "${waitingSecs}s"
        else -> "${waitingSecs / 60}m ${waitingSecs % 60}s"
    }

    val tcDisplay = remember(ticket.timeControl) {
        runCatching {
            val tc = TimeControl.fromKey(ticket.timeControl)
            val (initial, increment) = tc.timeControl
            GameTimeControl(type = tc, initialTime = initial, increment = increment).toDisplayString()
        }.getOrElse { ticket.timeControl }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${currentUser?.displayName ?: "–"} (${currentUser?.rating ?: "–"})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "$tcDisplay · ${
                        if (ticket.rated) localizedString(Res.string.rated_info_card)
                        else localizedString(Res.string.casual_info_card)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
                Text(
                    text = localizedString(Res.string.lobby_waiting_time).replace($$"%1$s", waitingFmt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                )
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onCancel,
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
            ) {
                LocalizedText(
                    Res.string.cancel,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

// ── Modal de nueva búsqueda ────────────────────────────────────────────────────

/**
 * Dialog simplificado para crear una nueva búsqueda desde el lobby.
 * Persiste las preferencias de time control y rated/casual en [SettingsRepository],
 * compartiendo el mismo almacenamiento que el modal de GameScreen.
 */
@Composable
private fun NewSearchSheet(
    onStartSearch: (timeControl: String, rated: Boolean, spectatingAllowed: Boolean) -> Unit,
    onDismiss: () -> Unit,
    settings: SettingsRepository = koinInject(),
    authViewModel: IAuthViewModel = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val timeControls = TimeControl.list()
    val isGuest = authViewModel.currentUser?.isGuest == true

    val savedTc by settings.onlineTimeControl.collectAsState(TimeControl.BLITZ.key)
    val savedRated by settings.onlineRated.collectAsState(true)
    val savedSpectatingAllowed by settings.onlineSpectatingAllowed.collectAsState(true)

    var selectedTc by remember(savedTc) { mutableStateOf(savedTc) }
    // Invitados solo pueden jugar partidas no-puntuadas
    var isRated by remember(savedRated, isGuest) { mutableStateOf(if (isGuest) false else savedRated) }
    // Los invitados siempre permiten espectadores — el toggle queda desactivado.
    var spectatingAllowed by remember(savedSpectatingAllowed, isGuest) {
        mutableStateOf(if (isGuest) true else savedSpectatingAllowed)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { LocalizedText(Res.string.lobby_new_search, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Time control chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(timeControls) { tc ->
                        FilterChip(
                            selected = selectedTc == tc,
                            onClick = {
                                selectedTc = tc
                                scope.launch { settings.setOnlineTimeControl(tc) }
                            },
                            label = { Text(tc.replaceFirstChar { it.titlecase() }) },
                        )
                    }
                }
                // Rated / casual row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LocalizedText(
                        Res.string.rated,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Switch(
                        checked = isRated,
                        onCheckedChange = {
                            isRated = it
                            scope.launch { settings.setOnlineRated(it) }
                        },
                        enabled = !isGuest,
                    )
                }
                // Allow spectators row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LocalizedText(
                        Res.string.allow_spectators,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Switch(
                        checked = spectatingAllowed,
                        onCheckedChange = {
                            spectatingAllowed = it
                            scope.launch { settings.setOnlineSpectatingAllowed(it) }
                        },
                        enabled = !isGuest,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onStartSearch(selectedTc, isRated, spectatingAllowed) }) {
                LocalizedText(Res.string.lobby_new_search)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                LocalizedText(Res.string.cancel)
            }
        },
    )
}

@Composable
private fun GameHistoryTab(
    viewModel: IOnlineLobbyViewModel,
    onNavigateToGameDetails: ((gameId: String) -> Unit)? = null,
) {
    val state by viewModel.history.collectAsState()
    val listState = rememberLazyListState()

    // Cargar al entrar en el tab (solo si no hay datos ya).
    LaunchedEffect(Unit) {
        if (state.games.isEmpty() && !state.isLoading) {
            viewModel.loadHistory()
        }
    }

    // Paginación automática: cuando faltan 3 ítems para el final, cargar más.
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMoreHistory()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Filtros ────────────────────────────────────────────────────────────
        HistoryFilterRow(state = state, viewModel = viewModel)

        // ── Contenido ─────────────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> CenteredLoader()

                state.error != null -> CenteredMessage(
                    text = localizedString(Res.string.error)
                        .replace($$"%1$s", state.error.orEmpty()),
                    color = MaterialTheme.colorScheme.error,
                )

                state.games.isEmpty() -> CenteredMessage(
                    text = localizedString(Res.string.no_games_found),
                )

                else -> LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    itemsIndexed(state.games, key = { _, g -> g.gameId }) { _, game ->
                        HistoryGameCard(
                            game = game,
                            onClick = onNavigateToGameDetails?.let { cb -> { cb(game.gameId) } },
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryFilterRow(
    state: GameHistoryUiState,
    viewModel: IOnlineLobbyViewModel,
) {
    val filters = state.filters

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Time control chips
        item {
            TimeControl.list().forEach { tc ->
                FilterChip(
                    selected = filters.timeControl == tc,
                    onClick = { viewModel.setTimeControlFilter(if (filters.timeControl == tc) null else tc) },
                    label = { Text(tc.replaceFirstChar { it.titlecase() }) },
                )
            }
        }

        // Result chips
        item {
            listOf(
                "win" to localizedString(Res.string.win),
                "loss" to localizedString(Res.string.loss),
                "draw" to localizedString(Res.string.draw)
            ).forEach { (key, label) ->
                FilterChip(
                    selected = filters.result == key,
                    onClick = { viewModel.setResultFilter(if (filters.result == key) null else key) },
                    label = { Text(label) },
                )
            }
        }

        // Rated chip
        item {
            FilterChip(
                selected = filters.rated == true,
                onClick = { viewModel.setRatedFilter(if (filters.rated == true) null else true) },
                label = { LocalizedText(Res.string.rated) },
            )
        }
    }
}

@Composable
private fun HistoryGameCard(game: GameHistoryDto, onClick: (() -> Unit)? = null) {
    val myColor = cobColorByDescription(game.myColor) ?: CobColor.WHITE
    val (resultText, resultColor) = when (game.result) {
        "win" -> localizedString(Res.string.win) to Color(0xFF4CAF50)
        "loss" -> localizedString(Res.string.loss) to MaterialTheme.colorScheme.error
        else -> localizedString(Res.string.draw) to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val ratingChangeFmt = when {
        game.ratingChange > 0 -> "+${game.ratingChange}"
        else -> "${game.ratingChange}"
    }

    val ratingChangeColor = when {
        game.ratingChange > 0 -> Color(0xFF4CAF50)
        game.ratingChange < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val dateFmt = remember(game.endedAtMs) {
        // 1. Obtener el LocalDate en la zona horaria del sistema
        val localDate = Instant.fromEpochMilliseconds(game.endedAtMs)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        // 2. Aplicar un formato personalizado con el DSL
        val customFormat = LocalDate.Format {
            this@Format.day(padding = Padding.ZERO)
            char('/')
            monthNumber()
            char('/')
            year()
        }

        // 3. Obtener la fecha formateada como String
        localDate.format(customFormat)
    }

    GameCardItem(
        title = "vs ${game.opponentUsername} (${game.opponentRating})",
        subtitle = "${game.timeControl.toDisplayString()} · ${
            if (game.rated) localizedString(Res.string.rated_info_card)
            else localizedString(Res.string.casual_info_card)
        } · $dateFmt",
        leadingContent = { CobColorIndicator(myColor, size = 28.dp) },
        badge = "$resultText  $ratingChangeFmt",
        badgeColor = ratingChangeColor,
        rows = listOf(
            localizedString(Res.string.result) to resultText,
            localizedString(Res.string.moves) to "${game.moveCount}",
            localizedString(Res.string.rating) to "${game.ratingAfter} ($ratingChangeFmt)",
        ),
        onClick = onClick,
    )
}

// ── Tab: Feed de seguidos ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTab(
    viewModel: IOnlineLobbyViewModel,
    onNavigateToGameDetails: ((gameId: String) -> Unit)? = null,
) {
    val state by viewModel.feedState.collectAsState()
    val listState = rememberLazyListState()

    // ── Filtros locales ────────────────────────────────────────────────────────
    var resultFilter by remember { mutableStateOf<String?>(null) }
    var tcFilter by remember { mutableStateOf<TimeControl?>(null) }

    val displayGames = state.games
        .let { if (resultFilter != null) it.filter { g -> g.result == resultFilter } else it }
        .let { if (tcFilter != null) it.filter { g -> g.timeControl.type == tcFilter } else it }

    val filtersActive = resultFilter != null || tcFilter != null

    LaunchedEffect(Unit) {
        if (state.games.isEmpty() && !state.isLoading) viewModel.loadFeed()
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMoreFeed()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FeedFilterBar(
            resultFilter = resultFilter,
            onResultFilter = { resultFilter = it },
            tcFilter = tcFilter,
            onTcFilter = { tcFilter = it },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> CenteredLoader()

                state.error != null -> CenteredMessage(
                    text = localizedString(Res.string.error).replace($$"%1$s", state.error.orEmpty()),
                    color = MaterialTheme.colorScheme.error,
                )

                state.games.isEmpty() -> CenteredMessage(
                    text = localizedString(Res.string.social_no_feed_games),
                )

                displayGames.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CenteredMessage(text = localizedString(Res.string.no_tournaments_match_filters))
                    if (filtersActive) {
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            resultFilter = null
                            tcFilter = null
                        }) {
                            Text(localizedString(Res.string.clear_filters))
                        }
                    }
                }

                else -> LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    itemsIndexed(displayGames, key = { _, g -> g.gameId }) { _, game ->
                        FeedGameCard(
                            game = game,
                            onClick = onNavigateToGameDetails?.let { cb -> { cb(game.gameId) } },
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedFilterBar(
    resultFilter: String?,
    onResultFilter: (String?) -> Unit,
    tcFilter: TimeControl?,
    onTcFilter: (TimeControl?) -> Unit,
) {
    var showTcMenu by remember { mutableStateOf(false) }
    val tcLabel = when (tcFilter) {
        null -> localizedString(Res.string.time_control)
        else -> tcFilter.description
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp)) {
        // Fila 1: chips de resultado (ancho completo)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf<String?>(null, "win", "loss", "draw").forEach { result ->
                FilterChip(
                    selected = resultFilter == result,
                    onClick = {
                        onResultFilter(if (resultFilter == result && result != null) null else result)
                    },
                    label = {
                        Text(
                            when (result) {
                                null -> localizedString(Res.string.lobby_filter_all)
                                "win" -> localizedString(Res.string.win)
                                "loss" -> localizedString(Res.string.loss)
                                else -> localizedString(Res.string.draw)
                            },
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                )
            }
        }
        // Fila 2: control de tiempo alineado a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box {
                FilterChip(
                    selected = tcFilter != null,
                    onClick = { showTcMenu = true },
                    label = { Text(tcLabel, style = MaterialTheme.typography.labelSmall) },
                    leadingIcon = { Icon(TaratiIcons.Timer, null, Modifier.size(14.dp)) },
                    trailingIcon = { Icon(TaratiIcons.ArrowDropDown, null, Modifier.size(14.dp)) },
                )
                DropdownMenu(expanded = showTcMenu, onDismissRequest = { showTcMenu = false }) {
                    listOf<TimeControl?>(
                        null,
                        TimeControl.BULLET,
                        TimeControl.BLITZ,
                        TimeControl.RAPID,
                        TimeControl.CLASSICAL
                    )
                        .forEach { tc ->
                            DropdownMenuItem(
                                text = { Text(if (tc == null) localizedString(Res.string.lobby_filter_all) else tc.description) },
                                onClick = { onTcFilter(tc); showTcMenu = false },
                                leadingIcon = if (tcFilter == tc) ({
                                    Icon(TaratiIcons.Check, null, Modifier.size(16.dp))
                                }) else null,
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun FeedGameCard(game: GameHistoryDto, onClick: (() -> Unit)? = null) {
    val (resultText, resultColor) = when (game.result) {
        "win" -> localizedString(Res.string.win) to Color(0xFF4CAF50)
        "loss" -> localizedString(Res.string.loss) to MaterialTheme.colorScheme.error
        else -> localizedString(Res.string.draw) to MaterialTheme.colorScheme.onSurfaceVariant
    }
    val ratingChangeFmt = if (game.ratingChange > 0) "+${game.ratingChange}" else "${game.ratingChange}"
    val ratingChangeColor = when {
        game.ratingChange > 0 -> Color(0xFF4CAF50)
        game.ratingChange < 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val dateFmt = remember(game.endedAtMs) {
        val localDate = Instant.fromEpochMilliseconds(game.endedAtMs)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        localDate.format(LocalDate.Format {
            day(Padding.ZERO)
            char('/')
            monthNumber()
            char('/')
            year()
        })
    }
    val feedColor = cobColorByDescription(game.myColor) ?: CobColor.WHITE
    val playerLabel = game.playerUsername ?: "?"

    GameCardItem(
        title = localizedString(Res.string.social_feed_player_context).replace($$"%1$s", playerLabel) +
                " vs ${game.opponentUsername} (${game.opponentRating})",
        subtitle = "${game.timeControl.toDisplayString()} · ${
            if (game.rated) localizedString(Res.string.rated_info_card)
            else localizedString(Res.string.casual_info_card)
        } · $dateFmt",
        leadingContent = { CobColorIndicator(feedColor, size = 28.dp) },
        badge = "$resultText  $ratingChangeFmt",
        badgeColor = ratingChangeColor,
        rows = listOf(
            localizedString(Res.string.result) to resultText,
            localizedString(Res.string.moves) to "${game.moveCount}",
            localizedString(Res.string.rating) to "${game.ratingAfter} ($ratingChangeFmt)",
        ),
        onClick = onClick,
    )
}

// ── Helpers ────────────────────────────────────────────────────────────────────

@Composable
private fun CenteredLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenteredMessage(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, color = color, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatMs(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "$min:${sec.toString().padStart(2, '0')}"
}

// ── Tab: Torneos ───────────────────────────────────────────────────────────────

private enum class TournamentSort { NEWEST, MOST_PLAYERS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TournamentsTab(
    onNavigateToTournament: ((tournamentId: String) -> Unit)? = null,
    authViewModel: IAuthViewModel = koinInject(),
    viewModel: ITournamentViewModel = koinViewModel<TournamentViewModel>(),
) {
    val state by viewModel.listState.collectAsState()
    val scope = rememberCoroutineScope()
    val token = authViewModel.accessToken ?: authViewModel.getStoredToken()
    var showCreateDialog by remember { mutableStateOf(false) }

    // ── Filtros locales ────────────────────────────────────────────────────────
    var statusFilter by remember { mutableStateOf<TournamentStatus?>(null) }
    var recentOnly by remember { mutableStateOf(true) }
    var sortBy by remember { mutableStateOf(TournamentSort.NEWEST) }

    DisposableEffect(Unit) {
        viewModel.startTournamentPolling()
        onDispose { viewModel.stopTournamentPolling() }
    }

    // ── Aplicar filtros ────────────────────────────────────────────────────────
    val nowMs = Clock.System.now().toEpochMilliseconds()
    val cutoffMs = nowMs - 7L * 24 * 60 * 60 * 1000

    fun List<TournamentSummaryDto>.sorted() = when (sortBy) {
        TournamentSort.NEWEST -> sortedByDescending { it.createdAt.toEpochMilliseconds() }
        TournamentSort.MOST_PLAYERS -> sortedByDescending { it.participantCount }
    }

    val displayRegistering = state.registering
        .takeIf { statusFilter == null || statusFilter == TournamentStatus.REGISTERING }
        ?.sorted() ?: emptyList()

    val displayActive = state.active
        .takeIf { statusFilter == null || statusFilter == TournamentStatus.ACTIVE }
        ?.sorted() ?: emptyList()

    val displayFinished = state.finished
        .let { if (recentOnly) it.filter { t -> (t.finishedAt?.toEpochMilliseconds() ?: nowMs) > cutoffMs } else it }
        .takeIf { statusFilter == null || statusFilter == TournamentStatus.FINISHED }
        ?.sorted() ?: emptyList()

    val filtersActive = statusFilter != null || !recentOnly || sortBy != TournamentSort.NEWEST
    val isEmptyAfterFilter = displayRegistering.isEmpty() && displayActive.isEmpty() && displayFinished.isEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        state.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (token != null) viewModel.loadTournaments(token) }) {
                        Text(localizedString(Res.string.retry))
                    }
                }
            }

            state.isEmpty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        localizedString(Res.string.no_tournaments_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { showCreateDialog = true }) {
                        Text(localizedString(Res.string.create_tournament))
                    }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    TournamentFilterBar(
                        statusFilter = statusFilter,
                        onStatusFilter = { statusFilter = it },
                        recentOnly = recentOnly,
                        onRecentOnlyToggle = { recentOnly = !recentOnly },
                        sortBy = sortBy,
                        onSortChange = { sortBy = it },
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isEmptyAfterFilter) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    localizedString(Res.string.no_tournaments_match_filters),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                if (filtersActive) {
                                    Spacer(Modifier.height(12.dp))
                                    TextButton(onClick = {
                                        statusFilter = null
                                        recentOnly = true
                                        sortBy = TournamentSort.NEWEST
                                    }) {
                                        Text(localizedString(Res.string.clear_filters))
                                    }
                                }
                            }
                        } else {
                            LazyColumn(contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)) {
                                if (displayRegistering.isNotEmpty()) {
                                    item {
                                        Text(
                                            localizedString(Res.string.tournament_registering_section),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                        )
                                    }
                                    items(displayRegistering, key = { it.id }) { t ->
                                        TournamentCard(t, onClick = { onNavigateToTournament?.invoke(t.id) })
                                    }
                                }
                                if (displayActive.isNotEmpty()) {
                                    item {
                                        Text(
                                            localizedString(Res.string.tournament_status_active),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                                        )
                                    }
                                    items(displayActive, key = { it.id }) { t ->
                                        TournamentCard(t, onClick = { onNavigateToTournament?.invoke(t.id) })
                                    }
                                }
                                if (displayFinished.isNotEmpty()) {
                                    item {
                                        Text(
                                            localizedString(Res.string.tournaments_finished_section),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                                        )
                                    }
                                    items(displayFinished, key = { it.id }) { t ->
                                        TournamentCard(t, onClick = { onNavigateToTournament?.invoke(t.id) })
                                    }
                                }
                                item { Spacer(Modifier.height(80.dp)) }
                            }
                        }
                        // FAB siempre visible cuando hay datos
                        androidx.compose.material3.FloatingActionButton(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                        ) {
                            Icon(
                                TaratiIcons.EmojiEvents,
                                contentDescription = localizedString(Res.string.create_tournament)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTournamentDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { request ->
                if (token != null) scope.launch {
                    viewModel.createTournament(token, request)
                    showCreateDialog = false
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TournamentFilterBar(
    statusFilter: TournamentStatus?,
    onStatusFilter: (TournamentStatus?) -> Unit,
    recentOnly: Boolean,
    onRecentOnlyToggle: () -> Unit,
    sortBy: TournamentSort,
    onSortChange: (TournamentSort) -> Unit,
) {
    var showSortMenu by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp)) {
        // Fila 1: chips de estado (ancho completo)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf<TournamentStatus?>(
                null,
                TournamentStatus.REGISTERING,
                TournamentStatus.ACTIVE,
                TournamentStatus.FINISHED
            )
                .forEach { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = {
                            onStatusFilter(if (statusFilter == status && status != null) null else status)
                        },
                        label = {
                            Text(
                                when (status) {
                                    null -> localizedString(Res.string.tournament_filter_all)
                                    TournamentStatus.REGISTERING -> localizedString(Res.string.tournament_status_registering)
                                    TournamentStatus.ACTIVE -> localizedString(Res.string.tournament_status_active)
                                    TournamentStatus.FINISHED -> localizedString(Res.string.tournament_status_finished)
                                    TournamentStatus.CANCELLED -> localizedString(Res.string.tournament_status_cancelled)
                                },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                    )
                }
        }
        // Fila 2: Recientes + ordenamiento alineados a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = recentOnly,
                onClick = onRecentOnlyToggle,
                label = { LocalizedText(Res.string.tournament_recent_only) },
                leadingIcon = { Icon(TaratiIcons.Timer, null, Modifier.size(14.dp)) },
            )
            Box {
                IconButton(onClick = { showSortMenu = true }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        TaratiIcons.Sort,
                        contentDescription = localizedString(Res.string.sort),
                        modifier = Modifier.size(18.dp),
                        tint = if (sortBy != TournamentSort.NEWEST)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                    listOf(
                        TournamentSort.NEWEST to Res.string.sort_newest,
                        TournamentSort.MOST_PLAYERS to Res.string.tournament_sort_most_players,
                    ).forEach { (sort, stringRes) ->
                        DropdownMenuItem(
                            text = { LocalizedText(stringRes) },
                            onClick = { onSortChange(sort); showSortMenu = false },
                            leadingIcon = if (sortBy == sort) ({
                                Icon(TaratiIcons.Check, null, Modifier.size(16.dp))
                            }) else null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TournamentCard(
    tournament: TournamentSummaryDto,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    tournament.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                val statusColor = when (tournament.status) {
                    TournamentStatus.REGISTERING -> Color(0xFFFFC107)
                    TournamentStatus.ACTIVE -> Color(0xFF4CAF50)
                    TournamentStatus.FINISHED -> MaterialTheme.colorScheme.onSurfaceVariant
                    TournamentStatus.CANCELLED -> MaterialTheme.colorScheme.error
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(statusColor, CircleShape)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    when (tournament.type) {
                        TournamentType.ROUND_ROBIN -> localizedString(Res.string.tournament_type_round_robin)
                        TournamentType.SWISS -> localizedString(Res.string.tournament_type_swiss)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    tournament.timeControl.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    localizedString(Res.string.tournament_players_of)
                        .replace($$"%1$d", "${tournament.participantCount}")
                        .replace($$"%2$d", "${tournament.maxPlayers}"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CreateTournamentDialog(
    onDismiss: () -> Unit,
    onCreate: (CreateTournamentRequest) -> Unit,
    authViewModel: IAuthViewModel = koinInject(),
) {
    val isGuest = authViewModel.currentUser?.isGuest == true
    var name by remember { mutableStateOf("") }
    var selectedType by remember {
        mutableStateOf(TournamentType.ROUND_ROBIN)
    }
    var selectedTc by remember { mutableStateOf("blitz") }
    var isRated by remember { mutableStateOf(true) }
    var spectatingAllowed by remember { mutableStateOf(true) }
    var minPlayers by remember { mutableStateOf("4") }
    var maxPlayers by remember { mutableStateOf("16") }

    val minInt = minPlayers.toIntOrNull()
    val maxInt = maxPlayers.toIntOrNull()
    val playerError: String? = when {
        minInt == null || maxInt == null -> localizedString(Res.string.validation_players_number)
        minInt < 2 -> localizedString(Res.string.validation_min_players_count)
        maxInt > 128 -> localizedString(Res.string.validation_max_players_count)
        maxInt < minInt -> localizedString(Res.string.validation_max_gte_min)
        else -> null
    }

    // Presets de tiempo control
    val tcPresets = mapOf(
        "bullet" to (60 to 0),
        "blitz" to (180 to 2),
        "rapid" to (600 to 5),
        "classical" to (1800 to 20),
    )
    val (initialTime, increment) = tcPresets[selectedTc] ?: (180 to 2)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(localizedString(Res.string.create_tournament)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(localizedString(Res.string.user_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                // Tipo
                Text(localizedString(Res.string.tournament_format), style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    TournamentType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = {
                                Text(
                                    when (type) {
                                        TournamentType.ROUND_ROBIN -> localizedString(Res.string.tournament_type_round_robin)
                                        TournamentType.SWISS -> localizedString(Res.string.tournament_type_swiss)
                                    }
                                )
                            },
                        )
                    }
                }
                // Time control
                Text(localizedString(Res.string.time_control), style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    TimeControl.list().forEach { tc ->
                        FilterChip(
                            selected = selectedTc == tc,
                            onClick = { selectedTc = tc },
                            label = { Text(tc.replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
                // Rated toggle — invitados solo juegan no puntuado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(localizedString(Res.string.rated))
                    Switch(
                        checked = if (isGuest) false else isRated,
                        onCheckedChange = { isRated = it },
                        enabled = !isGuest,
                    )
                }
                // Spectating toggle — invitados siempre permiten espectadores
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(localizedString(Res.string.allow_spectators))
                    Switch(
                        checked = if (isGuest) true else spectatingAllowed,
                        onCheckedChange = { spectatingAllowed = it },
                        enabled = !isGuest,
                    )
                }
                // Jugadores
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minPlayers,
                        onValueChange = { minPlayers = it },
                        label = { Text(localizedString(Res.string.min_players)) },
                        singleLine = true,
                        isError = playerError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = maxPlayers,
                        onValueChange = { maxPlayers = it },
                        label = { Text(localizedString(Res.string.max_players)) },
                        singleLine = true,
                        isError = playerError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (playerError != null) {
                    Text(
                        text = playerError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val request = CreateTournamentRequest(
                        name = name.trim(),
                        type = selectedType,
                        timeControl = selectedTc,
                        initialTime = initialTime,
                        increment = increment,
                        isRated = isRated,
                        minPlayers = minInt ?: 4,
                        maxPlayers = maxInt ?: 16,
                        spectatingAllowed = if (isGuest) true else spectatingAllowed,
                    )
                    onCreate(request)
                },
                enabled = name.isNotBlank() && playerError == null,
            ) { Text(localizedString(Res.string.create)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(localizedString(Res.string.cancel)) }
        },
    )
}

// ── Tab: Conectados ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectedUsersTab(
    viewModel: IOnlineLobbyViewModel,
    currentUserId: String?,
    isCurrentUserGuest: Boolean,
    onlineGameViewModel: IOnlineGameViewModel,
    onNavigateToProfile: ((String) -> Unit)?,
) {
    val users by viewModel.onlineUsers.collectAsState()
    var challengeTarget by remember { mutableStateOf<OnlineUserDto?>(null) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        viewModel.startConnectedPolling()
        onDispose { viewModel.stopConnectedPolling() }
    }

    // ── Filtros locales ────────────────────────────────────────────────────────
    var statusFilter by remember { mutableStateOf<OnlineUserStatus?>(null) }
    var registeredOnly by remember { mutableStateOf(false) }

    val displayUsers = users
        .let { if (statusFilter != null) it.filter { u -> u.status == statusFilter } else it }
        .let { if (registeredOnly) it.filter { u -> !u.isGuest } else it }

    challengeTarget?.let { target ->
        ConnectedUserChallengeDialog(
            targetName = target.displayName,
            isCurrentUserGuest = isCurrentUserGuest,
            onConfirm = { tc, rated ->
                challengeTarget = null
                scope.launch { onlineGameViewModel.sendChallenge(target.userId, tc, rated) }
            },
            onDismiss = { challengeTarget = null },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ConnectedUsersFilterBar(
            statusFilter = statusFilter,
            onStatusFilter = { statusFilter = it },
            registeredOnly = registeredOnly,
            onRegisteredOnlyToggle = { registeredOnly = !registeredOnly },
        )
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                users.isEmpty() ->
                    CenteredMessage(text = localizedString(Res.string.lobby_online_users_section))

                displayUsers.isEmpty() -> CenteredMessage(
                    text = localizedString(Res.string.lobby_no_players_match_filters),
                )

                else -> LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(displayUsers, key = { it.userId }) { user ->
                        ConnectedUserRow(
                            user = user,
                            isSelf = user.userId == currentUserId,
                            onClick = if (!user.isGuest && onNavigateToProfile != null) {
                                { onNavigateToProfile(user.userId) }
                            } else null,
                            onChallenge = if (!user.isGuest && user.userId != currentUserId && user.status == OnlineUserStatus.IN_LOBBY) {
                                { challengeTarget = user }
                            } else null,
                        )
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectedUsersFilterBar(
    statusFilter: OnlineUserStatus?,
    onStatusFilter: (OnlineUserStatus?) -> Unit,
    registeredOnly: Boolean,
    onRegisteredOnlyToggle: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp)) {
        // Fila 1: chips de estado (ancho completo)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf<OnlineUserStatus?>(null, OnlineUserStatus.PLAYING, OnlineUserStatus.IN_LOBBY)
                .forEach { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = {
                            onStatusFilter(if (statusFilter == status && status != null) null else status)
                        },
                        label = {
                            Text(
                                when (status) {
                                    null -> localizedString(Res.string.lobby_filter_all)
                                    OnlineUserStatus.PLAYING -> localizedString(Res.string.lobby_status_playing)
                                    OnlineUserStatus.IN_LOBBY -> localizedString(Res.string.lobby_status_in_lobby)
                                },
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                    )
                }
        }
        // Fila 2: filtro secundario alineado a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = registeredOnly,
                onClick = onRegisteredOnlyToggle,
                label = { LocalizedText(Res.string.lobby_filter_registered_only) },
                leadingIcon = { Icon(TaratiIcons.Person, null, Modifier.size(14.dp)) },
            )
        }
    }
}

@Composable
private fun ConnectedUserRow(
    user: OnlineUserDto,
    isSelf: Boolean,
    onClick: (() -> Unit)?,
    onChallenge: (() -> Unit)?,
) {
    val statusColor = if (user.status == OnlineUserStatus.PLAYING) Color(0xFF4CAF50)
    else MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .background(statusColor, CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                if (isSelf) "${user.displayName} (${localizedString(Res.string.you)})"
                else user.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelf) FontWeight.SemiBold else FontWeight.Normal,
            )
            Text(
                localizedString(
                    if (user.status == OnlineUserStatus.PLAYING) Res.string.lobby_status_playing
                    else Res.string.lobby_status_in_lobby
                ),
                style = MaterialTheme.typography.bodySmall,
                color = if (user.status == OnlineUserStatus.PLAYING) Color(0xFF4CAF50)
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!user.isGuest && user.ratingBlitz != null) {
            Text(
                "${user.ratingBlitz}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp),
            )
        }
        if (onChallenge != null) {
            IconButton(onClick = onChallenge, modifier = Modifier.size(36.dp)) {
                Icon(
                    TaratiIcons.PlayArrow,
                    contentDescription = localizedString(Res.string.social_challenge),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ConnectedUserChallengeDialog(
    targetName: String,
    isCurrentUserGuest: Boolean,
    onConfirm: (timeControl: String, rated: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val timeControls = TimeControl.list()
    var selectedTc by remember { mutableStateOf(TimeControl.BLITZ.key) }
    var isRated by remember { mutableStateOf(!isCurrentUserGuest) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                localizedString(Res.string.social_challenge_dialog_title).replace($$"%1$s", targetName),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(timeControls) { tc ->
                        FilterChip(
                            selected = selectedTc == tc,
                            onClick = { selectedTc = tc },
                            label = { Text(tc.replaceFirstChar { it.titlecase() }) },
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(localizedString(Res.string.rated), style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isRated,
                        onCheckedChange = { isRated = it },
                        enabled = !isCurrentUserGuest,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedTc, isRated) }) {
                Text(localizedString(Res.string.social_challenge))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(localizedString(Res.string.cancel))
            }
        },
    )
}

// ── Sesión invitado ───────────────────────────────────────────────────────────

@Composable
private fun GuestSessionBanner(onSignIn: () -> Unit) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                TaratiIcons.Person,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    localizedString(Res.string.auth_guest_banner_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Text(
                    localizedString(Res.string.auth_guest_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f),
                )
            }
            TextButton(onClick = onSignIn) {
                Text(
                    localizedString(Res.string.auth_sign_in),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}
