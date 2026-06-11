package com.agustin.tarati.services.achievements

/**
 * IDs canónicos de todos los logros del juego.
 *
 * Usado como identificador en el servidor, en [AchievementSyncService] y
 * como clave de traducción al resource ID de Google Play Games en Android.
 * El campo [id] es la cadena almacenada en la tabla `user_achievements`.
 */
enum class AchievementId(val id: String) {
    WELCOME_TO_TARATI("welcome_to_tarati"),
    FIRST_CAPTURE("first_capture"),
    FIRST_PROMOTION("first_promotion"),
    FIRST_VICTORY("first_victory"),
    PLAY_10_GAMES("play_10_games"),
    THE_FLIPPER("the_flipper"),
    ROK_MASTER("rok_master"),
    UNSTOPPABLE("unstoppable"),
    CHAMPION("champion"),
    MIT("mit"),
    STALEMIT("stalemit"),
    ETERNAL_LOOP("eternal_loop"),
    FIFTY_MOVE_RULE("fifty_move_rule"),
    DEAD_BUT_DANGEROUS("dead_but_dangerous"),
    GRANDMASTER("grandmaster"),
    HALLOWEEN_THEME("halloween_theme"),
    CHRISTMAS_THEME("christmas_theme"),
    APPRENTICE("apprentice"),
    STRATEGIST("strategist"),
    TACTICIAN("tactician"),
    THE_FIRST_LIGHT("the_first_light"),
    THE_DARK_SIDE("the_dark_side"),
    ;

    companion object {
        fun fromId(id: String): AchievementId? = entries.firstOrNull { it.id == id }
    }
}
