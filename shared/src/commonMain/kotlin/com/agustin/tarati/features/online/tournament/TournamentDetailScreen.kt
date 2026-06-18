package com.agustin.tarati.features.online.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agustin.tarati.features.online.auth.IAuthViewModel
import com.agustin.tarati.features.online.game.IOnlineGameViewModel
import com.agustin.tarati.network.models.TournamentDetailDto
import com.agustin.tarati.network.models.TournamentGameStatus
import com.agustin.tarati.network.models.TournamentPairingDto
import com.agustin.tarati.network.models.TournamentRoundDto
import com.agustin.tarati.network.models.TournamentStandingDto
import com.agustin.tarati.network.models.TournamentStatus
import com.agustin.tarati.network.models.TournamentType
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel_tournament
import com.agustin.tarati.shared.generated.resources.casual
import com.agustin.tarati.shared.generated.resources.fixture
import com.agustin.tarati.shared.generated.resources.rated
import com.agustin.tarati.shared.generated.resources.standings
import com.agustin.tarati.shared.generated.resources.tournament
import com.agustin.tarati.shared.generated.resources.tournament_active_status
import com.agustin.tarati.shared.generated.resources.tournament_cancelled_status
import com.agustin.tarati.shared.generated.resources.tournament_created_by
import com.agustin.tarati.shared.generated.resources.tournament_finished_status
import com.agustin.tarati.shared.generated.resources.tournament_players_of
import com.agustin.tarati.shared.generated.resources.tournament_register
import com.agustin.tarati.shared.generated.resources.tournament_round_n
import com.agustin.tarati.shared.generated.resources.tournament_round_progress
import com.agustin.tarati.shared.generated.resources.tournament_start
import com.agustin.tarati.shared.generated.resources.tournament_status_active
import com.agustin.tarati.shared.generated.resources.tournament_status_cancelled
import com.agustin.tarati.shared.generated.resources.tournament_status_finished
import com.agustin.tarati.shared.generated.resources.tournament_status_registering
import com.agustin.tarati.shared.generated.resources.tournament_type_arena
import com.agustin.tarati.shared.generated.resources.tournament_type_round_robin
import com.agustin.tarati.shared.generated.resources.tournament_type_swiss
import com.agustin.tarati.shared.generated.resources.tournament_unregister
import com.agustin.tarati.shared.generated.resources.watch_game
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.layout.CompanionPanelHeader
import com.agustin.tarati.ui.layout.DisplayMode
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.TaratiIcons
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Pantalla de detalle de un torneo.
 *
 * Muestra la información completa del torneo: estado, standings en tiempo real,
 * rondas y emparejamientos con su estado (pendiente / en curso / completado),
 * y los controles de acción (inscribirse, iniciar, etc.).
 *
 * Las actualizaciones en tiempo real llegan vía [TournamentViewModel] que escucha
 * [IOnlineGameViewModel.tournamentEvents].
 *
 * @param tournamentId       ID del torneo a mostrar.
 * @param onBack             Callback de navegación hacia atrás.
 * @param displayMode        FullScreen o CompanionPanel — controla el tipo de top bar.
 * @param onSpectateGame     Callback al tocar una partida en curso del fixture. Null oculta el botón.
 * @param onNavigateToGameDetails Callback al tocar una partida terminada del fixture. Null oculta el tap.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    onBack: () -> Unit,
    displayMode: DisplayMode = DisplayMode.FullScreen,
    onSpectateGame: ((gameId: String) -> Unit)? = null,
    onNavigateToGameDetails: ((gameId: String) -> Unit)? = null,
    authViewModel: IAuthViewModel = koinInject(),
    onlineGameViewModel: IOnlineGameViewModel = koinInject(),
    viewModel: ITournamentViewModel = koinViewModel<TournamentViewModel>(),
) {
    val state by viewModel.detailState.collectAsState()
    val scope = rememberCoroutineScope()
    val token = authViewModel.accessToken ?: authViewModel.getStoredToken()
    val currentUserId = authViewModel.currentUser?.userId

    LaunchedEffect(tournamentId, token) {
        if (token != null) viewModel.loadTournament(token, tournamentId)
    }

    TaratiBackground {
        Scaffold(
            topBar = {
                when (displayMode) {
                    DisplayMode.FullScreen -> TaratiTopBar(
                        title = state.tournament?.name ?: localizedString(Res.string.tournament),
                        navigationType = TopBarNavigationType.Back,
                        onNavigationClick = onBack,
                    )

                    DisplayMode.CompanionPanel -> CompanionPanelHeader(
                        title = state.tournament?.name ?: localizedString(Res.string.tournament),
                        onClose = onBack,
                    )
                }
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
                    onCancel = {
                        if (token != null) scope.launch { viewModel.cancel(token, tournamentId) }
                    },
                    onRefresh = {
                        if (token != null) viewModel.loadTournament(token, tournamentId)
                    },
                    onSpectateGame = if (onSpectateGame != null) { gameId ->
                        scope.launch {
                            val ok = onlineGameViewModel.spectateGame(gameId)
                            if (ok) {
                                onSpectateGame(gameId)
                            } else {
                                // La partida terminó entre el fixture load y el tap.
                                // Recargar el fixture y abrir detalles si es posible.
                                if (token != null) viewModel.loadTournament(token, tournamentId)
                                onNavigateToGameDetails?.invoke(gameId)
                            }
                        }
                    } else null,
                    onNavigateToGameDetails = onNavigateToGameDetails,
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
    onCancel: () -> Unit,
    onRefresh: () -> Unit,
    onSpectateGame: ((gameId: String) -> Unit)?,
    onNavigateToGameDetails: ((gameId: String) -> Unit)?,
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
                onCancel = onCancel,
            )
        }

        // ── Standings ─────────────────────────────────────────────────────────
        if (tournament.standings.isNotEmpty()) {
            item {
                Text(
                    localizedString(Res.string.standings),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            itemsIndexed(tournament.standings) { _, standing ->
                StandingRow(standing, isSwiss = tournament.type == TournamentType.SWISS)
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
            }
        }

        // ── Fixture (rondas) ──────────────────────────────────────────────────
        if (tournament.rounds.isNotEmpty()) {
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    localizedString(Res.string.fixture),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            tournament.rounds.forEach { round ->
                item {
                    RoundSection(
                        round = round,
                        isCurrent = round.roundNumber == tournament.currentRound &&
                                tournament.status == TournamentStatus.ACTIVE,
                        onSpectateGame = onSpectateGame,
                        onNavigateToGameDetails = onNavigateToGameDetails,
                    )
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
                if (tournament.isRated) localizedString(Res.string.rated) else localizedString(Res.string.casual),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        val typeLabel = when (tournament.type) {
            TournamentType.ROUND_ROBIN -> localizedString(Res.string.tournament_type_round_robin)
            TournamentType.SWISS -> localizedString(Res.string.tournament_type_swiss)
            TournamentType.ARENA -> localizedString(Res.string.tournament_type_arena)
        }
        Text(
            "$typeLabel · ${
                localizedString(Res.string.tournament_players_of)
                    .replace($$"%1$d", "${tournament.standings.size}")
                    .replace($$"%2$d", "${tournament.maxPlayers}")
            }",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (tournament.status == TournamentStatus.ACTIVE) {
            Text(
                localizedString(Res.string.tournament_round_progress)
                    .replace($$"%1$d", "${tournament.currentRound}")
                    .replace($$"%2$d", "${tournament.totalRounds}"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
        Text(
            localizedString(Res.string.tournament_created_by).replace($$"%1$s", tournament.creatorUsername),
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
    onCancel: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        when (tournament.status) {
            TournamentStatus.REGISTERING -> {
                if (isCreator && tournament.standings.size >= tournament.minPlayers) {
                    Button(onClick = onStart) { Text(localizedString(Res.string.tournament_start)) }
                }
                if (!isParticipant && tournament.standings.size < tournament.maxPlayers) {
                    Button(onClick = onRegister) { Text(localizedString(Res.string.tournament_register)) }
                } else if (isParticipant) {
                    OutlinedButton(onClick = onUnregister) { Text(localizedString(Res.string.tournament_unregister)) }
                }
                if (isCreator) {
                    OutlinedButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) { Text(localizedString(Res.string.cancel_tournament)) }
                }
            }

            TournamentStatus.ACTIVE -> {
                Text(
                    localizedString(Res.string.tournament_active_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                )
            }

            TournamentStatus.FINISHED -> {
                Text(
                    localizedString(Res.string.tournament_finished_status),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            TournamentStatus.CANCELLED -> {
                Text(
                    localizedString(Res.string.tournament_cancelled_status),
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
        TournamentStatus.REGISTERING -> localizedString(Res.string.tournament_status_registering) to MaterialTheme.colorScheme.secondaryContainer
        TournamentStatus.ACTIVE -> localizedString(Res.string.tournament_status_active) to MaterialTheme.colorScheme.primaryContainer
        TournamentStatus.FINISHED -> localizedString(Res.string.tournament_status_finished) to MaterialTheme.colorScheme.surfaceVariant
        TournamentStatus.CANCELLED -> localizedString(Res.string.tournament_status_cancelled) to MaterialTheme.colorScheme.errorContainer
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
                "${formatTournamentScore(standing.score)} pts",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (isSwiss) {
                Text(
                    "BH ${formatTournamentScore(standing.buchholz)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RoundSection(
    round: TournamentRoundDto,
    isCurrent: Boolean,
    onSpectateGame: ((gameId: String) -> Unit)?,
    onNavigateToGameDetails: ((gameId: String) -> Unit)?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                localizedString(Res.string.tournament_round_n).replace($$"%1$d", "${round.roundNumber}"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
            }
        }
        round.pairings.forEach { pairing ->
            PairingRow(
                pairing = pairing,
                onSpectate = if (pairing.status == TournamentGameStatus.ACTIVE && pairing.gameId != null && onSpectateGame != null) {
                    { onSpectateGame(pairing.gameId) }
                } else null,
                onViewCompleted = if (pairing.status == TournamentGameStatus.COMPLETED && pairing.gameId != null && onNavigateToGameDetails != null) {
                    { onNavigateToGameDetails(pairing.gameId) }
                } else null,
            )
        }
    }
}

@Composable
private fun PairingRow(
    pairing: TournamentPairingDto,
    onSpectate: (() -> Unit)?,
    onViewCompleted: (() -> Unit)?,
) {
    val isActive = pairing.status == TournamentGameStatus.ACTIVE
    val isPending = pairing.status == TournamentGameStatus.PENDING
    val isCompleted = pairing.result != null

    val dimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    val whiteWon = pairing.result == "white_wins"
    val blackWon = pairing.result == "black_wins"

    val rowModifier = when {
        isActive && onSpectate != null -> Modifier
            .fillMaxWidth()
            .clickable(onClick = onSpectate)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)

        isCompleted && onViewCompleted != null -> Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewCompleted)
            .padding(horizontal = 4.dp, vertical = 3.dp)

        else -> Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 3.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // ── Jugador blancas ──────────────────────────────────────────────────
        Text(
            pairing.whiteUsername,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (whiteWon) FontWeight.Bold else FontWeight.Normal,
            color = if (isPending) dimColor else MaterialTheme.colorScheme.onSurface,
        )

        // ── Resultado / estado ───────────────────────────────────────────────
        when {
            isCompleted -> {
                val label = when (pairing.result) {
                    "white_wins" -> "1 - 0"
                    "black_wins" -> "0 - 1"
                    "draw" -> "½ - ½"
                    else -> "?"
                }
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            isActive -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                    Text(
                        localizedString(Res.string.tournament_status_active),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                    )
                    if (onSpectate != null) {
                        Icon(
                            TaratiIcons.Visibility,
                            contentDescription = localizedString(Res.string.watch_game),
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            else -> {
                Text(
                    "vs",
                    style = MaterialTheme.typography.labelSmall,
                    color = dimColor,
                )
            }
        }

        // ── Jugador negras ───────────────────────────────────────────────────
        Text(
            pairing.blackUsername,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (blackWon) FontWeight.Bold else FontWeight.Normal,
            color = if (isPending) dimColor else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/**
 * Convierte medios puntos (almacenamiento interno 2-1-0) a notación estándar 1-½-0.
 * 0→"0"  1→"½"  2→"1"  3→"1½"  4→"2"  ...
 */
private fun formatTournamentScore(halfPoints: Int): String {
    val whole = halfPoints / 2
    val hasHalf = halfPoints % 2 != 0
    return when {
        !hasHalf -> whole.toString()
        whole == 0 -> "½"
        else -> "$whole½"
    }
}
