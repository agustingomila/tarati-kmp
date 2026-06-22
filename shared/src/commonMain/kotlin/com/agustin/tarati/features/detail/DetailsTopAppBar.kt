@file:OptIn(ExperimentalMaterial3Api::class)

package com.agustin.tarati.features.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.copy_move_history
import com.agustin.tarati.shared.generated.resources.edit
import com.agustin.tarati.shared.generated.resources.game_details
import com.agustin.tarati.shared.generated.resources.save
import com.agustin.tarati.ui.components.TooltipIconButton
import com.agustin.tarati.ui.components.topbar.TaratiTopBar
import com.agustin.tarati.ui.components.topbar.TopBarNavigationType
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
) {
    TaratiTopBar(
        title = stringResource(Res.string.game_details),
        navigationType = TopBarNavigationType.Back,
        onNavigationClick = onBack,
        actions = {
            // Botón de copiar historial de movimientos
            if (!isEditing) {
                TooltipIconButton(
                    tooltip = localizedString(Res.string.copy_move_history),
                    onClick = onCopyMoveHistory,
                ) {
                    Icon(
                        imageVector = TaratiIcons.ContentCopy,
                        contentDescription = localizedString(Res.string.copy_move_history),
                    )
                }
            }

            // Botón de edición/guardar
            val editSaveLabel = if (isEditing) localizedString(Res.string.save) else localizedString(Res.string.edit)
            TooltipIconButton(
                tooltip = editSaveLabel,
                onClick = {
                    if (isEditing) {
                        // Guardar cambios
                        matchDto?.let { dto ->
                            viewModel.saveGame(dto)
                        }
                    } else {
                        viewModel.setEditing(true)
                    }
                },
            ) {
                Icon(
                    imageVector = if (isEditing) TaratiIcons.Save else TaratiIcons.Edit,
                    contentDescription = editSaveLabel,
                )
            }

            // Botón de cancelar edición
            if (isEditing) {
                TooltipIconButton(
                    tooltip = localizedString(Res.string.cancel),
                    onClick = {
                        viewModel.setEditing(false)
                        // Recargar datos originales
                        viewModel.loadGame(gameId)
                    },
                ) {
                    Icon(
                        imageVector = TaratiIcons.Close,
                        contentDescription = localizedString(Res.string.cancel),
                    )
                }
            }
        },
    )
}