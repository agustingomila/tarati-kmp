package com.agustin.tarati.desktop.di

import com.agustin.tarati.core.data.database.TaratiDatabase
import com.agustin.tarati.core.data.database.dao.GameDao
import com.agustin.tarati.core.data.repositories.RoomGameRepository
import com.agustin.tarati.core.domain.repository.GameRepository
import com.agustin.tarati.desktop.data.createDesktopDatabase
import com.agustin.tarati.desktop.features.game.DesktopGameViewModel
import com.agustin.tarati.desktop.services.achievements.NoOpAchievementsManager
import com.agustin.tarati.features.detail.GameDetailsViewModel
import com.agustin.tarati.features.library.GamesLibraryViewModel
import com.agustin.tarati.features.seasonal.ISpecialEventManager
import com.agustin.tarati.features.seasonal.NoOpSpecialEventManager
import com.agustin.tarati.features.settings.DesktopSettingsRepository
import com.agustin.tarati.features.settings.DesktopSettingsViewModel
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.services.achievements.IAchievementsManager
import com.agustin.tarati.services.clipboard.DesktopClipboardService
import com.agustin.tarati.services.clipboard.GameClipboardHelper
import com.agustin.tarati.services.clipboard.IClipboardService
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.services.sound.NoOpSoundService
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for Desktop (JVM).
 *
 * Provides Desktop-specific implementations:
 * - [DesktopClipboardService] → uses java.awt.Toolkit
 * - [NoOpSoundService] → no audio in Desktop (for now)
 * - [NoOpAchievementsManager] → no achievements system in Desktop
 * - [DesktopSettingsRepository] → **java.util.prefs.Preferences persistence** ✅
 * - [RoomGameRepository] → **Room with SQLite persistence** ✅
 *
 * ## Persistence Locations
 *
 * **Game Database** (Room/SQLite):
 * - Windows: `C:\Users\{username}\.tarati\tarati.db`
 * - macOS: `/Users/{username}/.tarati/tarati.db`
 * - Linux: `/home/{username}/.tarati/tarati.db`
 *
 * **Settings** (Preferences):
 * - Windows: `HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\agustin\tarati\settings`
 * - macOS: `~/Library/Preferences/com.agustin.tarati.settings.plist`
 * - Linux: `~/.java/.userPrefs/com/agustin/tarati/settings/prefs.xml`
 *
 * ## Expected evolution
 * - Sound: add support with javax.sound.sampled
 * - Achievements: consider local achievement system with SQLite + system notifications
 */
val desktopServiceModule = module {
    // Clipboard — java.awt.Toolkit
    single<IClipboardService> { DesktopClipboardService() }
    single { GameClipboardHelper(get()) }

    // Sound — no-op in Desktop for now
    single<ISoundService> { NoOpSoundService() }

    // Special Events — no-op in Desktop
    single<ISpecialEventManager> { NoOpSpecialEventManager() }

    // Achievements — no-op in Desktop (no Google Play Games)
    single<IAchievementsManager> { NoOpAchievementsManager() }
}

/**
 * Database and Repository module for Desktop.
 *
 * **Room Database with SQLite persistence**:
 * - Uses Room 2.8+ multiplatform support
 * - SQLite via JDBC (BundledSQLiteDriver)
 * - Database persists between app launches
 * - Supports migrations (MIGRATION_1_2, MIGRATION_2_3)
 * - Same schema as Android (shared DAO and Entity definitions)
 *
 * **Settings Repository with Preferences**:
 * - Uses java.util.prefs.Preferences for persistence
 * - Settings survive app restarts
 * - Type-safe with enums
 * - Reactive with StateFlows
 */
val desktopDataModule = module {
    // Settings — Preferences persistence
    single<SettingsRepository> { DesktopSettingsRepository() }

    // Room Database — creates instance with SQLite persistence
    single<TaratiDatabase> { createDesktopDatabase() }

    // GameDao — extracted from database
    single<GameDao> { get<TaratiDatabase>().gameDao() }

    // Repository — Room implementation with disk persistence
    single<GameRepository> { RoomGameRepository(get()) }
}

val desktopViewModelModule = module {
    // Settings ViewModel
    viewModel { DesktopSettingsViewModel(get()) }

    // Game ViewModel — CRITICAL: register with IGameModel interface
    // so NavGraph can resolve it correctly with koinViewModel<GameViewModel>()
    viewModel { DesktopGameViewModel(get(), get()) }

    // Library and Details ViewModels (already in shared, just need registration)
    viewModel { GamesLibraryViewModel(get()) }
    viewModel { GameDetailsViewModel(get()) }
}

val desktopModules = listOf(
    desktopServiceModule,
    desktopDataModule,
    desktopViewModelModule,
)