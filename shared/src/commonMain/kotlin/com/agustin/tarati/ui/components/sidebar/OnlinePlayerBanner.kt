package com.agustin.tarati.ui.components.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.network.models.OnlineGame
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.you
import com.agustin.tarati.ui.theme.TaratiIcons

/**
 * Banner que reemplaza al [PlayerConfigSection] durante una partida online activa.
 *
 * Muestra dos filas — una por banda de color — con identidad fija:
 *
 * ```
 * ● [Person]   Tú              (banda local)
 * ● [Network]  Bot (Hard) 1600 (banda remota)
 * ```
 *
 * No hay toggles ni dropdowns: la configuración del juego online es inmutable
 * una vez iniciado.
 *
 * @param onlineGame  Partida online activa con info del oponente y color local.
 * @param localName   Nombre del jugador local (de ajustes).
 */
@Composable
fun OnlinePlayerBanner(
    onlineGame: OnlineGame,
    localName: String,
    modifier: Modifier = Modifier,
) {
    val localColor = if (onlineGame.yourColor == "white") CobColor.WHITE else CobColor.BLACK
    val remoteColor = if (localColor == CobColor.WHITE) CobColor.BLACK else CobColor.WHITE

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        OnlinePlayerRow(
            cobColor = localColor,
            isLocal = true,
            displayName = localName.ifBlank { localizedString(Res.string.you) },
            rating = null,
        )
        OnlinePlayerRow(
            cobColor = remoteColor,
            isLocal = false,
            displayName = onlineGame.opponentInfo.username,
            rating = onlineGame.opponentInfo.rating,
        )
    }
}

@Composable
private fun OnlinePlayerRow(
    cobColor: CobColor,
    isLocal: Boolean,
    displayName: String,
    rating: Int?,
) {
    val chipBg = if (isLocal) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.secondaryContainer
    val chipContent = if (isLocal) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSecondaryContainer
    // Person = local human, SmartToy = remote (bot or human online)
    val icon = if (isLocal) TaratiIcons.Person else TaratiIcons.SmartToy

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Disc de color (reutiliza BandIndicator via Canvas)
        BandIndicator(cobColor)

        // Chip de identidad
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(chipBg)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = chipContent,
            )
            Column {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = chipContent,
                )
                if (rating != null) {
                    Text(
                        text = rating.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = chipContent.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}