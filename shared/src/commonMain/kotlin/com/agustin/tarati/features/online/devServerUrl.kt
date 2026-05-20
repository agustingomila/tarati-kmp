package com.agustin.tarati.features.online

/**
 * URL base del servidor, incluyendo protocolo, específica por plataforma.
 *
 * - Android / Desktop (producción) → https://tarati.tech
 * - Web WASM (producción)          → https://<window.location.host>
 * - Web WASM (dev override)        → http://localhost:8080  (via __TARATI_SERVER__)
 */
expect val devServerUrl: String