package com.agustin.tarati.services.localization

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * CompositionLocal que provee el código de idioma actual de la aplicación.
 *
 * ## KMP - Usar String en lugar de Locale
 * En Kotlin Multiplatform commonMain, java.util.Locale no está disponible.
 * Usamos String con códigos de idioma: "es", "en", etc.
 *
 * ## Cambio reactivo de idioma
 * Cuando el usuario cambia el idioma en Settings, LanguageAwareApp actualiza
 * este CompositionLocal con el nuevo código. Todos los composables que observan
 * LocalAppLanguage.current se recomponen automáticamente.
 */
val LocalAppLanguage = compositionLocalOf { "en" }

/**
 * Composable hoja que renderiza un string localizado como [Text].
 *
 * ## Recomposición reactiva al cambio de idioma
 * Usa `key(LocalAppLanguage.current)` para forzar la recomposición
 * cuando cambia el idioma en Settings.
 */
@NonRestartableComposable
@Composable
fun LocalizedText(
    resource: StringResource,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
    vararg args: Any,
) {
    val currentLanguage = LocalAppLanguage.current

    key(currentLanguage) {
        Text(
            text = if (args.isEmpty()) {
                stringResource(resource)
            } else {
                stringResource(resource, *args)
            },
            modifier = modifier,
            style = style,
            color = color,
            textAlign = textAlign,
        )
    }
}

/**
 * Resuelve un string localizado desde Compose Multiplatform Resources.
 */
@NonRestartableComposable
@Composable
fun localizedString(
    resource: StringResource,
    vararg args: Any,
): String {
    val currentLanguage = LocalAppLanguage.current

    return key(currentLanguage) {
        if (args.isEmpty()) {
            stringResource(resource)
        } else {
            stringResource(resource, *args)
        }
    }
}