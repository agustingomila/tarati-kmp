package com.agustin.tarati.features.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.achievements.AchievementsMetadata
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.achievement_progress
import com.agustin.tarati.shared.generated.resources.achievements
import com.agustin.tarati.shared.generated.resources.achievements_count
import com.agustin.tarati.shared.generated.resources.achievements_hidden_desc
import com.agustin.tarati.shared.generated.resources.achievements_hidden_title
import com.agustin.tarati.shared.generated.resources.achievements_points_total
import com.agustin.tarati.shared.generated.resources.achievements_unlocked_on
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AchievementsScreen(
    onBack: () -> Unit = {},
    viewModel: IAchievementsViewModel = koinViewModel<AchievementsViewModel>(),
) {
    val serverAchievements by viewModel.serverAchievements.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val unlockedCount = AchievementsMetadata.all.count { meta ->
        serverAchievements[meta.id.id]?.unlockedAt != null
    }
    val totalPoints = AchievementsMetadata.all
        .filter { meta -> serverAchievements[meta.id.id]?.unlockedAt != null }
        .sumOf { it.pointValue }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TaratiTopBar(
                title = stringResource(Res.string.achievements),
                navigationType = TopBarNavigationType.Back,
                onNavigationClick = onBack,
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) {
            if (isLoading && serverAchievements.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    // Summary row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.achievements_count,
                                unlockedCount,
                                AchievementsMetadata.all.size
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(Res.string.achievements_points_total, totalPoints),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        items(AchievementsMetadata.all, key = { it.id }) { meta ->
                            val server = serverAchievements[meta.id.id]
                            val isUnlocked = server?.unlockedAt != null
                            AchievementCard(
                                meta = meta,
                                isUnlocked = isUnlocked,
                                currentSteps = server?.currentSteps ?: 0,
                                unlockedAt = server?.unlockedAt,
                            )
                        }
                    }
                } // Column
            }
        }
    }
}

@Composable
private fun AchievementCard(
    meta: AchievementsMetadata.Meta,
    isUnlocked: Boolean,
    currentSteps: Int,
    unlockedAt: String?,
) {
    val showDetails = isUnlocked || !meta.isHidden
    val title = if (showDetails) stringResource(meta.titleRes)
    else stringResource(Res.string.achievements_hidden_title)
    val description = if (showDetails) stringResource(meta.descRes)
    else stringResource(Res.string.achievements_hidden_desc)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Icon
            Image(
                painter = painterResource(meta.iconRes),
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .graphicsLayer { alpha = if (isUnlocked) 1f else 0.35f },
                colorFilter = if (isUnlocked) null
                else ColorFilter.colorMatrix(
                    androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
                ),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked)
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
                if (meta.isIncremental && !isUnlocked) {
                    val progress = currentSteps.toFloat() / meta.maxSteps.toFloat()
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = stringResource(Res.string.achievement_progress, currentSteps, meta.maxSteps),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
                if (unlockedAt != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.achievements_unlocked_on, formatDate(unlockedAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

/** Convierte ISO-8601 "2026-06-18T..." a "Jun 18, 2026". */
private fun formatDate(isoDate: String): String {
    return try {
        val date = isoDate.take(10) // "2026-06-18"
        val parts = date.split("-")
        if (parts.size != 3) return isoDate
        val year = parts[0]
        val month = parts[1].toIntOrNull() ?: return isoDate
        val day = parts[2].trimStart('0').ifEmpty { "0" }
        val monthName = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        ).getOrNull(month - 1) ?: return isoDate
        "$monthName $day, $year"
    } catch (_: Exception) {
        isoDate
    }
}
