package com.agustin.tarati.features.detail.previews

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.data.database.dto.MatchDto
import com.agustin.tarati.core.data.database.dto.PGNHeader
import com.agustin.tarati.features.detail.CollapsibleMoveHistoryCard
import com.agustin.tarati.features.detail.GameDetailsScreen
import com.agustin.tarati.features.detail.GameInfoCard
import com.agustin.tarati.features.detail.IGameDetailsViewModel
import com.agustin.tarati.features.game.previews.previewRandomFinalGameState
import com.agustin.tarati.features.game.previews.previewRandomMidGameState
import com.agustin.tarati.features.library.previews.previewGameDto
import com.agustin.tarati.ui.theme.BoardPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.TaratiTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ══════════════════════════════════════════════════════════════════════════════
// Datos de preview
// ══════════════════════════════════════════════════════════════════════════════

private val richHeader = PGNHeader(
    white = "Agustín",
    black = "IA (Difícil)",
    result = "1-0",
    date = "2024.08.15",
    event = "Partida Casual",
    site = "La Plata, Argentina",
    round = "3",
    gameType = "Clásico",
    rules = "Estándar",
    timeControl = "10+5",
    termination = "Normal",
    observations = "Jugada táctica brillante en el movimiento 23 que forzó la rendición.",
)

private val richMatchDto = MatchDto(
    id = "preview-rich-1",
    header = richHeader,
    game = previewGameDto,
)

// ══════════════════════════════════════════════════════════════════════════════
// ViewModel de preview
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Construye un [MatchDto] con posición de tablero aleatoria.
 * Reutiliza el header y el historial de [richMatchDto] pero reemplaza
 * [boardPosition] con una posición aleatoria de final de partida.
 */
fun randomFinalMatchDto(): MatchDto = richMatchDto.copy(
    game = previewGameDto.copy(
        boardPosition = previewRandomFinalGameState.toPositionNotation(),
    ),
)

/**
 * Construye un [MatchDto] con posición de tablero aleatoria.
 * Reutiliza el header y el historial de [richMatchDto] pero reemplaza
 * [boardPosition] con una posición aleatoria de mitad de partida.
 */
fun randomMiddleMatchDto(): MatchDto = richMatchDto.copy(
    game = previewGameDto.copy(
        boardPosition = previewRandomMidGameState.toPositionNotation(),
    ),
)

class PreviewGameDetailsViewModel(
    matchDto: MatchDto? = randomMiddleMatchDto(),
    editing: Boolean = false,
) : IGameDetailsViewModel {
    override val currentMatchDto: StateFlow<MatchDto?> = MutableStateFlow(matchDto).asStateFlow()
    override val isEditing: StateFlow<Boolean> = MutableStateFlow(editing).asStateFlow()
    override fun loadGame(gameId: String) {}
    override fun saveGame(matchDto: MatchDto) {}
    override fun setEditing(editing: Boolean) {}
    override fun updateCurrentMatchDto(matchDto: MatchDto) {}
}

// ══════════════════════════════════════════════════════════════════════════════
// Composables compartidos
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun InfoCardPreview(
    palette: BoardPalette = ClassicPalette,
    darkTheme: Boolean = false,
    isEditing: Boolean = false,
    header: PGNHeader = richHeader,
) {
    TaratiTheme(darkTheme = darkTheme, palette = palette) {
        Surface {
            GameInfoCard(
                modifier = Modifier.padding(16.dp),
                isEditing = isEditing,
                header = header,
            )
        }
    }
}

@Composable
private fun MoveHistoryPreview(
    palette: BoardPalette = ClassicPalette,
    darkTheme: Boolean = false,
    isExpanded: Boolean = true,
    currentMoveIndex: Int = previewGameDto.moveHistory.lastIndex,
) {
    TaratiTheme(darkTheme = darkTheme, palette = palette) {
        Surface {
            CollapsibleMoveHistoryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                gameDto = previewGameDto,
                isExpanded = isExpanded,
                currentMoveIndex = currentMoveIndex,
            )
        }
    }
}

