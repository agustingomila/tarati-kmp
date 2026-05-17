package com.agustin.tarati.features.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.time.TimeControlMode
import com.agustin.tarati.core.domain.game.time.TimeControlPresets
import com.agustin.tarati.services.localization.localizedString
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.cancel
import com.agustin.tarati.shared.generated.resources.confirm
import com.agustin.tarati.shared.generated.resources.time_control
import com.agustin.tarati.shared.generated.resources.time_control_bronstein_label
import com.agustin.tarati.shared.generated.resources.time_control_byoyomi_label
import com.agustin.tarati.shared.generated.resources.time_control_fischer_label
import com.agustin.tarati.shared.generated.resources.time_control_reset_warning_message
import com.agustin.tarati.shared.generated.resources.time_control_reset_warning_title
import com.agustin.tarati.shared.generated.resources.time_control_subtitle
import com.agustin.tarati.shared.generated.resources.time_control_sudden_death_label
import com.agustin.tarati.shared.generated.resources.time_control_unlimited
import com.agustin.tarati.ui.theme.TaratiIcons

/**
 * Selector del modo de control de tiempo.
 *
 * Si el valor guardado no está entre los presets estándar se inserta al inicio
 * de la lista. Con [isGameActive] = `true`, cambiar el modo muestra un diálogo
 * de confirmación antes de aplicar.
 */
@Composable
fun TimeControlSetting(
    mode: TimeControlMode,
    isGameActive: Boolean = false,
    onModeSelected: (TimeControlMode) -> Unit,
) {
    SettingItem(
        icon = TaratiIcons.Timer,
        title = localizedString(Res.string.time_control),
        subtitle = localizedString(Res.string.time_control_subtitle),
    ) {
        var expanded by remember { mutableStateOf(false) }

        // Cuando el usuario elige un modo distinto durante una partida activa,
        // se guarda aquí para mostrar el diálogo. Si cancela, vuelve a null.
        var pendingMode by remember { mutableStateOf<TimeControlMode?>(null) }

        val options = remember(mode) {
            val presets = TimeControlPresets.all
            if (presets.any { it == mode }) presets
            else listOf(mode) + presets
        }

        // ── Diálogo de confirmación ───────────────────────────────────────────
        val pending = pendingMode
        if (pending != null) {
            AlertDialog(
                onDismissRequest = { pendingMode = null },
                title = { Text(localizedString(Res.string.time_control_reset_warning_title)) },
                text = { Text(localizedString(Res.string.time_control_reset_warning_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onModeSelected(pending)
                            pendingMode = null
                        },
                    ) {
                        Text(localizedString(Res.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingMode = null }) {
                        Text(localizedString(Res.string.cancel))
                    }
                },
            )
        }

        // ── Dropdown ──────────────────────────────────────────────────────────
        Box {
            Row(
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = timeControlLabel(mode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = TaratiIcons.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(timeControlLabel(option)) },
                        onClick = {
                            expanded = false
                            when {
                                option == mode -> Unit        // Sin cambio — no-op.
                                isGameActive -> pendingMode = option  // Pedir confirmación.
                                else -> onModeSelected(option)        // Aplicar directo.
                            }
                        },
                    )
                }
            }
        }
    }
}

/**
 * Convierte un [TimeControlMode] a etiqueta legible usando convenciones
 * ajedrecísticas estándar ("5+3" para 5 min base + 3 s de incremento).
 */
@Composable
fun timeControlLabel(mode: TimeControlMode): String = when (mode) {
    is TimeControlMode.Unlimited ->
        localizedString(Res.string.time_control_unlimited)

    is TimeControlMode.SuddenDeath ->
        localizedString(Res.string.time_control_sudden_death_label).replace(
            $$"%1$d",
            mode.totalMs.toMinutes().toString()
        )

    is TimeControlMode.Fischer ->
        localizedString(Res.string.time_control_fischer_label)
            .replace($$"%1$d", mode.baseMs.toMinutes().toString())
            .replace($$"%2$d", mode.incrementMs.toSeconds().toString())

    is TimeControlMode.Bronstein ->
        localizedString(Res.string.time_control_bronstein_label)
            .replace($$"%1$d", mode.baseMs.toMinutes().toString())
            .replace($$"%2$d", mode.delayMs.toSeconds().toString())

    is TimeControlMode.Byoyomi ->
        localizedString(Res.string.time_control_byoyomi_label)
            .replace($$"%1$d", mode.baseMs.toMinutes().toString())
            .replace($$"%2$d", mode.periodMs.toMinutes().toString())
            .replace($$"%3$d", mode.periods.toString())
}

fun Long.toMinutes(): Long = this / 60_000L
fun Long.toSeconds(): Long = this / 1_000L