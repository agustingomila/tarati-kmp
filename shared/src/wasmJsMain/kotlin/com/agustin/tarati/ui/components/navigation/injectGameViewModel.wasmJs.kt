package com.agustin.tarati.ui.components.navigation

import androidx.compose.runtime.Composable
import com.agustin.tarati.features.game.IGameModel
import com.agustin.tarati.features.game.WasmGameViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun injectGameViewModel(): IGameModel = koinViewModel<WasmGameViewModel>()
