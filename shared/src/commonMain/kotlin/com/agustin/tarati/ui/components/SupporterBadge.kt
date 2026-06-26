package com.agustin.tarati.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.supporter_badge
import com.agustin.tarati.ui.theme.TaratiIcons

/** Color con el que se tiñe el nombre de un supporter (flair C4). */
@Composable
@ReadOnlyComposable
fun supporterNameColor(): Color = MaterialTheme.colorScheme.primary

/**
 * Badge de supporter (flair C4): ícono pequeño junto al nombre de un usuario con el
 * entitlement `supporter` activo. Decorativo pero accesible (contentDescription).
 *
 * Se usa en las superficies "core" donde el nombre es prominente: header de
 * `PublicProfileScreen`, filas del leaderboard y del tab Conectados del lobby.
 */
@Composable
fun SupporterBadge(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
) {
    Icon(
        imageVector = TaratiIcons.Supporter,
        contentDescription = localizedString(Res.string.supporter_badge),
        tint = supporterNameColor(),
        modifier = modifier.size(size),
    )
}
