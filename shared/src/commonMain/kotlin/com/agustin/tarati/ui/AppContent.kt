package com.agustin.tarati.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.features.detail.GameDetailsScreen
import com.agustin.tarati.features.detail.GameDetailsViewModel
import com.agustin.tarati.features.detail.IGameDetailsViewModel
import com.agustin.tarati.features.game.IGameModel
import com.agustin.tarati.features.library.GamesLibraryScreen
import com.agustin.tarati.features.library.GamesLibraryViewModel
import com.agustin.tarati.features.library.IGamesLibraryViewModel
import com.agustin.tarati.features.online.auth.AuthState
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.auth.LoginSheet
import com.agustin.tarati.features.online.connection.IConnectionViewModel
import com.agustin.tarati.features.online.devServerUrl
import com.agustin.tarati.features.online.game.ChallengeEvent
import com.agustin.tarati.features.online.game.IOnlineGameViewModel
import com.agustin.tarati.features.online.game.TournamentEvent
import com.agustin.tarati.features.online.lobby.IOnlineLobbyViewModel
import com.agustin.tarati.features.online.lobby.OnlineLobbyScreen
import com.agustin.tarati.features.online.lobby.OnlineLobbyViewModel
import com.agustin.tarati.features.online.social.LeaderboardScreen
import com.agustin.tarati.features.online.social.PublicProfileScreen
import com.agustin.tarati.features.online.tournament.TournamentDetailScreen
import com.agustin.tarati.features.settings.ISettingsViewModel
import com.agustin.tarati.features.settings.LanguageAwareSettingsScreen
import com.agustin.tarati.features.settings.SettingsViewModel
import com.agustin.tarati.services.clipboard.GameClipboardHelper
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.services.notifications.AlertHost
import com.agustin.tarati.services.notifications.ToastHost
import com.agustin.tarati.services.notifications.UIMessage
import com.agustin.tarati.services.notifications.UIMessageBus
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.accept
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.challenge_declined_msg
import com.agustin.tarati.shared.generated.resources.challenge_expired_msg
import com.agustin.tarati.shared.generated.resources.challenge_from
import com.agustin.tarati.shared.generated.resources.challenge_invite
import com.agustin.tarati.shared.generated.resources.tournament_cancelled_notification
import com.agustin.tarati.shared.generated.resources.tournament_finished_notification
import com.agustin.tarati.shared.generated.resources.tournament_game_assigned
import com.agustin.tarati.ui.components.navigation.NavGraph
import com.agustin.tarati.ui.components.navigation.injectGameViewModel
import com.agustin.tarati.ui.layout.CompanionPanelController
import com.agustin.tarati.ui.layout.CompanionPanelDestination
import com.agustin.tarati.ui.layout.DisplayMode
import com.agustin.tarati.ui.layout.LocalCompanionPanelController
import com.agustin.tarati.ui.layout.LocalScreenLayout
import com.agustin.tarati.ui.layout.ScreenLayout
import com.agustin.tarati.ui.layout.screenLayoutFor
import com.agustin.tarati.ui.theme.AppTheme
import com.agustin.tarati.ui.theme.PaletteManager
import com.agustin.tarati.ui.theme.TaratiTheme
import com.agustin.tarati.ui.theme.availablePalettes
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Composable raíz compartido entre Android, Desktop y Web.
 *
 * ## Layouts adaptativos
 * - [ScreenLayout.Compact] / [ScreenLayout.Medium]: [NavGraph] ocupa toda la pantalla.
 * - [ScreenLayout.Expanded]: tablero siempre visible a la izquierda; panel lateral
 *   ([CompanionPane]) a la derecha cuando hay un destino activo. Las acciones de
 *   [GameScreen] (Lobby, Settings, Librería) populan el panel en lugar de navegar.
 *
 * ## KMP
 * Usa koinViewModel<ISettingsViewModel>() — Koin resuelve la implementación concreta
 * según el módulo registrado en cada plataforma.
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

    val companion = remember { CompanionPanelController() }

    val authViewModel: IAuthViewModel = koinInject()
    val connectionViewModel: IConnectionViewModel = koinInject()
    val authState by authViewModel.authState.collectAsState()

    var showLoginModal by remember { mutableStateOf(false) }
    var pendingLoginAction by remember { mutableStateOf<(suspend () -> Unit)?>(null) }
    val loginScope = rememberCoroutineScope()

    val onShowLogin: ((suspend () -> Unit)?) -> Unit = { action ->
        pendingLoginAction = action
        showLoginModal = true
    }

    // Al autenticarse, precarga el nombre local con el displayName de la cuenta.
    // El usuario puede cambiarlo después sin afectar su cuenta online.
    LaunchedEffect(authState) {
        val state = authState
        if (state is AuthState.Authenticated && !state.userInfo.isGuest) {
            val name = state.userInfo.displayName.takeIf { it.isNotBlank() }
                ?: state.userInfo.username
            settingsViewModel.setUserName(name)
        }
    }

    TaratiTheme(darkTheme = useDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val layout = screenLayoutFor(maxWidth)
                CompositionLocalProvider(
                    LocalScreenLayout provides layout,
                    LocalCompanionPanelController provides companion,
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (layout == ScreenLayout.Expanded) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    NavGraph(
                                        settingsViewModel = settingsViewModel,
                                        onShowLogin = onShowLogin,
                                    )
                                }
                                if (companion.isOpen) {
                                    VerticalDivider()
                                    Box(modifier = Modifier.width(380.dp).fillMaxHeight()) {
                                        CompanionPane(
                                            controller = companion,
                                            settingsViewModel = settingsViewModel,
                                            onShowLogin = onShowLogin,
                                        )
                                    }
                                }
                            }
                        } else {
                            NavGraph(
                                settingsViewModel = settingsViewModel,
                                onShowLogin = onShowLogin,
                            )
                        }
                        ToastHost(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                        AlertHost()
                        ChallengeNotificationEffect()
                        TournamentNotificationEffect()
                        if (showLoginModal) {
                            LoginSheet(
                                onLoginSuccess = {
                                    showLoginModal = false
                                    // Re-conectar con las nuevas credenciales (upgrade desde invitado)
                                    val newToken = authViewModel.accessToken
                                    if (newToken != null) {
                                        loginScope.launch {
                                            if (connectionViewModel.isConnected) connectionViewModel.disconnect()
                                            connectionViewModel.connectToServer(devServerUrl, newToken)
                                        }
                                    }
                                    val action = pendingLoginAction
                                    pendingLoginAction = null
                                    if (action != null) loginScope.launch { action() }
                                },
                                onDismiss = {
                                    showLoginModal = false
                                    pendingLoginAction = null
                                },
                                authViewModel = authViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Companion panel ───────────────────────────────────────────────────────────

/**
 * Renderiza el destino activo del panel lateral.
 *
 * Cada pantalla recibe callbacks que navegan dentro del panel ([controller.navigate])
 * o lo cierran ([controller.close]), en lugar de delegar en el [NavGraph] global.
 *
 * Las pantallas aún sin [DisplayMode.CompanionPanel] optimizado renderizan su propio
 * Scaffold dentro del panel — funcionalmente correcto, visualmente mejorable en fases
 * siguientes agregando el parámetro `displayMode` a cada una.
 */
@Composable
private fun CompanionPane(
    controller: CompanionPanelController,
    settingsViewModel: ISettingsViewModel,
    onShowLogin: ((suspend () -> Unit)?) -> Unit = {},
    gameViewModel: IGameModel = injectGameViewModel(),
    authViewModel: IAuthViewModel = koinInject(),
    gamesLibraryViewModel: IGamesLibraryViewModel = koinViewModel<GamesLibraryViewModel>(),
    onlineLobbyViewModel: IOnlineLobbyViewModel = koinViewModel<OnlineLobbyViewModel>(),
    gameDetailsViewModel: IGameDetailsViewModel = koinViewModel<GameDetailsViewModel>(),
    clipboardHelper: GameClipboardHelper = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val gameStatus by gameViewModel.gameStatus.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val loggedInUsername = (authState as? AuthState.Authenticated)?.userInfo?.username

    when (val dest = controller.destination) {
        CompanionPanelDestination.None -> Unit

        CompanionPanelDestination.Lobby -> OnlineLobbyScreen(
            displayMode = DisplayMode.CompanionPanel,
            onBack = controller::close,
            onShowLogin = { onShowLogin(null) },
            onMatchFound = { /* no-op: el tablero en el panel primario ya muestra la partida */ },
            onSpectateGame = { /* no-op: el tablero en el panel primario ya muestra el espectado */ },
            onLeaderboard = { controller.navigate(CompanionPanelDestination.Leaderboard) },
            onNavigateToProfile = { userId ->
                controller.navigate(CompanionPanelDestination.Profile(userId))
            },
            onNavigateToGameDetails = { gameId ->
                scope.launch {
                    val matchDto = onlineLobbyViewModel.loadAndPreviewGame(gameId)
                    if (matchDto != null) {
                        gameDetailsViewModel.updateCurrentMatchDto(matchDto)
                        controller.navigate(CompanionPanelDestination.GameDetails(gameId))
                    }
                }
            },
            onNavigateToTournament = { tournamentId ->
                controller.navigate(CompanionPanelDestination.TournamentDetail(tournamentId))
            },
        )

        CompanionPanelDestination.Leaderboard -> LeaderboardScreen(
            onBack = controller::back,
            onNavigateToProfile = { userId ->
                controller.navigate(CompanionPanelDestination.Profile(userId))
            },
        )

        is CompanionPanelDestination.Profile -> key(dest.userId) {
            PublicProfileScreen(
                userId = dest.userId,
                onBack = controller::back,
                onNavigateToGameDetails = { gameId ->
                    scope.launch {
                        val matchDto = onlineLobbyViewModel.loadAndPreviewGame(gameId)
                        if (matchDto != null) {
                            gameDetailsViewModel.updateCurrentMatchDto(matchDto)
                            controller.navigate(CompanionPanelDestination.GameDetails(gameId))
                        }
                    }
                },
            )
        }

        CompanionPanelDestination.Settings -> LanguageAwareSettingsScreen(
            viewModel = settingsViewModel,
            events = settingsEvents(settingsViewModel),
            isGameActive = gameStatus == GameStatus.PLAYING,
            onNavigateBack = controller::close,
            loggedInUsername = loggedInUsername,
            onLogout = if (authViewModel.isAuthenticated) {
                {
                    scope.launch {
                        authViewModel.logout()
                        controller.close()
                    }
                }
            } else null,
        )

        CompanionPanelDestination.Library -> GamesLibraryScreen(
            onGameSelected = { gameId ->
                controller.navigate(CompanionPanelDestination.GameDetails(gameId))
            },
            onBack = controller::close,
            viewModel = gamesLibraryViewModel,
        )

        is CompanionPanelDestination.TournamentDetail -> key(dest.tournamentId) {
            TournamentDetailScreen(
                tournamentId = dest.tournamentId,
                onBack = controller::back,
                displayMode = DisplayMode.CompanionPanel,
                onNavigateToGameDetails = { gameId ->
                    scope.launch {
                        val matchDto = onlineLobbyViewModel.loadAndPreviewGame(gameId)
                        if (matchDto != null) {
                            gameDetailsViewModel.updateCurrentMatchDto(matchDto)
                            controller.navigate(CompanionPanelDestination.GameDetails(gameId))
                        }
                    }
                },
            )
        }

        is CompanionPanelDestination.GameDetails -> GameDetailsScreen(
            gameId = dest.gameId,
            onImport = { matchDto ->
                gameViewModel.importGameFromMatchDto(matchDto)
                controller.close()
            },
            onCopyMoveHistory = { matchDto ->
                scope.launch {
                    clipboardHelper.copyMoveHistory(
                        moves = matchDto.game.moveHistory,
                        gameState = gameViewModel.gameState.value,
                        playerSide = gameViewModel.playerSide.value,
                        aiEnabled = gameViewModel.aIEnabled.value,
                    )
                }
            },
            onBack = controller::back,
            viewModel = gameDetailsViewModel,
        )
    }
}

// ── Challenge notifications ───────────────────────────────────────────────────

/**
 * Collector global de eventos de desafío directo.
 *
 * Corre en la raíz de la composición (AppContent) para que las notificaciones
 * de challenge sean visibles desde cualquier pantalla, no solo desde GameScreen.
 */
@Composable
private fun ChallengeNotificationEffect(
    onlineGameViewModel: IOnlineGameViewModel = koinInject(),
    bus: UIMessageBus = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val challengeDeclinedMsg = localizedString(Res.string.challenge_declined_msg)
    val challengeExpiredMsg = localizedString(Res.string.challenge_expired_msg)

    LaunchedEffect(Unit) {
        onlineGameViewModel.challengeEvents.collect { event ->
            when (event) {
                is ChallengeEvent.Received -> {
                    val tcDisplay = event.timeControl.replaceFirstChar { it.titlecase() }
                    val ratedDisplay = if (event.rated) "·" else ""
                    bus.alert { dismiss ->
                        androidx.compose.foundation.layout.Column(
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text(
                                text = localizedString(Res.string.challenge_from)
                                    .replace($$"%1$s", event.challengerInfo.username),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = localizedString(Res.string.challenge_invite)
                                    .replace($$"%1$s", event.challengerInfo.username)
                                    .replace($$"%2$s", "$tcDisplay $ratedDisplay"),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Row(
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                            ) {
                                Button(onClick = {
                                    scope.launch {
                                        onlineGameViewModel.respondToChallenge(event.challengeId, true)
                                    }
                                    dismiss()
                                }) {
                                    Text(localizedString(Res.string.accept))
                                }
                                TextButton(onClick = {
                                    scope.launch {
                                        onlineGameViewModel.respondToChallenge(event.challengeId, false)
                                    }
                                    dismiss()
                                }) {
                                    Text(localizedString(Res.string.cancel))
                                }
                            }
                        }
                    }
                }

                is ChallengeEvent.Declined -> bus.toast(
                    UIMessage.Toast(challengeDeclinedMsg)
                )

                is ChallengeEvent.Expired -> bus.toast(
                    UIMessage.Toast(challengeExpiredMsg)
                )
            }
        }
    }
}

/**
 * Collector global de eventos de torneo.
 *
 * Corre en la raíz de la composición para que las notificaciones de torneo
 * sean visibles desde cualquier pantalla.
 */
@Composable
private fun TournamentNotificationEffect(
    onlineGameViewModel: IOnlineGameViewModel = koinInject(),
    bus: UIMessageBus = koinInject(),
) {
    val gameAssignedTemplate = localizedString(Res.string.tournament_game_assigned)
    val tournamentFinishedMsg = localizedString(Res.string.tournament_finished_notification)
    val tournamentCancelledMsg = localizedString(Res.string.tournament_cancelled_notification)

    LaunchedEffect(Unit) {
        onlineGameViewModel.tournamentEvents.collect { event ->
            when (event) {
                is TournamentEvent.GameAssigned -> bus.toast(
                    UIMessage.Toast(
                        gameAssignedTemplate
                            .replace($$"%1$s", event.tournamentName)
                            .replace($$"%2$d", "${event.round}")
                            .replace($$"%3$d", "${event.totalRounds}")
                    )
                )

                is TournamentEvent.Finished -> bus.toast(
                    UIMessage.Toast(tournamentFinishedMsg)
                )

                is TournamentEvent.RoundStarted -> Unit
                is TournamentEvent.StandingsUpdated -> Unit
                is TournamentEvent.Cancelled -> bus.toast(UIMessage.Toast(tournamentCancelledMsg))
            }
        }
    }
}

fun setCurrentPalette(paletteName: String) {
    val currentPalette =
        availablePalettes.find { it.name == paletteName }
            ?: availablePalettes.first()
    PaletteManager.setPalette(currentPalette)
}
