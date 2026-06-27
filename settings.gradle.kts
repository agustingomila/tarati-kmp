pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tarati"
include(":shared")

// Módulos cliente: se incluyen solo si su directorio existe. El build Docker del
// server copia únicamente `shared` y `server`, por lo que estos se omiten ahí.
// Gradle 9.x falla en duro al incluir un proyecto cuyo directorio no existe
// (a diferencia de Gradle 8.x, que lo toleraba).
listOf("androidApp", "desktopApp", "webApp").forEach { module ->
    if (java.io.File(settingsDir, module).isDirectory) include(":$module")
}