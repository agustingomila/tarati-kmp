package com.agustin.tarati.ui.components.navigation

import androidx.compose.runtime.Composable
import com.agustin.tarati.features.game.IGameModel

/**
 * Inyecta la implementación de IGameModel correcta para cada plataforma.
 *
 * - Android  → koinViewModel<GameViewModel>()
 * - Desktop  → koinViewModel<DesktopGameViewModel>()
 *
 * Necesario porque koinViewModel<T> requiere que T extienda ViewModel,
 * por lo que no se puede usar la interfaz IGameModel directamente.
 */
@Composable
expect fun injectGameViewModel(): IGameModel