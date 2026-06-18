package com.agustin.tarati.features.achievements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agustin.tarati.network.models.ServerAchievementDto
import com.agustin.tarati.services.achievements.AchievementsMetadata
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.achievements
import com.agustin.tarati.shared.generated.resources.achievements_none_yet
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Sección de logros para [PublicProfileScreen].
 * Muestra una cuadrícula compacta de insignias de los logros desbloqueados.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAchievementsSection(
    achievements: List<ServerAchievementDto>,
    modifier: Modifier = Modifier,
) {
    val unlocked = achievements.filter { it.unlockedAt != null }

    Text(
        text = stringResource(Res.string.achievements),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )

    if (unlocked.isEmpty()) {
        Text(
            text = stringResource(Res.string.achievements_none_yet),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(horizontal = 20.dp, vertical = 4.dp),
        )
        return
    }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
    ) {
        unlocked.forEach { dto ->
            val meta = AchievementsMetadata.byId.entries
                .firstOrNull { it.key.id == dto.achievementId }?.value
            if (meta != null) {
                AchievementBadge(meta = meta)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AchievementBadge(
    meta: AchievementsMetadata.Meta,
    modifier: Modifier = Modifier,
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(meta.iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .combinedClickable(
                    onClick = { showTooltip = !showTooltip },
                    onLongClick = { showTooltip = true },
                ),
        )
    }
}
