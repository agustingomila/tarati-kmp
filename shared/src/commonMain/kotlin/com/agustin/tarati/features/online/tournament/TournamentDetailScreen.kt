package com.agustin.tarati.features.online.tournament

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.network.models.TournamentDetailDto
import com.agustin.tarati.network.models.TournamentPairingDto
import com.agustin.tarati.network.models.TournamentRoundDto
import com.agustin.tarati.network.models.TournamentStandingDto
import com.agustin.tarati.network.models.TournamentStatus
import com.agustin.tarati.network.models.TournamentType
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.theme.TaratiBackground
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla de detalle de un torneo.
 *
 * Muestra la información completa del torneo: estado, standings en tiempo real,
 * rondas y emparejamientos, y los controles de acción (inscribirse, iniciar, etc.).
 *
 * Las actualizaciones en tiempo real llegan vía [TournamentViewModel] que escucha
 * [IOnlineGameViewModel.tournamentEvents]. La pantalla también puede recargarse
 * manualmente.
 *
 * @param tournamentId ID del torneo a mostrar.
 * @param onBack       Callback de navegación hacia atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    onBack: () -> Unit,
    authViewModel: IAuthViewModel = koinInject(),
    viewModel: ITournamentViewModel = koinViewModel<TournamentViewModel>(),
) {
    val state by viewModel.detailState.collectAsState()
    val scope = rememberCoroutineScope()
    val token = authViewModel.getStoredToken()
    val currentUserId = authViewModel.currentUser?.userId

    LaunchedEffect(tournamentId) {
        if (token != null) viewModel.loadTournament(token, tournamentId)
    }

    TaratiBackground {
        Scaffold(
            topBar = {
                TaratiTopBar(
                    title = state.tournament?.name ?: "Torneo",
                    navigationType = TopBarNavigationType.Back,
                    onNavigationClick = onBack,
                )
            }
        ) { padding ->
            when {
                state.isLoading && state.tournament == null -> Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                state.error != null && state.tournament == null -> Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) { Text(state.error!!, color = MaterialTheme.colorScheme.error) }

                state.tournament != null -> TournamentDetailContent(
                    tournament = state.tournament!!,
                    currentUserId = currentUserId,
                    contentPadding = padding,
                    onRegister = {
                        if (token != null) scope.launch { viewModel.register(token, tournamentId) }
                    },
                    onUnregister = {
                        if (token != null) scope.launch { viewModel.unregister(token, tournamentId) }
                    },
                    onStart = {
                        if (token != null) scope.launch { viewModel.start(token, tournamentId) }
                    },
                    onRefresh = {
                        if (token != null) viewModel.loadTournament(token, tournamentId)
                    },
                )
            }
        }
    }
}

// ── Contenido principal ────────────────────────────────────────────────────────

@Composable
private fun TournamentDetailContent(
    tournament: TournamentDetailDto,
    currentUserId: String?,
    contentPadding: PaddingValues,
    onRegister: () -> Unit,
    onUnregister: () -> Unit,
    onStart: () -> Unit,
    onRefresh: () -> Unit,
) {
    val isParticipant = tournament.standings.any { it.userId == currentUserId }
    val isCreator = tournament.creatorId == currentUserId

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        item {
            TournamentHeader(tournament)
        }

        // ── Acciones ──────────────────────────────────────────────────────────
        item {
            TournamentActions(
                tournament = tournament,
                isParticipant = isParticipant,
                isCreator = isCreator,
                onRegister = onRegister,
                onUnregister = onUnregister,
                onStart = onStart,
            )
        }

        // ── Standings ─────────────────────────────────────────────────────────
        if (tournament.standings.isNotEmpty()) {
            item {
                Text(
                    "Clasificación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            itemsIndexed(tournament.standings) { _, standing ->
                StandingRow(standing, isSwiss = tournament.type == TournamentType.SWISS)
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
            }
        }

        // ── Rondas ────────────────────────────────────────────────────────────
        if (tournament.rounds.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Rondas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            tournament.rounds.forEach { round ->
                item {
                    RoundSection(round)
                }
            }
        }
    }
}

// ── Composables auxiliares ─────────────────────────────────────────────────────

@Composable
private fun TournamentHeader(tournament: TournamentDetailDto) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusBadge(tournament.status)
            Text(
                tournament.timeControl.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "·",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                if (tournament.isRated) "Rated" else "Casual",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            "${tournament.type.name.replace('_', ' ')} · ${tournament.standings.size}/${tournament.maxPlayers} jugadores",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (tournament.status == TournamentStatus.ACTIVE) {
            Text(
                "Ronda ${tournament.currentRound} de ${tournament.totalRounds}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
        Text(
            "Creado por ${tournament.creatorUsername}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TournamentActions(
    tournament: TournamentDetailDto,
    isParticipant: Boolean,
    isCreator: Boolean,
    onRegister: () -> Unit,
    onUnregister: () -> Unit,
    onStart: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        when (tournament.status) {
            TournamentStatus.REGISTERING -> {
                if (isCreator && tournament.standings.size >= tournament.minPlayers) {
                    Button(onClick = onStart) { Text("Iniciar torneo") }
                }
                if (!isParticipant && tournament.standings.size < tournament.maxPlayers) {
                    Button(onClick = onRegister) { Text("Inscribirse") }
                } else if (isParticipant) {
                    OutlinedButton(onClick = onUnregister) { Text("Desinscribirse") }
                }
            }
            TournamentStatus.ACTIVE -> {
                Text(
                    "Torneo en curso",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                )
            }
            TournamentStatus.FINISHED -> {
                Text(
                    "Torneo finalizado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TournamentStatus.CANCELLED -> {
                Text(
                    "Torneo cancelado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TournamentStatus) {
    val (label, containerColor) = when (status) {
        TournamentStatus.REGISTERING -> "Inscripciones" to MaterialTheme.colorScheme.secondaryContainer
        TournamentStatus.ACTIVE -> "En curso" to MaterialTheme.colorScheme.primaryContainer
        TournamentStatus.FINISHED -> "Finalizado" to MaterialTheme.colorScheme.surfaceVariant
        TournamentStatus.CANCELLED -> "Cancelado" to MaterialTheme.colorScheme.errorContainer
    }
    Badge(containerColor = containerColor) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun StandingRow(standing: TournamentStandingDto, isSwiss: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Rank
        Text(
            "#${standing.rank}",
            modifier = Modifier.width(36.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (standing.rank <= 3) FontWeight.Bold else FontWeight.Normal,
            color = when (standing.rank) {
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.secondary
                3 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurface
            },
        )
        // Nombre + rating
        Column(modifier = Modifier.weight(1f)) {
            Text(standing.username, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${standing.rating}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        // W/D/L
        Text(
            "${standing.wins}W ${standing.draws}D ${standing.losses}L",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(8.dp))
        // Puntos (+ buchholz para Swiss)
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${standing.score} pts",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (isSwiss) {
                Text(
                    "BH ${standing.buchholz}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RoundSection(round: TournamentRoundDto) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "Ronda ${round.roundNumber}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        round.pairings.forEach { pairing ->
            PairingRow(pairing)
        }
    }
}

@Composable
private fun PairingRow(pairing: TournamentPairingDto) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            pairing.whiteUsername,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
        )
        val resultLabel = when (pairing.result) {
            "white_wins" -> "1 - 0"
            "black_wins" -> "0 - 1"
            "draw" -> "½ - ½"
            else -> when (pairing.status) {
                "active" -> "en curso"
                else -> "pendiente"
            }
        }
        Text(
            resultLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            pairing.blackUsername,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
