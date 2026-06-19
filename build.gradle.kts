plugins {
    // Android
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false

    // Kotlin
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.21" apply false

    // Room & KSP
    id("androidx.room") version "2.8.4" apply false
    id("com.google.devtools.ksp") version "2.2.21-2.0.5" apply false

    // Quality
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    kotlin("jvm")
}

// Forzar ws 8.20.1 para evitar CVE-2026-45736 (uninitialized memory disclosure en websocket.close).
// ws es dependencia directa que KGP inyecta en los sub-packages generados (build/wasm/packages/*/package.json).
// La resolución en el package.json raíz del workspace de yarn overridea todas las sub-declaraciones.
// Kotlin 2.x usa dos instancias de yarn independientes (JS y Wasm).
//
// Nota: the<T>() dentro de plugins.withType<P> { } resuelve en el scope del plugin, no del project —
// por eso se accede a la extensión explícitamente via rootProject.extensions.
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.extensions.getByType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>()
        .resolution("ws", "8.20.1")
}
plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin> {
    rootProject.extensions.getByType<org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension>()
        .resolution("ws", "8.20.1")
}