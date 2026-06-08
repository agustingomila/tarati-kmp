# Tarati — Contexto de Desarrollo (dev_context.md)

## Rol de la IA

La IA actúa como **desarrollador senior de Kotlin Multiplatform** con expertise en Compose Multiplatform, Ktor,
Coroutines y arquitectura MVI/MVVM. Las respuestas reflejan ese nivel: decisiones de diseño justificadas, código
production-grade, y conocimiento profundo del codebase existente.

---

## Reglas de trabajo

### Código

- **Los archivos se presentan completos e integrables** al final de cada respuesta. Sin fragmentos parciales, sin
  pseudocódigo, sin "el resto queda igual". Si el archivo es muy grande y solo cambia una función, se presenta el
  archivo completo igualmente.
- **No se eliminan comentarios existentes** salvo que el cambio modifique intencionalmente su contenido. Los KDoc,
  comentarios de sección y anotaciones de diseño se preservan.
- **No se cambia estructura de archivos innecesariamente.** El orden de funciones, el agrupamiento de secciones y los
  separadores visuales (`// ── Sección ─────`) se mantienen salvo que el cambio lo requiera explícitamente.
- **Los comentarios nuevos describen qué hace el código**, no por qué se cambió ni qué bug resolvía. El historial de
  cambios queda en el chat, no en el código.
- **Si se necesita un archivo no subido al Proyecto para producir código correcto, se solicita antes de escribir.** No
  se asume firmas, estructuras ni implementaciones.

### Archivos faltantes

Si durante el análisis se detecta que falta un archivo del árbol del proyecto necesario para completar la tarea, se
lista claramente cuáles y por qué, y se espera que el usuario los suba antes de continuar.

### Diffs vs archivos completos

- **Diffs (recortes)** solo cuando el cambio es una sola función completa bien delimitada, o cuando el archivo tiene más
  de 500 líneas y el cambio es quirúrgico y no ambiguo. Se documenta claramente el contexto de inserción.
- **Archivos completos** en todos los demás casos.

### Presentación

- Los archivos se entregan con `present_files` al final de la respuesta.
- El resumen post-entrega es conciso: qué cambia en cada archivo, sin repetir el código.
- No se mezclan análisis y producción en la misma respuesta si son pasos separados.

### Sincronización de contexto

- El usuario actualiza los archivos del Proyecto tras cada sesión de trabajo.
- Salvo indicación contraria, la IA usa siempre los archivos del Proyecto como base, nunca los outputs
  de sesiones anteriores que no hayan sido incorporados.

---

## Estado del proyecto

### Aplicaciones locales (producción)

- **Android**: Google Play, `v1.x`, 5.000+ descargas
- **Desktop**: GitHub Releases, Linux / macOS / Windows
- Motor AI: Minimax + Alpha-Beta + transposition table, 4 niveles (Easy → Champion)
- 5 time controls, pre-moves, 12+ paletas, tutorial interactivo, biblioteca de partidas
- 400+ tests unitarios

### Plataformas pendientes (orden de prioridad)

1. **Web (Compose for Web / Kotlin WASM)** ✅ Funcional — juego corriendo en browser, matchmaking y partidas online OK.
   Ver sección "Módulo webApp" más abajo.
2. **iOS** — `IOSSettingsRepository` (NSUserDefaults), `IOSClipboardService` (UIPasteboard), Xcode project.

### Servidor online (Fase 11 completada — Junio 2026)

- **Stack**: Ktor 3.5 · PostgreSQL 16 · Redis 7.2 · Docker Compose
- **Auth**: JWT (access 15 min + refresh 7 días), BCrypt, rotation
- **WebSocket**: autenticado por JWT, mensajería via Redis List (`rpush`/`blpop`)
- **Matchmaking**: `MatchmakingEngine` con colas por time control y rated/casual, expansión de rango de rating
  por tiempo de espera. Al emparejar, llama `leaveQueue` completo para ambos usuarios (limpia todas las colas).
  `createDirectMatch()` crea partida sin cola para challenges directos.
- **Bots**: 6 bots de matchmaking (Medium×2, Hard×2, Champion×2) gestionados por `BotManager`. Cada bot tiene un
  `BotAgent` con ciclo autónomo IDLE→SEARCHING→PLAYING→IDLE. Flag `BOTS_ENABLED` (env var) desactiva todos en
  producción. Bots Easy (`Pino`, `Cala`) solo para demo local offline.
- **Sesiones**: `GameSessionManager` con estados STARTING → IN_PROGRESS → PAUSED → FINISHED.
  `playerReconnected()` reanuda sesiones PAUSED cuando ambos jugadores reconectan.
