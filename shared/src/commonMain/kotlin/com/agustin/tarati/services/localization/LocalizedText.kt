package com.agustin.tarati.services.localization

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ProvidableCompositionLocal
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
val LocalAppLanguage: ProvidableCompositionLocal<String> = compositionLocalOf { "en" }

/**
 * Normaliza espacios en blanco y procesa escapes XML en strings localizados.
 *
 * ## Uso
 * Se aplica automáticamente en [LocalizedText] y [localizedString], por lo que
 * no necesita ser llamada manualmente. Todos los strings localizados pasan por
 * esta normalización de forma transparente.
 *
 * @return String con espacios normalizados y escapes procesados
 */
fun String.normalizeWhitespace(): String {
    // 1. Preservar \n literales reemplazándolos temporalmente
    val withPlaceholder = this.replace("\\n", "\uE000")

    // 2. Colapsar TODOS los espacios en blanco en uno solo
    val normalized = withPlaceholder.replace("\\s+".toRegex(), " ").trim()

    // 3. Eliminar espacios alrededor del placeholder
    val cleanedPlaceholder = normalized.replace("\\s*\uE000\\s*".toRegex(), "\uE000")

    // 4. Restaurar \n literales como newlines reales
    val withNewlines = cleanedPlaceholder.replace("\uE000", "\n")

    // 5. Procesar escapes XML comunes (ORDEN CRÍTICO: backslash primero)
    return withNewlines
        .replace("\\\\", "\\")    // Backslash escapado (PRIMERO)
        .replace("\\'", "'")      // Comilla simple escapada
        .replace("\\\"", "\"")    // Comilla doble escapada
}

/**
 * Composable hoja que renderiza un string localizado como [Text].
 *
 * ## Recomposición reactiva al cambio de idioma
 * Usa `key(LocalAppLanguage.current)` para forzar la recomposición
 * cuando cambia el idioma en Settings.
 *
 * ## Normalización automática de espacios
 * Los strings se normalizan automáticamente para colapsar múltiples espacios
 * causados por el formateo del IDE en los archivos XML.
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
        val rawText = if (args.isEmpty()) {
            stringResource(resource)
        } else {
            stringResource(resource, *args)
        }

        Text(
            text = rawText.normalizeWhitespace(),
            modifier = modifier,
            style = style,
            color = color,
            textAlign = textAlign,
        )
    }
}

/**
 * Resuelve un string localizado desde Compose Multiplatform Resources.
 *
 * ## Normalización automática de espacios
 * Los strings se normalizan automáticamente para colapsar múltiples espacios
 * causados por el formateo del IDE en los archivos XML.
 */
@NonRestartableComposable
@Composable
fun localizedString(
    resource: StringResource,
    vararg args: Any,
): String {
    val currentLanguage = LocalAppLanguage.current

    return key(currentLanguage) {
        val rawText = if (args.isEmpty()) {
            stringResource(resource)
        } else {
            stringResource(resource, *args)
        }
        rawText.normalizeWhitespace()
    }
}