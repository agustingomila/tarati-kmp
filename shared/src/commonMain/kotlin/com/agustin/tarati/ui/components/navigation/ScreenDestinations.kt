package com.agustin.tarati.ui.components.navigation

sealed class ScreenDestinations(
    val route: String,
) {
    object SplashScreenDest : ScreenDestinations(route = "splash_screen")

    object GameScreenDest : ScreenDestinations(route = "game_screen")

    object SettingsScreenDest : ScreenDestinations(route = "settings_screen")

    object GamesLibraryDest : ScreenDestinations(route = "games_library")

    object GameDetailsDest : ScreenDestinations(route = "game_details/{gameId}")
}