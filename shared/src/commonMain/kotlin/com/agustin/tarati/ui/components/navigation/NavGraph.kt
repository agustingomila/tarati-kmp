package com.agustin.tarati.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.agustin.tarati.features.game.GameViewModel
import com.agustin.tarati.features.game.IGameModel
import com.agustin.tarati.features.library.GamesLibraryScreen
import com.agustin.tarati.features.library.GamesLibraryViewModel
import com.agustin.tarati.features.library.IGamesLibraryViewModel
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
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.SettingsScreenDest
import com.agustin.tarati.ui.components.navigation.ScreenDestinations.SplashScreenDest
import com.agustin.tarati.ui.settingsEvents
import com.agustin.tarati.ui.splash.SplashScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    gameViewModel: IGameModel = koinViewModel<GameViewModel>(),
    animationViewModel: IBoardAnimationViewModel = koinViewModel<BoardAnimationViewModel>(),
    geometryViewModel: IBoardGeometryViewModel = koinViewModel<BoardGeometryViewModel>(),
    gamesLibraryViewModel: IGamesLibraryViewModel = koinViewModel<GamesLibraryViewModel>(),
    gameDetailsViewModel: IGameDetailsViewModel = koinViewModel<GameDetailsViewModel>(),
    settingsViewModel: ISettingsViewModel = koinViewModel<SettingsViewModel>(),
    clipboardHelper: GameClipboardHelper = koinInject(),
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = SplashScreenDest.route,
    ) {
        composable(route = SplashScreenDest.route) {
            SplashScreen {
                navController.navigate(GameScreenDest.route) {
                    popUpTo(SplashScreenDest.route) { inclusive = true }
                }
            }
        }

        composable(route = GameScreenDest.route) {
            GameScreen(
                viewModel = gameViewModel,
                animationViewModel = animationViewModel,
                geometryViewModel = geometryViewModel,
                settingsViewModel = settingsViewModel,
                onNavigateToSettings = {
                    navController.navigate(SettingsScreenDest.route)
                },
                onGamesLibrary = { navController.navigate(GamesLibraryDest.route) },
                onSaveGame = { match -> gamesLibraryViewModel.saveCurrentGame(match) },
            )
        }

        composable(route = SettingsScreenDest.route) {
            val gameStatus by gameViewModel.gameStatus.collectAsState()
            LanguageAwareSettingsScreen(
                viewModel = settingsViewModel,
                events = settingsEvents(settingsViewModel),
                isGameActive = gameStatus == GameStatus.PLAYING,
                onNavigateBack = {
                    navController.popBackStack()
                },
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
    }
}