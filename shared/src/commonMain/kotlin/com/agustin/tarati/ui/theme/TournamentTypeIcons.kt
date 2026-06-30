package com.agustin.tarati.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.agustin.tarati.network.models.TournamentType
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.tournament_type_arena
import com.agustin.tarati.shared.generated.resources.tournament_type_elimination
import com.agustin.tarati.shared.generated.resources.tournament_type_round_robin
import com.agustin.tarati.shared.generated.resources.tournament_type_swiss

/**
 * Helpers de presentación de [TournamentType]: ícono y etiqueta localizada.
 *
 * En el mismo espíritu que [TimeControl.icon]: permite identificar de un vistazo el formato
 * del torneo en el lobby, el detalle y el diálogo de creación.
 *
 * - ROUND_ROBIN → flechas circulares: rotación todos-contra-todos.
 * - SWISS       → barras de ranking: emparejamiento por puntos.
 * - ARENA       → velocímetro: juego continuo y rápido (distinto de Bolt=Blitz y Timer=Rapid).
 * - ELIMINATION → árbol de bracket: eliminación directa.
 */
val TournamentType.icon: ImageVector
    get() = when (this) {
        TournamentType.ROUND_ROBIN -> TaratiIcons.RotateRight
        TournamentType.SWISS -> TaratiIcons.Leaderboard
        TournamentType.ARENA -> TaratiIcons.Speed
        TournamentType.ELIMINATION -> TaratiIcons.AccountTree
    }

/** Etiqueta localizada del formato del torneo. */
@Composable
fun tournamentTypeLabel(type: TournamentType): String = when (type) {
    TournamentType.ROUND_ROBIN -> localizedString(Res.string.tournament_type_round_robin)
    TournamentType.SWISS -> localizedString(Res.string.tournament_type_swiss)
    TournamentType.ARENA -> localizedString(Res.string.tournament_type_arena)
    TournamentType.ELIMINATION -> localizedString(Res.string.tournament_type_elimination)
}
