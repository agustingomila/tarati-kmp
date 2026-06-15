package com.agustin.tarati.ui.components.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import com.agustin.tarati.core.domain.game.play.GameStatus
import com.agustin.tarati.features.detail.GameDetailsScreen
import com.agustin.tarati.features.detail.GameDetailsViewModel
import com.agustin.tarati.features.detail.IGameDetailsViewModel
import com.agustin.tarati.features.game.GameScreen
import com.agustin.tarati.features.game.IGameModel
import com.agustin.tarati.features.library.GamesLibraryScreen
import com.agustin.tarati.features.library.GamesLibraryViewModel
import com.agustin.tarati.features.library.IGamesLibraryViewModel
import com.agustin.tarati.features.online.auth.AuthState
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.game.IOnlineGameViewModel
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
import com.agustin.tarati.ui.components.game.animation.BoardAnimationViewModel
import com.agustin.tarati.ui.components.game.animation.BoardGeometryViewModel
import com.agustin.tarati.ui.components.game.animation.IBoardAnimationViewModel
import com.agustin.tarati.ui.components.game.animation.IBoardGeometryViewModel
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.GameDetailsDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.GameScreenDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.GamesLibraryDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.LeaderboardDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.PublicProfileDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.SettingsScreenDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.SplashScreenDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.TournamentDetailDest
import com.agustin.tarati.ui.layout.CompanionPanelDestination
import com.agustin.tarati.ui.layout.LocalCompanionPanelController
import com.agustin.tarati.ui.layout.LocalScreenLayout
import com.agustin.tarati.ui.layout.ScreenLayout
import com.agustin.tarati.ui.settingsEvents
import com.agustin.tarati.ui.splash.SplashScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    onShowLogin: ((suspend () -> Unit)?) -> Unit = {},
    gameViewModel: IGameModel = injectGameViewModel(),
    animationViewModel: IBoardAnimationViewModel = koinViewModel<BoardAnimationViewModel>(),
    geometryViewModel: IBoardGeometryViewModel = koinViewModel<BoardGeometryViewModel>(),
    gamesLibraryViewModel: IGamesLibraryViewModel = koinViewModel<GamesLibraryViewModel>(),
    gameDetailsViewModel: IGameDetailsViewModel = koinViewModel<GameDetailsViewModel>(),
    settingsViewModel: ISettingsViewModel = koinViewModel<SettingsViewModel>(),
    onlineLobbyViewModel: IOnlineLobbyViewModel = koinViewModel<OnlineLobbyViewModel>(),
    onlineGameViewModel: IOnlineGameViewModel = koinInject(),
    authViewModel: IAuthViewModel = koinInject(),
    clipboardHelper: GameClipboardHelper = koinInject(),
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Estado de transferencia entre el lobby y GameScreen.
    val pendingMatchmaking = remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    NavHost(
        navController = navController,
        startDestination = SplashScreenDest.route,
    ) {
        composable(route = SplashScreenDest.route) {
            // El juego es totalmente offline — siempre arranca en GameScreen.
            SplashScreen {
                navController.navigate(GameScreenDest.route) {
                    popUpTo(SplashScreenDest.route) { inclusive = true }
                }
            }
        }

        composable(route = GameScreenDest.route) {
            // En Expanded, las acciones de navegación populan el panel lateral en lugar
            // de navegar en el NavGraph — el tablero permanece visible en todo momento.
            val layout = LocalScreenLayout.current
            val companion = LocalCompanionPanelController.current

            GameScreen(
                viewModel = gameViewModel,
                animationViewModel = animationViewModel,
                geometryViewModel = geometryViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = {
                    if (layout == ScreenLayout.Expanded)
                        companion.navigate(CompanionPanelDestination.Settings)
                    else
                        navController.navigate(SettingsScreenDest.route)
                },
                onGamesLibrary = {
                    if (layout == ScreenLayout.Expanded)
                        companion.navigate(CompanionPanelDestination.Library)
                    else
                        navController.navigate(GamesLibraryDest.route)
                },
                onOnlineLobby = {
                    if (layout == ScreenLayout.Expanded)
                        companion.navigate(CompanionPanelDestination.Lobby)
                    else
                        navController.navigate(ScreenDestinations.OnlineLobbyDest.route)
                },
                onSaveGame = { match -> gamesLibraryViewModel.saveCurrentGame(match) },
                onNavigateToLogin = { suspendAction -> onShowLogin(suspendAction) },
                initialMatchmaking = pendingMatchmaking.value.also { pendingMatchmaking.value = null },
            )
        }

        composable(route = SettingsScreenDest.route) {
            val gameStatus by gameViewModel.gameStatus.collectAsState()
            val currentUser by authViewModel.authState.collectAsState()
            LanguageAwareSettingsScreen(
                viewModel = settingsViewModel,
                events = settingsEvents(settingsViewModel),
                isGameActive = gameStatus == GameStatus.PLAYING,
                onNavigateBack = { navController.popBackStack() },
                loggedInUsername = (currentUser as? AuthState.Authenticated)
                    ?.userInfo?.username,
                onLogout = if (authViewModel.isAuthenticated) {
                    {
                        scope.launch {
                            authViewModel.logout()
                            // El NavGraph no necesita hacer nada más:
                            // el guard de authState en LoginScreen se disparará al Unauthenticated
                            navController.popBackStack()
                        }
                    }
                } else null,
            )
        }

        composable(GamesLibraryDest.route) {
            GamesLibraryScreen(
                onGameSelected = { gameId ->
                    navController.navigate("game_details/$gameId")
                },
                onBack = { navController.popBackStack() },
                viewModel = gamesLibraryViewModel,
            )
        }

        composable(GameDetailsDest.route) { backStackEntry ->
            val gameId = backStackEntry.arguments?.read {
                getString("gameId")
            } ?: ""
            GameDetailsScreen(
                gameId = gameId,
                onImport = { matchDto ->
                    gameViewModel.importGameFromMatchDto(matchDto)
                    navController.navigate(GameScreenDest.route) {
                        popUpTo(GameScreenDest.route) { inclusive = true }
                    }
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
                onBack = { navController.popBackStack() },
                viewModel = gameDetailsViewModel,
            )
        }

        composable(ScreenDestinations.OnlineLobbyDest.route) {
            OnlineLobbyScreen(
                onBack = { navController.popBackStack() },
                onShowLogin = { onShowLogin(null) },
                onSpectateGame = { _ ->
                    navController.popBackStack()
                },
                onLeaderboard = { navController.navigate(LeaderboardDest.route) },
                onNavigateToProfile = { userId ->
                    navController.navigate(PublicProfileDest.createRoute(userId))
                },
                onNavigateToGameDetails = { gameId ->
                    // Pre-cargar el detalle desde el servidor antes de navegar.
                    // loadAndPreviewGame obtiene el Game y lo convierte a MatchDto;
                    // updateCurrentMatchDto lo deja disponible para GameDetailsScreen
                    // sin necesidad de una segunda llamada HTTP.
                    scope.launch {
                        val matchDto = onlineLobbyViewModel.loadAndPreviewGame(gameId)
                        if (matchDto != null) {
                            gameDetailsViewModel.updateCurrentMatchDto(matchDto)
                            navController.navigate("game_details/$gameId")
                        }
                    }
                },
                onNavigateToTournament = { tournamentId ->
                    navController.navigate(TournamentDetailDest.createRoute(tournamentId))
                },
                viewModel = onlineLobbyViewModel,
            )
        }

        composable(LeaderboardDest.route) {
            LeaderboardScreen(
                onBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate(PublicProfileDest.createRoute(userId))
                },
            )
        }

        composable(PublicProfileDest.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.read { getString("userId") } ?: return@composable
            PublicProfileScreen(
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigateToGameDetails = { gameId ->
                    scope.launch {
                        val matchDto = onlineLobbyViewModel.loadAndPreviewGame(gameId)
                        if (matchDto != null) {
                            gameDetailsViewModel.updateCurrentMatchDto(matchDto)
                            navController.navigate("game_details/$gameId")
                        }
                    }
                },
            )
        }

        composable(TournamentDetailDest.route) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.read {
                getString("tournamentId")
            } ?: return@composable
            TournamentDetailScreen(
                tournamentId = tournamentId,
                onBack = { navController.popBackStack() },
                onSpectateGame = { _ -> },
                onNavigateToGameDetails = { gameId ->
                    scope.launch {
                        val matchDto = onlineLobbyViewModel.loadAndPreviewGame(gameId)
                        if (matchDto != null) {
                            gameDetailsViewModel.updateCurrentMatchDto(matchDto)
                            navController.navigate("game_details/$gameId")
                        }
                    }
                },
            )
        }
    }
}
