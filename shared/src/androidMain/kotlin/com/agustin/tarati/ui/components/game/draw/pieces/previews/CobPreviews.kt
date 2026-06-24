package com.agustin.tarati.ui.components.game.draw.pieces.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard.A1
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import com.agustin.tarati.core.domain.game.pieces.Cob
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.pieces.opponent
import com.agustin.tarati.ui.components.game.animation.AnimatedCob
import com.agustin.tarati.ui.components.game.draw.pieces.drawAnimatedCob
import com.agustin.tarati.ui.components.game.draw.pieces.drawCob
import com.agustin.tarati.ui.theme.AuroraPalette
import com.agustin.tarati.ui.theme.BoardColors
import com.agustin.tarati.ui.theme.ChristmasPalette
import com.agustin.tarati.ui.theme.ClassicPalette
import com.agustin.tarati.ui.theme.DarkPalette
import com.agustin.tarati.ui.theme.EmberPalette
import com.agustin.tarati.ui.theme.GildedPalette
import com.agustin.tarati.ui.theme.GrayscalePalette
import com.agustin.tarati.ui.theme.HalloweenPalette
import com.agustin.tarati.ui.theme.NaturePalette
import com.agustin.tarati.ui.theme.TaratiTheme
import com.agustin.tarati.ui.theme.getBoardColors