- **Rating**: Glicko-2, no se aplica a bots
- **Lobby**: 22 endpoints (6 públicos + 15 REST protegidos + 1 WebSocket)
- **Reconexión**: `ConnectionState.Reconnecting` + backoff exponencial (2→4→8→16→30s) en `ConnectionViewModel`;
  servidor reanuda sesión PAUSED y reenvía `GameStarted` o `GameStateUpdate` al reconectar
- **Errores**: `ServerErrorEvent (InvalidMove / GenericError)` como `SharedFlow` en `OnlineGameClient`;
  toasts vía `UIMessageBus` en `OnlineGameSideEffects`
- **Social**: tabla `Follows` en DB; endpoints follow/unfollow/followers/following/follow-status;
  `GET /api/leaderboard/:tc`; `GET /api/users/:id` perfil público; `GET /api/feed` partidas de seguidos
- **Challenges**: `ChallengeUser/RespondToChallenge/CancelChallenge` vía WS; timeout 30s en
  `ConnectionManager.pendingChallenges`; `ChallengeNotificationEffect` global en `AppContent`
- **Hardening**: CORS plugin, rate limiting auth (10 logins/h · 5 registros/h por IP), audit logging,
  JWT_SECRET validación 64+ chars en producción

### Protocolo cliente-servidor

- `ClientMessage`: `JoinMatchmaking`, `CancelMatchmaking`, `MakeMove`, `Resign`, `OfferDraw`, `RespondToDraw`,
  `OfferRematch`, `AcceptRematch`, `DeclineRematch`, `ChallengeUser`, `RespondToChallenge`, `CancelChallenge`,
  `SpectateGame`, `LeaveSpectating`, `Heartbeat`
- `ServerMessage`: `MatchmakingStarted`, `MatchFound(rated)`, `GameStarted`, `GameStateUpdate`, `GameEnded`,
  `DrawOffered`, `DrawDeclined`, `OpponentDisconnected`, `OpponentReconnected`, `InvalidMove`, `Error`,
  `HeartbeatAck`, `RematchOffered`, `RematchAccepted`, `RematchDeclined`, `RematchExpired`,
  `SpectatingStarted`, `SpectatorJoined`, `SpectatorLeft`,
  `ChallengeReceived`, `ChallengeDeclined`, `ChallengeExpired`
- Mensajería unidireccional servidor→cliente vía cola Redis `user:{userId}:messages` (rpush/blpop)

### Flujo de partida online (Android)

```
JoinMatchmaking → MatchmakingStarted
→ MatchFound (bot o humano emparejado)  [también puede originarse desde challenge aceptado]
→ [BotManager.activateReadySessions] → playerConnected(botId) para todos los bots de la sesión
→ [ConnectionManager.notifyPlayerConnected] → playerConnected(humanId) → ambos conectados → GameStarted
→ LaunchedEffect(status=InProgress) → viewModel.startGame(color) → tablero activo
→ LaunchedEffect(gameState) → hash diff → events.applyMove(move) → animación
→ GameEnded → LaunchedEffect(status=Finished) → viewModel.gameOver() → dialog
→ delay(30s con toast "Revancha") → clearOnlineGame() → modo local restaurado
→ Si RematchAccepted llega antes → LaunchedEffect(status=Starting) → nueva partida
```

### Lobby online (OnlineLobbyScreen)

Tres tabs:

- **Tab "En Vivo"** (índice 0): lista intercalada de partidas en curso y búsquedas abiertas, refresco cada 5s.
  Filtros: chips "En Vivo" / "Buscando", menú de ordenamiento. `LiveGameCard` con miniatura de tablero.
  `OpenSearchCard` con botón [Unirse]. `OwnSearchCard` mientras el usuario espera rival.
  TopBar: botón 🏅 (leaderboard) y 🔍 (nueva búsqueda).
- **Tab "Mis Partidas"** (índice 1): historial paginado con filtros por TC, resultado y tipo.
- **Tab "Seguidos"** (índice 2): `FeedTab` — partidas recientes de jugadores seguidos (`GET /api/feed`).
  `FeedGameCard` muestra perspectiva del jugador seguido.

### Pantallas sociales

- **`LeaderboardScreen`**: tabs por TC (Bullet/Blitz/Rapid/Classical), filas rankeadas con colores
  oro/plata/bronce, tap → perfil.
- **`PublicProfileScreen`**: header (nombre, país, bio, fecha ingreso, contadores seguidores/seguidos),
  botones Follow/Siguiendo + Desafiar (solo si no es el propio perfil), grilla de ratings 4 TCs,
  historial paginado con filtros con tap → detalles. `ChallengeDialog` para elegir TC y rated antes de enviar el
  desafío.
