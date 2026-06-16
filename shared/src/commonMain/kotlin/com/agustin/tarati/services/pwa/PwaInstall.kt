package com.agustin.tarati.services.pwa

/** Returns true if the browser has a pending PWA install prompt available. */
expect fun pwaInstallAvailable(): Boolean

/** Triggers the browser's PWA install prompt. No-op if not available. */
expect fun pwaInstall()