@Composable
fun GameDetailScreenPreview(
    palette: BoardPalette = ClassicPalette,
    darkTheme: Boolean = false,
    viewModel: IGameDetailsViewModel = PreviewGameDetailsViewModel(),
) {
    TaratiTheme(darkTheme = darkTheme, palette = palette) {
        GameDetailsScreen(gameId = "preview-1", viewModel = viewModel)
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// Grupo 1 — GameInfoCard
// ══════════════════════════════════════════════════════════════════════════════

@Preview(group = "InfoCard", name = "Datos completos", showBackground = true, widthDp = 411)
@Composable
private fun InfoCard_RichData() = InfoCardPreview()

@Preview(group = "InfoCard", name = "Datos mínimos", showBackground = true, widthDp = 411)
@Composable
private fun InfoCard_MinimalData() =
    InfoCardPreview(header = PGNHeader(white = "Humano", black = "IA"))

@Preview(group = "InfoCard", name = "Modo edición", showBackground = true, widthDp = 411)
@Composable
private fun InfoCard_Editing() = InfoCardPreview(isEditing = true)

@Preview(group = "InfoCard", name = "Oscuro", showBackground = true, widthDp = 411, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun InfoCard_Dark() = InfoCardPreview(darkTheme = true)

// ══════════════════════════════════════════════════════════════════════════════
// Grupo 2 — CollapsibleMoveHistoryCard
// ══════════════════════════════════════════════════════════════════════════════

@Preview(group = "MoveHistory", name = "Colapsado", showBackground = true, widthDp = 411)
@Composable
private fun MoveHistory_Collapsed() = MoveHistoryPreview(isExpanded = false)

@Preview(group = "MoveHistory", name = "Expandido", showBackground = true, widthDp = 411)
@Composable
private fun MoveHistory_Expanded() =
    MoveHistoryPreview(currentMoveIndex = previewGameDto.moveHistory.lastIndex / 2)

@Preview(
    group = "MoveHistory",
    name = "Expandido — Oscuro",
    showBackground = true,
    widthDp = 411,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun MoveHistory_Dark() = MoveHistoryPreview(darkTheme = true, currentMoveIndex = 0)

// ══════════════════════════════════════════════════════════════════════════════
// Grupo 3 — GameDetailsScreen · Portrait
// ══════════════════════════════════════════════════════════════════════════════

@Preview(
    group = "Screen · Portrait",
    name = "Classic",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5
)
@Composable
private fun Screen_Portrait_Classic() = GameDetailScreenPreview()

@Preview(
    group = "Screen · Portrait",
    name = "Dark palette",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5
)
@Composable
private fun Screen_Portrait_DarkPalette() = GameDetailScreenPreview(DarkPalette)

@Preview(
    group = "Screen · Portrait",
    name = "Nature",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5
)
@Composable
private fun Screen_Portrait_Nature() = GameDetailScreenPreview(NaturePalette)

@Preview(
    group = "Screen · Portrait",
    name = "Grayscale",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5
)
@Composable
private fun Screen_Portrait_Grayscale() = GameDetailScreenPreview(GrayscalePalette)

@Preview(
    group = "Screen · Portrait",
    name = "Classic — Oscuro",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun Screen_Portrait_Classic_Dark() = GameDetailScreenPreview(darkTheme = true)

@Preview(
    group = "Screen · Portrait",
    name = "Dark palette — Oscuro",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun Screen_Portrait_DarkPalette_Dark() = GameDetailScreenPreview(DarkPalette, darkTheme = true)

@Preview(
    group = "Screen · Portrait",
    name = "Nature — Oscuro",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun Screen_Portrait_Nature_Dark() = GameDetailScreenPreview(NaturePalette, darkTheme = true)

@Preview(
    group = "Screen · Portrait",
    name = "Grayscale — Oscuro",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun Screen_Portrait_Grayscale_Dark() = GameDetailScreenPreview(GrayscalePalette, darkTheme = true)

@Preview(
    group = "Screen · Portrait",
    name = "Edición",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_5
)
@Composable
private fun Screen_Portrait_Editing() = GameDetailScreenPreview(viewModel = PreviewGameDetailsViewModel(editing = true))

// ══════════════════════════════════════════════════════════════════════════════
// Grupo 4 — GameDetailsScreen · Landscape
// ══════════════════════════════════════════════════════════════════════════════

private const val LANDSCAPE_SPEC = "spec:width=891dp,height=411dp,orientation=landscape"

@Preview(
    group = "Screen · Landscape",
    name = "Classic",
    showBackground = true,
    showSystemUi = true,
    device = LANDSCAPE_SPEC
)
@Composable
private fun Screen_Landscape_Classic() = GameDetailScreenPreview()

@Preview(
    group = "Screen · Landscape",
    name = "Dark palette",
    showBackground = true,
    showSystemUi = true,
    device = LANDSCAPE_SPEC
)
@Composable
private fun Screen_Landscape_DarkPalette() = GameDetailScreenPreview(DarkPalette)

@Preview(
    group = "Screen · Landscape",
    name = "Nature",
    showBackground = true,
    showSystemUi = true,
    device = LANDSCAPE_SPEC
)
@Composable
private fun Screen_Landscape_Nature() = GameDetailScreenPreview(NaturePalette)

@Preview(
    group = "Screen · Landscape",
    name = "Grayscale",
    showBackground = true,
    showSystemUi = true,
    device = LANDSCAPE_SPEC
)
@Composable
private fun Screen_Landscape_Grayscale() = GameDetailScreenPreview(GrayscalePalette)

@Preview(
    group = "Screen · Landscape",
    name = "Classic — Oscuro",
    showBackground = true,
    showSystemUi = true,
    device = LANDSCAPE_SPEC,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun Screen_Landscape_Classic_Dark() = GameDetailScreenPreview(darkTheme = true)

@Preview(
    group = "Screen · Landscape",
    name = "Nature — Oscuro",
    showBackground = true,
    showSystemUi = true,
    device = LANDSCAPE_SPEC,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun Screen_Landscape_Nature_Dark() = GameDetailScreenPreview(NaturePalette, darkTheme = true)

// ══════════════════════════════════════════════════════════════════════════════
// Grupo 5 — Estados especiales
// ══════════════════════════════════════════════════════════════════════════════

@Preview(group = "Screen · Estados", name = "Estado de carga", showBackground = true, device = Devices.PIXEL_5)
@Composable
private fun Screen_Loading() =
    GameDetailScreenPreview(viewModel = PreviewGameDetailsViewModel(matchDto = null))