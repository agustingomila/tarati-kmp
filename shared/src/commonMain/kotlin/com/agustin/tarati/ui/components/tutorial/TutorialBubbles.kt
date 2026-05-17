package com.agustin.tarati.ui.components.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.agustin.tarati.services.localization.LocalizedText
import com.agustin.tarati.shared.generated.resources.Res
import com.agustin.tarati.shared.generated.resources.back
import com.agustin.tarati.shared.generated.resources.collapse_step
import com.agustin.tarati.shared.generated.resources.expand_step
import com.agustin.tarati.shared.generated.resources.next
import com.agustin.tarati.shared.generated.resources.repeat_explanation
import com.agustin.tarati.shared.generated.resources.skip_tutorial
import com.agustin.tarati.shared.generated.resources.start
import com.agustin.tarati.ui.theme.TaratiIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun TutorialBubble(
    title: String,
    bubbleState: TutorialBubbleState,
    bubbleEvents: TutorialBubbleEvents,
    modifier: Modifier = Modifier,
) {
    val config = bubbleState.config

    Box(
        modifier =
            modifier
                .zIndex(1000f)
                .padding(16.dp),
    ) {
        val alignment =
            when (config.position) {
                BubblePosition.TOP_LEFT -> Alignment.TopStart
                BubblePosition.TOP_CENTER -> Alignment.TopCenter
                BubblePosition.TOP_RIGHT -> Alignment.TopEnd
                BubblePosition.CENTER_LEFT -> Alignment.CenterStart
                BubblePosition.CENTER_CENTER -> Alignment.Center
                BubblePosition.CENTER_RIGHT -> Alignment.CenterEnd
                BubblePosition.BOTTOM_LEFT -> Alignment.BottomStart
                BubblePosition.BOTTOM_CENTER -> Alignment.BottomCenter
                BubblePosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                BubblePosition.VERTEX_SPECIFIC -> Alignment.TopStart
            }

        Box(
            modifier =
                Modifier
                    .align(alignment)
                    .width(config.size.width.dp)
                    .wrapContentHeight(),
        ) {
            BubbleContent(
                title = title,
                bubbleState = bubbleState.contentState,
                bubbleEvents = bubbleEvents,
            )
        }
    }
}

@Composable
private fun BubbleContent(
    title: String,
    bubbleState: TutorialBubbleContentState,
    bubbleEvents: TutorialBubbleEvents,
    modifier: Modifier = Modifier,
) {
    val currentStep = bubbleState.currentStep
    val totalSteps = bubbleState.totalSteps
    val description = bubbleState.description
    val canGoBack = bubbleState.canGoBack
    val canGoForward = bubbleState.canGoForward
    val lastStep = currentStep == totalSteps

    // Expanded by default. rememberSaveable keyed on currentStep resets the state
    // each time the user navigates to a new step, so each step starts expanded.
    var expanded by rememberSaveable(currentStep) { mutableStateOf(true) }

    Surface(
        modifier =
            modifier
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header — always visible: [˄˅] [title ............] [replay] [close]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Collapse/expand toggle — left of title
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = if (expanded) TaratiIcons.ExpandLess else TaratiIcons.ExpandMore,
                        contentDescription = if (expanded) {
                            stringResource(Res.string.collapse_step)
                        } else {
                            stringResource(Res.string.expand_step)
                        },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )

                Row {
                    // Botón de repetir
                    IconButton(
                        onClick = bubbleEvents::onRepeat,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            TaratiIcons.Replay,
                            contentDescription = stringResource(Res.string.repeat_explanation),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    // Botón de saltar
                    IconButton(
                        onClick = bubbleEvents::onSkip,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            TaratiIcons.Close,
                            contentDescription = stringResource(Res.string.skip_tutorial),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar — always visible regardless of collapsed state
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
            )

            // Collapsible body: step counter + description + navigation buttons
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Indicador de paso
                    Text(
                        text = "$currentStep/$totalSteps",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de navegación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        if (currentStep > 1) {
                            Button(
                                onClick = bubbleEvents::onPrevious,
                                enabled = canGoBack,
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary,
                                    ),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(
                                    TaratiIcons.KeyboardArrowLeft,
                                    contentDescription = stringResource(Res.string.back),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(Res.string.back))
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = bubbleEvents::onNext,
                            enabled = canGoForward,
                            modifier = Modifier.weight(1f),
                        ) {
                            if (lastStep) {
                                LocalizedText(Res.string.start)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    TaratiIcons.PlayArrow,
                                    contentDescription = stringResource(Res.string.start),
                                )
                            } else {
                                LocalizedText(Res.string.next)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    TaratiIcons.KeyboardArrowRight,
                                    contentDescription = stringResource(Res.string.next),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}