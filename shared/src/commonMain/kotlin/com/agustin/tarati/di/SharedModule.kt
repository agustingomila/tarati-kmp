package com.agustin.tarati.di

import com.agustin.tarati.core.domain.ai.api.IAIEngine
import com.agustin.tarati.core.domain.ai.engine.TaratiAI
import com.agustin.tarati.features.detail.GameDetailsViewModel
import com.agustin.tarati.features.game.GameViewModel
import com.agustin.tarati.features.library.GamesLibraryViewModel
import com.agustin.tarati.services.ai.AIViewModel
import com.agustin.tarati.services.clock.ClockViewModel
import com.agustin.tarati.services.clock.IClockService
import com.agustin.tarati.services.dialogs.DialogService
import com.agustin.tarati.services.dialogs.DialogViewModel
import com.agustin.tarati.services.dialogs.IDialogViewModel
import com.agustin.tarati.ui.components.game.animation.AnimationCoordinator
import com.agustin.tarati.ui.components.game.animation.BoardAnimationViewModel
import com.agustin.tarati.ui.components.game.animation.BoardGeometryViewModel
import com.agustin.tarati.ui.components.game.animation.IBoardGeometryViewModel
import com.agustin.tarati.ui.components.game.behaviors.BoardSelectionViewModel
import com.agustin.tarati.ui.components.tutorial.ITutorialService
import com.agustin.tarati.ui.components.tutorial.TutorialService
import com.agustin.tarati.ui.components.tutorial.TutorialViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Módulo Koin compartido entre todas las plataformas.
 *
 * Contiene únicamente dependencias que no requieren APIs de Android:
 * - Motor de IA
 * - ViewModels de animación/geometría/selección (lógica pura)
 * - Servicios de diálogo y tutorial (lógica pura)
 *
 * ## Lo que NO está aquí (es platform-specific):
 * - Room/DataStore → androidModule
 * - SoundService (MediaPlayer) → androidModule / desktopModule
 * - ClipboardService → androidModule / desktopModule
 * - Achievements/Billing → androidModule
 * - SavedStateHandle → eliminado de ViewModels KMP
 */
val sharedAiModule = module {
    single<IAIEngine> { TaratiAI.instance }
}

val sharedAnimationModule = module {
    single<AnimationCoordinator> { AnimationCoordinator(get()) }
}

val sharedDialogModule = module {
    single<IDialogViewModel> { DialogService(get()) }
}

val sharedTutorialModule = module {
    single<ITutorialService> { TutorialService(get()) }
}

val sharedViewModelModule = module {
    // SettingsViewModel base (sin achievements/billing - override en androidApp)
    // NO registrar aquí por interfaz - cada plataforma registra su implementación
    viewModel { params -> GameViewModel(params.get(), get(), get()) }
    viewModel { GamesLibraryViewModel(get()) }
    viewModel { GameDetailsViewModel(get()) }

    viewModel { BoardGeometryViewModel() } bind IBoardGeometryViewModel::class
    viewModel { BoardAnimationViewModel(get()) }
    viewModel { BoardSelectionViewModel() }
    viewModel { ClockViewModel() } bind IClockService::class
    viewModel { DialogViewModel() }
    viewModel { AIViewModel(get()) }
    viewModel { (animatorCoordinator: AnimationCoordinator) ->
        TutorialViewModel(animatorCoordinator, get())
    }
}

/**
 * Lista de módulos compartidos para usar en todas las plataformas.
 * Cada plataforma agrega sus módulos específicos encima de estos.
 *
 * Uso:
 * ```kotlin
 * // Android
 * startKoin { modules(sharedModules + androidModules) }
 *
 * // Desktop
 * KoinApplication(application = { modules(sharedModules + desktopModules) })
 * ```
 */
val sharedModules = listOf(
    sharedAiModule,
    sharedAnimationModule,
    sharedDialogModule,
    sharedTutorialModule,
    sharedViewModelModule,
)