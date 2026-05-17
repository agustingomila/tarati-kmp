package com.agustin.tarati.features.game

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.agustin.tarati.services.localization.LocalizedText
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.undo_anyway
import com.agustin.tarati.shared.generated.resources.undo_warning_message
import com.agustin.tarati.shared.generated.resources.undo_warning_title
import com.agustin.tarati.ui.theme.TaratiIcons

/**
 * Dialog de advertencia que informa al usuario que usar Undo deshabilita los
 * logros para la partida en curso.
 *
 * Se muestra **la primera vez por partida** que el usuario intenta deshacer un
 * movimiento, tanto desde el [BottomGameBar]
 * como desde el Sidebar. El estado `hasWarnedAboutUndo` que controla cuándo
 * mostrarlo reside en [GameScreen], que actúa como fuente de verdad única y
 * coordina ambos puntos de entrada.
 *
 * @param onConfirm Ejecuta el undo real y marca `hasWarnedAboutUndo = true`.
 * @param onDismiss Cierra el dialog sin deshacer ningún movimiento.
 */
@Composable
fun UndoWarningDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = TaratiIcons.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = { Text(localizedString(Res.string.undo_warning_title)) },
        text = { Text(localizedString(Res.string.undo_warning_message)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(localizedString(Res.string.undo_anyway))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                LocalizedText(Res.string.cancel)
            }
        },
    )
}