@file:OptIn(ExperimentalMaterial3Api::class)

package com.agustin.tarati.features.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.load_to_board
import com.agustin.tarati.ui.theme.TaratiBackground
import com.agustin.tarati.ui.theme.TaratiIcons
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    gameId: String,
    onImport: (MatchDto) -> Unit = {},
    onCopyMoveHistory: (MatchDto) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: IGameDetailsViewModel = koinViewModel<GameDetailsViewModel>(),
) {
    val matchDto by viewModel.currentMatchDto.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    TaratiBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                DetailsTopAppBar(
                    isEditing = isEditing,
                    gameId = gameId,
                    matchDto = matchDto,
                    onBack = onBack,
                    onCopyMoveHistory = { matchDto?.let { onCopyMoveHistory(it) } },
                    viewModel = viewModel,
                )
            },
            floatingActionButton = {
                if (!isEditing) {
                    matchDto?.let { dto ->
                        FloatingActionButton(
                            onClick = { onImport(dto) },
                        ) {
                            Icon(TaratiIcons.PlayArrow, localizedString(Res.string.load_to_board))
                        }
                    }
                }
            },
        ) { padding ->
            matchDto?.let { dto ->
                GameDetailsContent(
                    modifier = Modifier.padding(padding),
                    isEditing = isEditing,
                    matchDto = dto,
                ) { updatedMatchDto ->
                    viewModel.updateCurrentMatchDto(updatedMatchDto)
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}