plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
}

group = "com.example.rutaupt"
version = "1.0.0"

application {
    mainClass = "com.example.rutaupt.ApplicationKt"
}

dependencies {
    api(projects.core)
    implementation(libs.logback)
    
    // Ktor Server
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation) // Corregido a SERVER
    implementation(libs.ktor.serialization.kotlinx.json)
    
    // Database (MySQL)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hikaricp)
    implementation(libs.mysql.connector)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