- Navegación: `LeaderboardDest`, `PublicProfileDest("public_profile/{userId}")` en `NavGraph`.
- **Tap en partida → detalles**: funciona en todas las listas — `HistoryGameCard`, `FeedGameCard`,
  `ProfileHistoryCard`. Patrón: `loadAndPreviewGame(gameId)` → `updateCurrentMatchDto()` →
  `navigate("game_details/$gameId")`.

### Panel lateral configurable (CompanionPanel)

En layout `Expanded` (pantallas anchas), el panel lateral muestra Lobby/Settings/Library junto al tablero.

- `CompanionPanelController` (singleton, `LocalCompositionLocal`) — estado de navegación del panel
- `CompanionPanelDestination`: `None | Lobby | Leaderboard | Profile(userId) | Settings | Library | GameDetails(gameId)`
- `CompanionPane` en `AppContent`: renderiza cada destino con callbacks internos al panel
- `NavGraph.GameScreen`: acciones `onOnlineLobby/onGamesLibrary/onNavigateToSettings` llaman `companion.navigate(...)`
  en `Expanded` y `navController.navigate(...)` en modo normal
- Todas las pantallas del panel tienen navegación completa incluyendo Leaderboard → Perfil → Partida → Detalles

### Deuda técnica conocida

| Item                     | Descripción                                                                                                                             |
|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `syncOnlineStateToLocal` | No-op intencionalmente — tablero sincronizado vía `LaunchedEffect(gameState)` → `events.applyMove` en GameScreen. Sin acción necesaria. |

### Inyección de dependencias

Koin. Platform-specific resuelto con `expect/actual`. ViewModels exponen interfaces consumidas por las pantallas.

**Regla crítica — singletons vs viewModels:**
Los ViewModels que gestionan estado de sesión compartido entre múltiples composables deben registrarse como `single`
en Koin (no `viewModel`), e inyectarse con `koinInject()` (no `koinViewModel()`). De lo contrario, la navegación
con back-stack puede crear múltiples instancias y duplicar collectors.

| ViewModel                                       | Koin        | Razón                                               |
|-------------------------------------------------|-------------|-----------------------------------------------------|
| `ConnectionViewModel`                           | `single`    | Sesión WS única; `koinInject()` en composables      |
| `OnlineGameViewModel`                           | `single`    | Un collector sobre `OnlineGameClient.currentGame`   |
| `AndroidGameViewModel` / `DesktopGameViewModel` | `viewModel` | Scope de pantalla, estado local                     |
| `OnlineLobbyViewModel`                          | `viewModel` | Scope de pantalla, polling propio                   |
| `LeaderboardViewModel`                          | `viewModel` | Scope de pantalla, carga por TC                     |
| `PublicProfileViewModel`                        | `viewModel` | Parametrizado por `userId` — `parametersOf(userId)` |

`GameViewModel` (commonMain) es la clase base abstracta compartida. `AndroidGameViewModel` agrega
`SavedStateHandle` para sobrevivir rotaciones de pantalla. `DesktopGameViewModel` la extiende con
`saveGameState()` / `persistEditingState()` como no-ops.

### Sistema de bots

```
BotService
  └── BotManager (enabled: Boolean desde BOTS_ENABLED env var)
        ├── BotAgent × 6 (Medium×2, Hard×2, Champion×2)
        │     └── ciclo: IDLE → joinQueue → SEARCHING → PLAYING → IDLE
        │           - intervalo tick: aleatorio 5-8s (TICK_INTERVAL_MIN/MAX, var para tests)
        │           - timeout de cola: 20s → decideNextStrategy (unirse a búsqueda abierta o nueva cola)
        │           - preferredRated: Boolean? (null = aleatorio, útil en tests para coincidir en cola)
        └── BotPlayer × N (uno por bot en sesión activa)
              └── clave en activePlayers: "gameId::botId" (separador :: para no romper UUIDs)
```

**Bots Easy** (`Pino`, `Cala`): `participatesInMatchmaking = false` — no entran en cola automáticamente.
Solo para demo local offline.

### Hardening de producción

Variables de entorno requeridas en producción:

```
ENVIRONMENT=production
JWT_SECRET=<openssl rand -hex 32>   # mínimo 64 chars
CORS_ALLOWED_ORIGINS=https://tudominio.com
DATABASE_PASSWORD=<password segura>
REDIS_PASSWORD=<password segura>
BOTS_ENABLED=false
```

`ServerConfig.validate()` lanza excepción al arrancar si `JWT_SECRET` usa defaults o tiene < 64 chars en
producción. `AuthRateLimiter`: ventana deslizante en memoria, 10 logins/hora y 5 registros/hora por IP.
Logger `audit.auth` registra todos los eventos de autenticación.

### Módulo `webApp` (Compose for Web / Kotlin WASM)

