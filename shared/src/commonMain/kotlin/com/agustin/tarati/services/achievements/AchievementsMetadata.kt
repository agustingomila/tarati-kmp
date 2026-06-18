package com.agustin.tarati.services.achievements

import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.achievement_apprentice
import com.agustin.tarati.shared.generated.resources.achievement_champion
import com.agustin.tarati.shared.generated.resources.achievement_christmas_theme
import com.agustin.tarati.shared.generated.resources.achievement_dark_side
import com.agustin.tarati.shared.generated.resources.achievement_dead_but_dangerous
import com.agustin.tarati.shared.generated.resources.achievement_desc_apprentice
import com.agustin.tarati.shared.generated.resources.achievement_desc_champion
import com.agustin.tarati.shared.generated.resources.achievement_desc_christmas_theme
import com.agustin.tarati.shared.generated.resources.achievement_desc_dead_but_dangerous
import com.agustin.tarati.shared.generated.resources.achievement_desc_eternal_loop
import com.agustin.tarati.shared.generated.resources.achievement_desc_fifty_move_rule
import com.agustin.tarati.shared.generated.resources.achievement_desc_first_capture
import com.agustin.tarati.shared.generated.resources.achievement_desc_first_promotion
import com.agustin.tarati.shared.generated.resources.achievement_desc_first_victory
import com.agustin.tarati.shared.generated.resources.achievement_desc_grandmaster
import com.agustin.tarati.shared.generated.resources.achievement_desc_halloween_theme
import com.agustin.tarati.shared.generated.resources.achievement_desc_mit
import com.agustin.tarati.shared.generated.resources.achievement_desc_play_10_games
import com.agustin.tarati.shared.generated.resources.achievement_desc_rok_master
import com.agustin.tarati.shared.generated.resources.achievement_desc_stalemit
import com.agustin.tarati.shared.generated.resources.achievement_desc_strategist
import com.agustin.tarati.shared.generated.resources.achievement_desc_tactician
import com.agustin.tarati.shared.generated.resources.achievement_desc_the_dark_side
import com.agustin.tarati.shared.generated.resources.achievement_desc_the_first_light
import com.agustin.tarati.shared.generated.resources.achievement_desc_the_flipper
import com.agustin.tarati.shared.generated.resources.achievement_desc_unstoppable
import com.agustin.tarati.shared.generated.resources.achievement_desc_welcome_to_tarati
import com.agustin.tarati.shared.generated.resources.achievement_eternal_loop
import com.agustin.tarati.shared.generated.resources.achievement_fifty_move_rule
import com.agustin.tarati.shared.generated.resources.achievement_first_capture
import com.agustin.tarati.shared.generated.resources.achievement_first_light
import com.agustin.tarati.shared.generated.resources.achievement_first_promotion
import com.agustin.tarati.shared.generated.resources.achievement_first_victory
import com.agustin.tarati.shared.generated.resources.achievement_grandmaster
import com.agustin.tarati.shared.generated.resources.achievement_halloween_theme
import com.agustin.tarati.shared.generated.resources.achievement_mit
import com.agustin.tarati.shared.generated.resources.achievement_play_10_games
import com.agustin.tarati.shared.generated.resources.achievement_rok_master
import com.agustin.tarati.shared.generated.resources.achievement_stalemit
import com.agustin.tarati.shared.generated.resources.achievement_strategist
import com.agustin.tarati.shared.generated.resources.achievement_tactician
import com.agustin.tarati.shared.generated.resources.achievement_the_flipper
import com.agustin.tarati.shared.generated.resources.achievement_title_apprentice
import com.agustin.tarati.shared.generated.resources.achievement_title_champion
import com.agustin.tarati.shared.generated.resources.achievement_title_christmas_theme
import com.agustin.tarati.shared.generated.resources.achievement_title_dead_but_dangerous
import com.agustin.tarati.shared.generated.resources.achievement_title_eternal_loop
import com.agustin.tarati.shared.generated.resources.achievement_title_fifty_move_rule
import com.agustin.tarati.shared.generated.resources.achievement_title_first_capture
import com.agustin.tarati.shared.generated.resources.achievement_title_first_promotion
import com.agustin.tarati.shared.generated.resources.achievement_title_first_victory
import com.agustin.tarati.shared.generated.resources.achievement_title_grandmaster
import com.agustin.tarati.shared.generated.resources.achievement_title_halloween_theme
import com.agustin.tarati.shared.generated.resources.achievement_title_mit
import com.agustin.tarati.shared.generated.resources.achievement_title_play_10_games
import com.agustin.tarati.shared.generated.resources.achievement_title_rok_master
import com.agustin.tarati.shared.generated.resources.achievement_title_stalemit
import com.agustin.tarati.shared.generated.resources.achievement_title_strategist
import com.agustin.tarati.shared.generated.resources.achievement_title_tactician
import com.agustin.tarati.shared.generated.resources.achievement_title_the_dark_side
import com.agustin.tarati.shared.generated.resources.achievement_title_the_first_light
import com.agustin.tarati.shared.generated.resources.achievement_title_the_flipper
import com.agustin.tarati.shared.generated.resources.achievement_title_unstoppable
import com.agustin.tarati.shared.generated.resources.achievement_title_welcome_to_tarati
import com.agustin.tarati.shared.generated.resources.achievement_unstoppable
import com.agustin.tarati.shared.generated.resources.achievement_welcome_to_tarati
import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Metadatos estáticos por logro: recursos visuales, tipo incremental y valores de puntuación.
 * Fuente de verdad para la pantalla de logros y las insignias de perfil.
 * El estado de desbloqueo proviene del servidor vía [AchievementSyncService].
 */