// ==================== CLASSIC ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color)
        PreviewCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingClassicSelected(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    isSelected: Boolean = true,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color, isSelected)
        PreviewCobDrawing(boardColors, color.opponent, isSelected)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewSelectedCobDrawing(boardColors, color)
        PreviewSelectedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewUpgradedAnimatedCobDrawing(boardColors, color)
        PreviewUpgradedAnimatedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewConvertingCobDrawing(boardColors, color)
        PreviewConvertingCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsClassic(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== DARK ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingDarkSelected(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassicSelected(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewSelectedCobClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewUpgradedAnimatedCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewConvertingCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsDark(
    boardColors: BoardColors = getBoardColors(DarkPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== NATURE ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingNatureSelected(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassicSelected(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewSelectedCobClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewUpgradedAnimatedCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewConvertingCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsNature(
    boardColors: BoardColors = getBoardColors(NaturePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== GRAYSCALE ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingGrayscaleSelected(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassicSelected(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewSelectedCobClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewUpgradedAnimatedCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewConvertingCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsGrayscale(
    boardColors: BoardColors = getBoardColors(GrayscalePalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== CHRISTMAS ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingChristmasSelected(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassicSelected(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewSelectedCobClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewUpgradedAnimatedCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewConvertingCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsChristmas(
    boardColors: BoardColors = getBoardColors(ChristmasPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== HALLOWEEN ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingHalloweenSelected(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewCobDrawingClassicSelected(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewSelectedCobClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewUpgradedAnimatedCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewConvertingCobDrawingClassic(boardColors, color)
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsHalloween(
    boardColors: BoardColors = getBoardColors(HalloweenPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== EMBER ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color)
        PreviewCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingEmberSelected(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    color: CobColor = CobColor.WHITE,
    isSelected: Boolean = true,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color, isSelected)
        PreviewCobDrawing(boardColors, color.opponent, isSelected)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewSelectedCobDrawing(boardColors, color)
        PreviewSelectedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewUpgradedAnimatedCobDrawing(boardColors, color)
        PreviewUpgradedAnimatedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewConvertingCobDrawing(boardColors, color)
        PreviewConvertingCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsEmber(
    boardColors: BoardColors = getBoardColors(EmberPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== AURORA ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color)
        PreviewCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingAuroraSelected(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    color: CobColor = CobColor.WHITE,
    isSelected: Boolean = true,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color, isSelected)
        PreviewCobDrawing(boardColors, color.opponent, isSelected)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewSelectedCobDrawing(boardColors, color)
        PreviewSelectedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewUpgradedAnimatedCobDrawing(boardColors, color)
        PreviewUpgradedAnimatedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewConvertingCobDrawing(boardColors, color)
        PreviewConvertingCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsAurora(
    boardColors: BoardColors = getBoardColors(AuroraPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== GILDED ====================

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color)
        PreviewCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobDrawingGildedSelected(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    color: CobColor = CobColor.WHITE,
    isSelected: Boolean = true,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewCobDrawing(boardColors, color, isSelected)
        PreviewCobDrawing(boardColors, color.opponent, isSelected)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewSelectedCobGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewSelectedCobDrawing(boardColors, color)
        PreviewSelectedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewUpgradedAnimatedCobDrawingGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewUpgradedAnimatedCobDrawing(boardColors, color)
        PreviewUpgradedAnimatedCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewConvertingCobDrawingGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    color: CobColor = CobColor.WHITE,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        PreviewConvertingCobDrawing(boardColors, color)
        PreviewConvertingCobDrawing(boardColors, color.opponent)
    }
}

@Preview(group = "Cob", showBackground = true)
@Composable
fun PreviewAnimatedCobVariantsGilded(
    boardColors: BoardColors = getBoardColors(GildedPalette),
    color: CobColor = CobColor.WHITE,
) {
    PreviewAnimatedCobVariants(boardColors, color)
}

// ==================== COMPOSABLES GENERALES ====================

@Composable
fun PreviewCobDrawing(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    isSelected: Boolean = false,
    orientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Box(
            modifier =
                Modifier
                    .size(240.dp)
                    .background(Color.LightGray)
                    .padding(48.dp),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val pieceRadius = minOf(size.width, size.height) * 0.8f
                val pos = getVisualPosition(A1, size, orientation)

                drawCob(
                    position = pos,
                    radius = pieceRadius,
                    selectedVertex = if (isSelected) A1 else null,
                    vertex = A1,
                    cob = Cob(color),
                    colors = boardColors,
                )
            }
        }
    }
}

@Composable
fun PreviewSelectedCobDrawing(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    orientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Box(
            modifier =
                Modifier
                    .size(240.dp)
                    .background(Color.LightGray)
                    .padding(48.dp),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val pieceRadius = minOf(size.width, size.height) * 0.8f
                val pos = getVisualPosition(A1, size, orientation)

                drawCob(
                    position = pos,
                    radius = pieceRadius,
                    selectedVertex = A1,
                    vertex = A1,
                    cob = Cob(color, true),
                    hourOfDay = 3.5f,
                    colors = boardColors,
                )
            }
        }
    }
}

@Composable
fun PreviewUpgradedAnimatedCobDrawing(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    orientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Box(
            modifier =
                Modifier
                    .size(240.dp)
                    .background(Color.LightGray)
                    .padding(48.dp),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val pieceRadius = minOf(size.width, size.height) * 0.8f
                val pos = getVisualPosition(A1, size, orientation)

                drawAnimatedCob(
                    position = pos,
                    radius = pieceRadius,
                    vertex = A1,
                    selectedVertex = null,
                    animatedCob =
                        AnimatedCob(
                            vertex = A1,
                            cob = Cob(color, true),
                            currentPos = A1,
                            targetPos = A1,
                            animationProgress = 0.5f,
                            upgradeProgress = 0.7f,
                            conversionProgress = 0.0f,
                        ),
                    hourOfDay = 9f,
                    colors = boardColors,
                )
            }
        }
    }
}

@Composable
fun PreviewConvertingCobDrawing(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    orientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Box(
            modifier =
                Modifier
                    .size(240.dp)
                    .background(Color.LightGray)
                    .padding(48.dp),
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val pieceRadius = minOf(size.width, size.height) * 0.8f
                val pos = getVisualPosition(A1, size, orientation)

                drawAnimatedCob(
                    position = pos,
                    radius = pieceRadius,
                    vertex = A1,
                    selectedVertex = null,
                    animatedCob =
                        AnimatedCob(
                            vertex = A1,
                            cob = Cob(color, true),
                            currentPos = A1,
                            targetPos = A1,
                            upgradeProgress = 0.0f,
                            conversionProgress = 0.6f,
                            isConverting = true,
                        ),
                    hourOfDay = 7.5f,
                    colors = boardColors,
                )
            }
        }
    }
}

@Composable
fun PreviewAnimatedCobVariants(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    orientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Column {
            Text(
                text = "Piezas Animadas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Upgrading
            Row {
                Text(
                    text = "Upgrade en Progreso:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Canvas(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                    ) {
                        val pieceRadius = minOf(size.width, size.height) * 0.8f
                        val pos = getVisualPosition(A1, size, orientation)

                        drawAnimatedCob(
                            position = pos,
                            radius = pieceRadius,
                            vertex = A1,
                            selectedVertex = null,
                            animatedCob =
                                AnimatedCob(
                                    vertex = A1,
                                    cob = Cob(color, true),
                                    currentPos = A1,
                                    targetPos = A1,
                                    upgradeProgress = 0.5f,
                                    conversionProgress = 0.0f,
                                ),
                            hourOfDay = 9f,
                            colors = boardColors,
                        )
                    }
                }

                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Canvas(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                    ) {
                        val pieceRadius = minOf(size.width, size.height) * 0.8f
                        val pos = getVisualPosition(A1, size, orientation)

                        drawAnimatedCob(
                            position = pos,
                            radius = pieceRadius,
                            vertex = A1,
                            selectedVertex = null,
                            animatedCob =
                                AnimatedCob(
                                    vertex = A1,
                                    cob = Cob(color, true),
                                    currentPos = A1,
                                    targetPos = A1,
                                    upgradeProgress = 0.8f,
                                    conversionProgress = 0.0f,
                                ),
                            hourOfDay = 9f,
                            colors = boardColors,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Converting
            Row {
                Text(
                    text = "Conversión en Progreso:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )

                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Canvas(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                    ) {
                        val pieceRadius = minOf(size.width, size.height) * 0.8f
                        val pos = getVisualPosition(A1, size, orientation)

                        drawAnimatedCob(
                            position = pos,
                            radius = pieceRadius,
                            vertex = A1,
                            selectedVertex = null,
                            animatedCob =
                                AnimatedCob(
                                    vertex = A1,
                                    cob = Cob(color),
                                    currentPos = A1,
                                    targetPos = A1,
                                    upgradeProgress = 0.0f,
                                    conversionProgress = 0.3f,
                                    isConverting = true,
                                ),
                            colors = boardColors,
                        )
                    }
                }

                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Canvas(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                    ) {
                        val pieceRadius = minOf(size.width, size.height) * 0.8f
                        val pos = getVisualPosition(A1, size, orientation)

                        drawAnimatedCob(
                            position = pos,
                            radius = pieceRadius,
                            vertex = A1,
                            selectedVertex = null,
                            animatedCob =
                                AnimatedCob(
                                    vertex = A1,
                                    cob = Cob(color),
                                    currentPos = A1,
                                    targetPos = A1,
                                    upgradeProgress = 0.0f,
                                    conversionProgress = 0.7f,
                                    isConverting = true,
                                ),
                            colors = boardColors,
                        )
                    }
                }
            }
        }
    }
}

@Preview(group = "Cob", showBackground = true, widthDp = 500, heightDp = 200)
@Composable
fun PreviewCobSizeVariants(
    boardColors: BoardColors = getBoardColors(ClassicPalette),
    color: CobColor = CobColor.WHITE,
    orientation: BoardOrientation = BoardOrientation.PORTRAIT_WHITE,
) {
    TaratiTheme {
        Column {
            Text(
                text = "Tamaños de Piezas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                listOf(0.8f, 1.0f, 1.2f, 1.5f).forEach { sizeFactor ->
                    Surface(
                        modifier = Modifier.size(120.dp),
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                        ) {
                            Canvas(
                                modifier =
                                    Modifier
                                        .matchParentSize()
                                        .padding(24.dp),
                            ) {
                                val pieceRadius = minOf(size.width, size.height) * sizeFactor
                                val pos = getVisualPosition(A1, size, orientation)

                                drawCob(
                                    position = pos,
                                    radius = pieceRadius,
                                    selectedVertex = null,
                                    vertex = A1,
                                    cob = Cob(color, true),
                                    hourOfDay = 8f,
                                    colors = boardColors,
                                )
                            }
                            Text(
                                text = "${sizeFactor}x",
                                modifier = Modifier.align(Alignment.TopCenter),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}