package com.agustin.tarati.features.online

/**
 * URL base del servidor, incluyendo protocolo, específica por plataforma.
 *
 * - Android / Desktop → https://tarati.tech
 * - Web WASM (prod)   → https://<window.location.host>  (mismo dominio que sirve el bundle)
 * - Web WASM (dev)    → http://localhost:3000            (webpack dev server en :3000; el proxy
 *                        redirige /api, /auth, /ws, etc. a Ktor en :8080 — ver devServer.js)
 * - Web WASM (manual) → http://<__TARATI_SERVER__>       (escape hatch para apuntar a un servidor
 *                        distinto sin reconstruir; definir en index.html antes de tarati.js)
 */
expect val devServerUrl: String