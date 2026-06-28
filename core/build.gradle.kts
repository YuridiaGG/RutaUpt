import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    // Declaramos el plugin pero NO lo aplicamos automáticamente para evitar fallos en Railway
    alias(libs.plugins.androidMultiplatformLibrary) apply false
}

// Detectamos si estamos en el entorno de Railway (donde no hay SDK de Android)
val isRailwayBuild = System.getenv("RAILWAY_ENVIRONMENT_NAME") != null || System.getenv("PORT") != null

// Aplicamos el plugin de Android SOLO si NO estamos en Railway
if (!isRailwayBuild) {
    apply(plugin = "com.android.kotlin.multiplatform.library")
}

kotlin {
    // NOTA: Se eliminó androidTarget() porque el plugin 'androidMultiplatformLibrary' ya lo crea automáticamente.
    
    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    // Configuración segura: solo se ejecuta si el plugin de Android está presente (entorno local)
    plugins.withId("com.android.kotlin.multiplatform.library") {
        configure<com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension> {
            namespace = "com.example.rutaupt.core"
            compileSdk = 34
            minSdk = 24
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
