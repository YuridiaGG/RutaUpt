import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Cargar local.properties de forma ultra-segura
val localProperties = Properties()
val localPropertiesFile = rootProject.projectDir.resolve("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
// Limpiamos comillas o espacios por si acaso
val mapsApiKey = localProperties.getProperty("MAPS_API_KEY")?.trim()?.removeSurrounding("\"") ?: ""

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(projects.app.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.preference)
    implementation(libs.osmdroid)
    implementation(libs.play.services.location)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.example.rutaupt"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.rutaupt"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        
        // Inyectar la llave limpia en el Manifest
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }
    
    // Esto permite que el código Kotlin también vea la llave si la necesita
    buildTypes {
        getByName("debug") {
            buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        }
    }
    
    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
