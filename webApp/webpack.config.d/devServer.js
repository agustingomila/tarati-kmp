// Dev server para Tarati WebApp
// Puerto: 3000 (evita conflicto con el servidor Ktor en :8080)
// Proxy: reenvía todas las llamadas al backend hacia Ktor en :8080
//
// Con este config, devServerUrl en WASM resuelve a http://localhost:3000.
// Todas las llamadas REST (/api, /auth, etc.) y WebSocket (/ws) son
// interceptadas por el proxy y redirigidas a localhost:8080.
// No hace falta descomentar __TARATI_SERVER__ en index.html.
if (config.devServer) {
    config.devServer.port = 3000;
    config.devServer.proxy = [
        {
            context: ['/api', '/auth', '/health', '/stats', '/metrics'],
            target: 'http://localhost:8080',
            changeOrigin: true,
        },
        {
            context: ['/ws'],
            target: 'ws://localhost:8080',
            ws: true,
            changeOrigin: true,
        },
    ];
}