- **Target**: `wasmJs`, `browser()`, `binaries.executable()` — compila a WebAssembly
- **Entry point**: `Main.kt` → `ComposeViewport(document.body!!)` + `KoinApplication(webModules)`
- **AppContent**: mismo composable compartido con Android/Desktop, sin cambios
- **Módulo DI** (`WebModule.kt`): `webServiceModule` + `webDataModule` + `webViewModelModule` + `sharedModules`
- **Fuentes platform-specific** en `webApp/src/wasmJsMain/kotlin/com/agustin/tarati/web/`:
    - `WasmSettingsRepository` — `window.localStorage`
    - `WasmAuthRepository` — `window.localStorage` (tokens)
    - `WasmClipboardService` — `navigator.clipboard`
    - `WasmGameViewModel` + `WasmPlayerSettingsHolder` — sin SavedStateHandle
    - `WasmSettingsViewModel` — sin Google Play Billing/Achievements
    - `NoOpGameRepository` — biblioteca local deshabilitada en web
- **No-ops compartidos** en `commonMain`: `NoOpSoundService`, `NoOpSpecialEventManager`, `NoOpAchievementsManager`
- **Room aislado** en `roomMain` (sin artifact wasmJs) — `wasmJsMain` no hereda ese source set
- **`Dispatchers.IO`** reemplazado por `Dispatchers.Default` en `commonMain` (WASM no tiene thread pool de I/O)
- **`runBlocking`** reemplazado por `expect/actual runSync` (WASM retorna null — sin previews Compose)
- **URL del servidor**: `window.location.host` en producción; `globalThis.__TARATI_SERVER__` para override en dev (ver
  `index.html`)
- **WebSocket auth**: token enviado como query param `?token=` — los browsers no permiten headers custom en WS
- **`Move.SEPARATOR`**: cambiado de `"→"` a `"-"` (ASCII) — Skiko WASM no tiene el glifo U+2192 en su font bundle.
  `parseMoveHistory` acepta ambos para backward compat con partidas existentes
- **`CobColorIndicator`**: nuevo composable en `ui.components.game` — reemplaza `CobColor.icon` (emojis U+26AB/U+26AA
  que tampoco están en Skiko WASM). Usa `drawIndicatorPiece` con la paleta actual, igual que `BandIndicator` del sidebar
- **`GameCardItem.customRows`**: nuevo parámetro `(@Composable () -> Unit)?` para filas con contenido visual (usado en
  `LiveGameTurnRow`)
- **Build**: `./gradlew :webApp:wasmJsBrowserDevelopmentRun` · distribución: `:webApp:wasmJsBrowserDistribution`
- **Minimax cooperativo**: `getNextMove` es `suspend`. `yieldForAnimation()` en `searchBestMove` cada ≥12ms
  (nivel raíz) → `delay(1ms)` en WASM (setTimeout, cede a rAF), `yield()` en JVM. Elimina el freeze de
  animación durante el pensamiento de la IA sin afectar calidad de búsqueda.
- **`AiMoveDelay`** expect/actual: 250ms en WASM antes de cada think cycle — la animación del movimiento
  previo completa antes de que el AI ocupe el hilo principal.
- **`NotationTurnControl`**: barra de notación colapsable + `TurnIndicator`. Landscape: colapsado por defecto.
  Toma `positionNotation: String` (no `GameState`) — `toPositionNotation()` se ejecuta en `LaunchedEffect`
  async para no bloquear composition durante IA vs IA. `key(Unit)` ancla identidad del indicador.
- **`TurnIndicator`**: `tween(400)` — rotación 2.5× más rápida; `AI_THINKING` continuo cuando `isAITurn &&
  PLAYING` — dirección CW/CCW claramente visible en IA vs IA sin interrupciones.
- **Desktop `ContentNegotiation`**: `DesktopModule` ahora instala `ContentNegotiation { json() }`. Deps
  `ktor.client.content.negotiation` y `ktor.serialization.kotlinx.json` añadidas a `desktopApp/build.gradle.kts`.

**Recursos externos:**

- [kmp-awesome](https://github.com/terrakok/kmp-awesome) — lista curada de librerías KMP por categoría.
  Útil para encontrar alternativas o complementos: logging (Napier/Kermit), testing (Turbine, Mokkery),
  settings (Multiplatform-Settings), navegación (Decompose). **Nota**: el soporte wasmJs se debe
  verificar por librería — no está indicado de forma sistemática en la lista.

**Documentos relacionados**: /docs/project_status.md, /docs/next_steps.md, /docs/server_status.md

*Última actualización: Junio 2026 — Fases 1-11 + hardening + web funcional + panel lateral + fixes WASM + navegación
completa a detalles*