object AchievementsMetadata {

    @Immutable
    data class Meta(
        val id: AchievementId,
        val titleRes: StringResource,
        val descRes: StringResource,
        val iconRes: DrawableResource,
        val isIncremental: Boolean,
        val maxSteps: Int,
        val isHidden: Boolean,
        val pointValue: Int,
        val sortOrder: Int,
    )

    val all: List<Meta> = listOf(
        Meta(AchievementId.WELCOME_TO_TARATI, Res.string.achievement_title_welcome_to_tarati, Res.string.achievement_desc_welcome_to_tarati, Res.drawable.achievement_welcome_to_tarati, false, 0, false, 5, 10),
        Meta(AchievementId.FIRST_CAPTURE, Res.string.achievement_title_first_capture, Res.string.achievement_desc_first_capture, Res.drawable.achievement_first_capture, false, 0, false, 5, 20),
        Meta(AchievementId.FIRST_PROMOTION, Res.string.achievement_title_first_promotion, Res.string.achievement_desc_first_promotion, Res.drawable.achievement_first_promotion, false, 0, false, 5, 30),
        Meta(AchievementId.FIRST_VICTORY, Res.string.achievement_title_first_victory, Res.string.achievement_desc_first_victory, Res.drawable.achievement_first_victory, false, 0, false, 10, 40),
        Meta(AchievementId.PLAY_10_GAMES, Res.string.achievement_title_play_10_games, Res.string.achievement_desc_play_10_games, Res.drawable.achievement_play_10_games, true, 10, false, 10, 50),
        Meta(AchievementId.THE_FLIPPER, Res.string.achievement_title_the_flipper, Res.string.achievement_desc_the_flipper, Res.drawable.achievement_the_flipper, true, 50, false, 20, 60),
        Meta(AchievementId.ROK_MASTER, Res.string.achievement_title_rok_master, Res.string.achievement_desc_rok_master, Res.drawable.achievement_rok_master, true, 25, false, 20, 70),
        Meta(AchievementId.UNSTOPPABLE, Res.string.achievement_title_unstoppable, Res.string.achievement_desc_unstoppable, Res.drawable.achievement_unstoppable, true, 10, false, 25, 80),
        Meta(AchievementId.CHAMPION, Res.string.achievement_title_champion, Res.string.achievement_desc_champion, Res.drawable.achievement_champion, false, 0, false, 50, 90),
        Meta(AchievementId.MIT, Res.string.achievement_title_mit, Res.string.achievement_desc_mit, Res.drawable.achievement_mit, false, 0, true, 15, 100),
        Meta(AchievementId.STALEMIT, Res.string.achievement_title_stalemit, Res.string.achievement_desc_stalemit, Res.drawable.achievement_stalemit, false, 0, true, 15, 110),
        Meta(AchievementId.ETERNAL_LOOP, Res.string.achievement_title_eternal_loop, Res.string.achievement_desc_eternal_loop, Res.drawable.achievement_eternal_loop, false, 0, true, 25, 120),
        Meta(AchievementId.FIFTY_MOVE_RULE, Res.string.achievement_title_fifty_move_rule, Res.string.achievement_desc_fifty_move_rule, Res.drawable.achievement_fifty_move_rule, false, 0, true, 15, 130),
        Meta(AchievementId.DEAD_BUT_DANGEROUS, Res.string.achievement_title_dead_but_dangerous, Res.string.achievement_desc_dead_but_dangerous, Res.drawable.achievement_dead_but_dangerous, false, 0, true, 25, 140),
        Meta(AchievementId.GRANDMASTER, Res.string.achievement_title_grandmaster, Res.string.achievement_desc_grandmaster, Res.drawable.achievement_grandmaster, true, 50, true, 50, 150),
        Meta(AchievementId.HALLOWEEN_THEME, Res.string.achievement_title_halloween_theme, Res.string.achievement_desc_halloween_theme, Res.drawable.achievement_halloween_theme, false, 0, true, 50, 160),
        Meta(AchievementId.CHRISTMAS_THEME, Res.string.achievement_title_christmas_theme, Res.string.achievement_desc_christmas_theme, Res.drawable.achievement_christmas_theme, false, 0, true, 50, 170),
        Meta(AchievementId.APPRENTICE, Res.string.achievement_title_apprentice, Res.string.achievement_desc_apprentice, Res.drawable.achievement_apprentice, false, 0, false, 5, 180),
        Meta(AchievementId.STRATEGIST, Res.string.achievement_title_strategist, Res.string.achievement_desc_strategist, Res.drawable.achievement_strategist, false, 0, false, 10, 190),
        Meta(AchievementId.TACTICIAN, Res.string.achievement_title_tactician, Res.string.achievement_desc_tactician, Res.drawable.achievement_tactician, false, 0, false, 25, 200),
        Meta(AchievementId.THE_FIRST_LIGHT, Res.string.achievement_title_the_first_light, Res.string.achievement_desc_the_first_light, Res.drawable.achievement_first_light, false, 0, true, 50, 210),
        Meta(AchievementId.THE_DARK_SIDE, Res.string.achievement_title_the_dark_side, Res.string.achievement_desc_the_dark_side, Res.drawable.achievement_dark_side, false, 0, true, 50, 220),
    )

    val byId: Map<AchievementId, Meta> = all.associateBy { it.id }
}
