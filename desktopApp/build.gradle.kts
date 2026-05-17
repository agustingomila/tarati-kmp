import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

// ═══════════════════════════════════════════════════════════════════════════
// Versión desde properties de Gradle (pasadas por workflow con -P)
// ═══════════════════════════════════════════════════════════════════════════
val appVersionName = project.findProperty("versionName")?.toString() ?: "1.0.0"
val appVersionCode = project.findProperty("versionCode")?.toString() ?: "1"

println("🖥️  Desktop Version: $appVersionName (build $appVersionCode)")

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.components.resources)

    implementation(libs.koin.core)
    implementation(libs.koin.compose.multiplatform)
    implementation(libs.koin.compose.viewmodel)

    // Coroutines Swing - CRITICAL for Desktop Main dispatcher
    implementation(libs.kotlinx.coroutines.swing)

    // SQLite driver for Room on Desktop
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.sqlite)
}

compose.desktop {
    application {
        mainClass = "com.agustin.tarati.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "Tarati"
            packageVersion = appVersionName  // Usa version.properties via -PversionName
            description = "Tarati - A strategic board game by George Spencer-Brown"
            copyright = "© 2026 Agustin Gomila. All rights reserved."
            vendor = "Agustin Gomila"

            // Módulos JDK requeridos
            modules("java.sql", "java.naming", "jdk.unsupported")

            // Windows
            windows {
                val iconFile = project.file("src/main/resources/icons/tarati.ico")
                if (iconFile.exists()) {
                    this.iconFile.set(iconFile)
                }

                menuGroup = "Tarati"
                shortcut = true
                dirChooser = true
                perUserInstall = false
            }

            // macOS
            macOS {
                val iconFile = project.file("src/main/resources/icons/tarati.icns")
                if (iconFile.exists()) {
                    this.iconFile.set(iconFile)
                }

                bundleID = "com.agustin.tarati.desktop"
                appStore = false
                appCategory = "public.app-category.games"
            }

            // Linux
            linux {
                val iconFile = project.file("src/main/resources/icons/tarati.png")
                if (iconFile.exists()) {
                    this.iconFile.set(iconFile)
                }

                appCategory = "Game"
                debMaintainer = "tarati.gameboard@gmail.com"
                menuGroup = "Games"
                appRelease = "1"
            }

            // Argumentos JVM
            jvmArgs += listOf(
                "-Xmx2G",
                "-Dfile.encoding=UTF-8"
            )
        }
    }
}