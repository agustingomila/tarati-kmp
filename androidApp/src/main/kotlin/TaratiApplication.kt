package com.agustin.tarati

import android.app.Application
import com.agustin.tarati.core.utils.logging.LoggingFactory.getLogger
import com.agustin.tarati.services.sound.SoundManager
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class TaratiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TaratiApplication)
            modules(androidModules)
        }
    }

    override fun onTerminate() {
        try {
            val soundManager: SoundManager? = getKoin().getOrNull()
            soundManager?.release()
        } catch (e: Exception) {
            getLogger("TaratiApplication").error("Error releasing SoundManager on terminate", e)
        }
        stopKoin()
        super.onTerminate()
    }
}