package com.agustin.tarati.ui.components.game.highlights.sequences.previews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.agustin.tarati.core.domain.game.board.BoardOrientation
import com.agustin.tarati.core.domain.game.board.GameBoard
import com.agustin.tarati.core.domain.game.board.buildPositionCache
import com.agustin.tarati.core.domain.game.board.getVisualPosition
import com.agustin.tarati.core.domain.game.pieces.CobColor
import com.agustin.tarati.core.domain.game.play.MatchState.Companion.createInitialMatchState
import com.agustin.tarati.features.game.previews.previewRandomFinalGameState
import com.agustin.tarati.ui.components.game.draw.board.drawArrowEdgeHighlightFromVertex
import com.agustin.tarati.ui.components.game.draw.board.drawDynamicEdgeElectricHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawDynamicFireballEdgeHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawElectricEdgeHighlightFromVertex
import com.agustin.tarati.ui.components.game.draw.board.drawFireballEdgeHighlightFromVertex
import com.agustin.tarati.ui.components.game.draw.board.drawForceArcDynamicHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawForceArcEdgeHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawForceArcImpactHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawRegionHighlight
import com.agustin.tarati.ui.components.game.draw.board.drawVertexHighlight
import com.agustin.tarati.ui.components.game.draw.board.getHighlightsSegmentsRange
import com.agustin.tarati.ui.components.game.highlights.HighlightAnimation
import com.agustin.tarati.ui.components.game.highlights.sequences.createAlternativeGameOverSequence
import com.agustin.tarati.ui.components.game.highlights.sequences.createGameOverSequence
import com.agustin.tarati.ui.theme.rememberBoardColors
import kotlin.random.Random

@Composable
private fun PreviewBoard(
    previewController: PreviewAnimationController,
    modifier: Modifier = Modifier,
) {
    val currentHighlights by previewController.currentHighlights.collectAsState()
    val colors = rememberBoardColors()
    val orientation = BoardOrientation.PORTRAIT_WHITE

    var containerSize by remember { mutableStateOf(Size.Zero) }
    val random = remember { Random.Default }
    var randomSegments by remember { mutableIntStateOf(8) }
    var randomSeed by remember { mutableFloatStateOf(0.5f) }

    Box(
        modifier =
            modifier
                .background(Color(0xFF1A1A1A))
                .onGloballyPositioned { coords ->
                    containerSize = coords.size.toSize()
                },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Dibujar fondo del tablero
            drawRect(color = Color(0xFF2D2D2D))

            // Dibujar vértices del tablero como referencia
            GameBoard.vertices.forEach { vertex ->
                val pos = getVisualPosition(vertex, containerSize, orientation)
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.3f),
                    center = pos,
                    radius = 8f,
                )
            }

            // Dibujar cada highlight
            currentHighlights.forEach { highlight ->

                when (highlight) {
                    is HighlightAnimation.Vertex -> {
                        drawVertexHighlight(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            orientation = orientation,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.FireballEdge -> {
                        drawFireballEdgeHighlightFromVertex(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            colors = colors,
                            orientation = orientation,
                        )
                    }

                    is HighlightAnimation.ElectricEdge -> {
                        val positionCache = buildPositionCache(containerSize, orientation)
                        val segmentsRange =
                            getHighlightsSegmentsRange(highlight.highlight, positionCache)
                        randomSegments = random.nextInt(segmentsRange.first, segmentsRange.second)
                        randomSeed = random.nextFloat()

                        drawElectricEdgeHighlightFromVertex(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            orientation = orientation,
                            variationFactor = randomSeed,
                            randomSegments = randomSegments,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.Region -> {
                        drawRegionHighlight(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            orientation = orientation,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.DynamicFireballEdge -> {
                        drawDynamicFireballEdgeHighlight(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.DynamicElectricEdge -> {
                        val segmentsRange = getHighlightsSegmentsRange(highlight.highlight)
                        randomSegments = random.nextInt(segmentsRange.first, segmentsRange.second)
                        randomSeed = random.nextFloat()

                        drawDynamicEdgeElectricHighlight(
                            highlight = highlight.highlight,
                            variationFactor = randomSeed,
                            randomSegments = randomSegments,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.DynamicForceArc -> {
                        drawForceArcDynamicHighlight(
                            highlight = highlight.highlight,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.DynamicForceArcImpact -> {
                        drawForceArcImpactHighlight(
                            highlight = highlight.highlight,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.ForceArcEdge -> {
                        drawForceArcEdgeHighlight(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            orientation = orientation,
                            colors = colors,
                        )
                    }

                    is HighlightAnimation.Arrow ->
                        drawArrowEdgeHighlightFromVertex(
                            highlight = highlight.highlight,
                            canvasSize = containerSize,
                            orientation = orientation,
                            colors = colors,
                        )

                    is HighlightAnimation.Pause -> {
                        // No dibujar nada durante pausas
                    }
                }
            }
        }

        // DEBUG: Mostrar información de depuración en la UI
        Text(
            text = "Highlights: ${currentHighlights.size}",
            color = Color.White,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(4.dp),
        )
    }
}

@Composable
private fun PreviewControls(
    previewController: PreviewAnimationController,
    isPlaying: Boolean,
    sequenceCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Fila 1: Controles principales
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Botones para secuencias específicas
            val buttonModifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)

            for (i in 0 until sequenceCount) {
                Button(
                    onClick = { previewController.animateGameOverSequence(i) },
                    enabled = !isPlaying,
                    modifier = buttonModifier,
                ) {
                    Text("Game Over ${i + 1}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fila 2: Secuencias directas
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    // Usar un MatchState válido con ganador
                    val validMatchState =
                        createInitialMatchState().copy(
                            winner = CobColor.WHITE,
                        )
                    previewController.animateSequence(
                        createGameOverSequence(validMatchState),
                        "preview_original",
                    )
                },
                enabled = !isPlaying,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Original Sequence")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Usar un MatchState válido con ganador
                    val validMatchState =
                        createInitialMatchState().copy(
                            gameState = previewRandomFinalGameState,
                            winner = CobColor.WHITE,
                        )
                    previewController.animateSequence(
                        createAlternativeGameOverSequence(validMatchState),
                        "preview_alternative",
                    )
                },
                enabled = !isPlaying,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Alternative Sequence")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de stop
        Button(
            onClick = { previewController.stopHighlights() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Stop All Animations")
        }
    }
}

@Composable
fun AnimationPreview(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val previewController = remember { PreviewAnimationController(coroutineScope) }
    val isPlaying by previewController.isPlaying.collectAsState()
    val currentSequenceName by previewController.currentSequenceName.collectAsState()
    val sequenceCount = previewController.getAvailableSequenceCount()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Header con información
            PreviewHeader(
                isPlaying = isPlaying,
                currentSequenceName = currentSequenceName,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            )

            // Tablero con animaciones
            PreviewBoard(
                previewController = previewController,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            )

            // Controles en la parte inferior
            PreviewControls(
                previewController = previewController,
                isPlaying = isPlaying,
                sequenceCount = sequenceCount,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            )
        }
    }
}

@Composable
private fun PreviewHeader(
    isPlaying: Boolean,
    currentSequenceName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isPlaying) "▶ Playing: $currentSequenceName" else "⏸ Animation Preview",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 900)
@Composable
fun AnimationPreview_Preview() {
    AnimationPreview()
}