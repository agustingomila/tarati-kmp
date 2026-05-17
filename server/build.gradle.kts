plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "3.4.3"
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("com.agustin.tarati.server.ApplicationKt")
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    implementation(libs.exposed.core)
    implementation(libs.postgresql)

    implementation(libs.logback)
}