package com.agustin.tarati.ui.components.navigation

sealed class ScreenDestinations(
    val route: String,
) {
    object SplashScreenDest : ScreenDestinations(route = "splash_screen")

    object GameScreenDest : ScreenDestinations(route = "game_screen")

    object SettingsScreenDest : ScreenDestinations(route = "settings_screen")

    object GamesLibraryDest : ScreenDestinations(route = "games_library")

    object GameDetailsDest : ScreenDestinations(route = "game_details/{gameId}")

    object OnlineLobbyDest : ScreenDestinations(route = "online_lobby")

    object LoginScreenDest : ScreenDestinations(route = "login_screen")

    object LeaderboardDest : ScreenDestinations(route = "leaderboard")

    object PublicProfileDest : ScreenDestinations(route = "public_profile/{userId}") {
        fun createRoute(userId: String) = "public_profile/$userId"
    }

    object TournamentDetailDest : ScreenDestinations(route = "tournament/{tournamentId}") {
        fun createRoute(tournamentId: String) = "tournament/$tournamentId"
    }
}