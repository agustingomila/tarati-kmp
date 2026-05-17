plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Kotlin
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.atomicfu)

            // Room Runtime (solo runtime en common)
            implementation(libs.androidx.room.runtime)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose.multiplatform)
            implementation(libs.koin.compose.viewmodel)

            // UUID
            implementation(libs.uuid)

            // Compose Multiplatform — usando libs en lugar de compose accessor
            implementation(libs.androidx.compose.runtime)
            implementation(libs.androidx.compose.foundation)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.components.resources)
            implementation(libs.androidx.compose.navigation)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            // Room KTX (solo en Android, tiene dependencias Android-specific)
            implementation(libs.androidx.room.ktx)
            // UI Tooling para previews en Android
            implementation(libs.androidx.compose.uiTooling)
        }

        iosMain.dependencies {
            // iOS-specific dependencies si son necesarias
        }

        // Logger para JVM (Desktop + Server)
        jvmMain.dependencies {
            // Sin dependencias extra — JVM tiene todo
        }
    }
}

android {
    namespace = "com.agustin.tarati.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    compose.resources {
        publicResClass = true
        packageOfResClass = "com.agustin.tarati.shared.generated.resources"
        generateResClass = always
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

// Room Configuration
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Room KSP para Android
    add("kspAndroid", libs.androidx.room.compiler)

    // Room KSP para iOS
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)

    // Room KSP para Desktop/JVM
    add("kspJvm", libs.androidx.room.compiler)
}