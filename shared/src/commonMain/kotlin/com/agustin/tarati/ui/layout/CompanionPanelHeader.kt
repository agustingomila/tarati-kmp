package com.agustin.tarati.ui.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.close
import com.agustin.tarati.ui.components.TooltipIconButton
import com.agustin.tarati.ui.theme.TaratiIcons

/**
 * Header slim para pantallas embebidas en el panel lateral.
 *
 * Diseñado para usarse en el slot `topBar` de un [Scaffold] cuando
 * [DisplayMode] es [DisplayMode.CompanionPanel]. Reemplaza a [TaratiTopBar]
 * con un header más compacto que incluye el botón de cierre del panel.
 *
 * @param title   Título de la pantalla.
 * @param onClose Acción del botón ✕ — normalmente [CompanionPanelController.close] o [CompanionPanelController.back].
 * @param actions Iconos de acción adicionales — los mismos que la pantalla mostraría en su TopBar.
 */
@Composable
fun CompanionPanelHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            actions()
            TooltipIconButton(
                tooltip = localizedString(Res.string.close),
                onClick = onClose,
            ) {
                Icon(
                    imageVector = TaratiIcons.Close,
                    contentDescription = localizedString(Res.string.close),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        HorizontalDivider()
    }
}
