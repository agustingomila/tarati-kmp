package com.agustin.tarati.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
import com.agustin.tarati.core.domain.game.time.TimeControl

/**
 * Ícono representativo de cada [TimeControl], estilo plataformas de ajedrez online:
 * bullet = proyectil, blitz = rayo, rapid = cronómetro, classical = reloj analógico.
 *
 * Permite identificar de un vistazo el ritmo de juego en el lobby, leaderboard,
 * perfiles públicos y barras de partida.
 */
val TimeControl.icon: ImageVector
    get() = when (this) {
        TimeControl.BULLET -> TaratiIcons.Bullet
        TimeControl.BLITZ -> TaratiIcons.Bolt
        TimeControl.RAPID -> TaratiIcons.Timer
        TimeControl.CLASSICAL -> TaratiIcons.Schedule
    }

/**
 * Variante por key de BD (`"bullet"` / `"blitz"` / `"rapid"` / `"classical"`).
 * Cae a [TaratiIcons.Timer] si la key es desconocida o inválida.
 */
fun timeControlIcon(key: String): ImageVector =
    runCatching { TimeControl.fromKey(key).icon }.getOrDefault(TaratiIcons.Timer)
