@file:OptIn(ExperimentalMaterial3Api::class)

package com.agustin.tarati.features.detail

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.copy_move_history
import com.agustin.tarati.shared.generated.resources.edit
import com.agustin.tarati.shared.generated.resources.game_details
import com.agustin.tarati.shared.generated.resources.save
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
import com.agustin.tarati.ui.layout.CompanionPanelHeader
import com.agustin.tarati.ui.layout.DisplayMode
import com.agustin.tarati.ui.theme.TaratiIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun DetailsTopAppBar(
    isEditing: Boolean,
    gameId: String,
    matchDto: MatchDto?,
    onBack: () -> Unit,
    onCopyMoveHistory: () -> Unit,
    viewModel: IGameDetailsViewModel,
    displayMode: DisplayMode = DisplayMode.FullScreen,
) {
    val actions: @Composable RowScope.() -> Unit = {
        if (!isEditing) {
            IconButton(onClick = onCopyMoveHistory) {
                Icon(
                    imageVector = TaratiIcons.ContentCopy,
                    contentDescription = localizedString(Res.string.copy_move_history),
                )
            }
        }
        IconButton(
            onClick = {
                if (isEditing) {
                    matchDto?.let { dto -> viewModel.saveGame(dto) }
                } else {
                    viewModel.setEditing(true)
                }
            },
        ) {
            Icon(
                imageVector = if (isEditing) TaratiIcons.Save else TaratiIcons.Edit,
                contentDescription = if (isEditing) localizedString(Res.string.save) else localizedString(Res.string.edit),
            )
        }
        if (isEditing) {
            IconButton(
                onClick = {
                    viewModel.setEditing(false)
                    viewModel.loadGame(gameId)
                },
            ) {
                Icon(
                    imageVector = TaratiIcons.Close,
                    contentDescription = localizedString(Res.string.cancel),
                )
            }
        }
    }
    when (displayMode) {
        DisplayMode.FullScreen -> TaratiTopBar(
            title = stringResource(Res.string.game_details),
            navigationType = TopBarNavigationType.Back,
            onNavigationClick = onBack,
            actions = actions,
        )
        DisplayMode.CompanionPanel -> CompanionPanelHeader(
            title = stringResource(Res.string.game_details),
            onClose = onBack,
            actions = actions,
        )
    }
}