package com.agustin.tarati

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.agustin.tarati.core.data.database.MIGRATION_1_2
import com.agustin.tarati.core.data.database.MIGRATION_2_3
import com.agustin.tarati.core.data.database.TaratiDatabase
import com.agustin.tarati.core.data.database.dao.GameDao
import com.agustin.tarati.core.data.repositories.RoomGameRepository
import com.agustin.tarati.core.domain.repository.GameRepository
import com.agustin.tarati.di.sharedModules
import com.agustin.tarati.features.seasonal.ISpecialEventManager
import com.agustin.tarati.features.seasonal.SpecialEventManager
import com.agustin.tarati.features.seasonal.SpecialEventRepository
import com.agustin.tarati.features.settings.AndroidSettingsRepository
import com.agustin.tarati.features.settings.SettingsRepository
import com.agustin.tarati.services.achievements.AchievementsManager
import com.agustin.tarati.services.achievements.AchievementsRepository
import com.agustin.tarati.services.achievements.AchievementsRepositoryImpl
import com.agustin.tarati.services.achievements.ActivityProvider
import com.agustin.tarati.services.achievements.GamesAuthManager
import com.agustin.tarati.services.achievements.IAchievementsManager
import com.agustin.tarati.services.achievements.IAchievementsReporter
import com.agustin.tarati.services.achievements.IAchievementsRepository
import com.agustin.tarati.services.achievements.PlayGamesAchievementsReporter
import com.agustin.tarati.services.billing.BillingManager
import com.agustin.tarati.services.billing.IBillingManager
import com.agustin.tarati.services.clipboard.ClipboardServiceImpl
import com.agustin.tarati.services.clipboard.GameClipboardHelper
import com.agustin.tarati.services.clipboard.IClipboardService
import com.agustin.tarati.services.sound.ISoundService
import com.agustin.tarati.services.sound.SoundManager
import com.agustin.tarati.services.sound.SoundServiceImpl
import com.google.android.gms.games.PlayGames
import features.settings.AndroidSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

// ── Database ──────────────────────────────────────────────────────────────────

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            TaratiDatabase::class.java,
            "tarati-db",
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }
    single<GameDao> { get<TaratiDatabase>().gameDao() }
    single<GameRepository> { RoomGameRepository(get()) }
}

// ── DataStore ─────────────────────────────────────────────────────────────────

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = {
                File(get<Context>().filesDir, "datastore/user_preferences.preferences_pb")
            },
        )
    }
}

// ── Settings ──────────────────────────────────────────────────────────────────

val appStateModule = module {
    single<SettingsRepository> { AndroidSettingsRepository(get()) }
}

// ── Achievements ──────────────────────────────────────────────────────────────

val achievementsModule = module {
    single { ActivityProvider() }
    single { AchievementsRepository(get()) }
    single<IAchievementsReporter> { PlayGamesAchievementsReporter(get(), get()) }
    single { AchievementsManager(get(), get(), get(), get(), get()) } bind IAchievementsManager::class

    /**
     * Activity - Inyectada desde ActivityProvider.
     * IMPORTANTE: Se debe llamar a ActivityProvider.set(activity) desde MainActivity.onResume()
     * antes de resolver cualquier dependencia que necesite Activity.
     */
    factory<Activity> {
        get<ActivityProvider>().get()
            ?: error("Activity not available. Call ActivityProvider.set() in MainActivity.onResume() first.")
    }

    /**
     * GamesAuthManager - Gestiona autenticación con Google Play Games.
     * Single: Una instancia compartida en toda la app.
     */
    single {
        GamesAuthManager(
            activity = get()  // Activity inyectado por Koin
        )
    }

    /**
     * IAchievementsRepository - Implementación con Google Play Games.
     * Single: Una instancia compartida que mantiene el estado de achievements.
     */
    single<IAchievementsRepository> {
        val activity: Activity = get()

        AchievementsRepositoryImpl(
            gamesSignInClient = PlayGames.getGamesSignInClient(activity),
            achievementsClient = PlayGames.getAchievementsClient(activity)
        )
    }
}

// ── Special Events ────────────────────────────────────────────────────────────

val specialEventModule = module {
    single { SpecialEventRepository(get()) }
    single<ISpecialEventManager> { SpecialEventManager(get(), get(), get(), get()) }
}

// ── Billing ───────────────────────────────────────────────────────────────────

val billingModule = module {
    single<IBillingManager> { BillingManager(context = get(), activityProvider = get()) }
}

// ── Services ──────────────────────────────────────────────────────────────────

val androidServiceModule = module {
    single<IClipboardService> { ClipboardServiceImpl(get()) }
    single { GameClipboardHelper(get()) }
}

// ── Sound ─────────────────────────────────────────────────────────────────────

val soundModule = module {
    single<SoundManager> { SoundManager(get()) }
    single<ISoundService> { SoundServiceImpl(get()) }
}

// ── ViewModels ────────────────────────────────────────────────────────────────

val androidViewModelModule = module {
    viewModel { AndroidSettingsViewModel(get(), get(), get()) }
}

// ── Lista completa de módulos Android ────────────────────────────────────────

/**
 * Todos los módulos necesarios para Android.
 * Incluye [sharedModules] + módulos Android-specific.
 *
 * Uso en TaratiApplication:
 * ```kotlin
 * startKoin { modules(androidModules) }
 * ```
 */
val androidModules = sharedModules + listOf(
    databaseModule,
    dataStoreModule,
    appStateModule,
    achievementsModule,
    specialEventModule,
    billingModule,
    androidServiceModule,
    soundModule,
    androidViewModelModule,
)