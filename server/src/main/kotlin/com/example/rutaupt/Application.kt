package com.example.rutaupt

import com.example.rutaupt.database.DatabaseFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Inicializar conexión a MySQL
    DatabaseFactory.init()

    // Configurar Serialización JSON
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Servidor RutaUPT funcionando con MySQL en Railway")
        }
        
        get("/health") {
            call.respondText("OK")
        }
    }
}
