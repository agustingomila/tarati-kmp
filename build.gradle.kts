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

// Forzar ws >= 8.20.1 para evitar CVE-2026-45736 (uninitialized memory disclosure en websocket.close).
// ws es dependencia transitiva de webpack-dev-server usada solo durante el build WASM.
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().resolution("ws", ">=8.20.1")
}