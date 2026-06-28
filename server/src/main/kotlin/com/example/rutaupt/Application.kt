package com.example.rutaupt

import com.example.rutaupt.database.AuthRepository
import com.example.rutaupt.database.DatabaseFactory
import com.example.rutaupt.model.*
import com.example.rutaupt.api.EmailService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    val authRepository = AuthRepository()

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Servidor RutaUPT Online")
        }

        route("/api/auth") {
            post("/login") {
                val request = call.receive<LoginRequest>()
                val user = authRepository.findUserByEmail(request.email)
                
                if (user != null && authRepository.getUserPassword(request.email) == request.pass) {
                    call.respond(LoginResponse(true, "Login exitoso", user))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, LoginResponse(false, "Credenciales incorrectas"))
                }
            }

            post("/register") {
                val user = call.receive<User>()
                val existing = authRepository.findUserByEmail(user.email)
                
                if (existing != null) {
                    call.respond(HttpStatusCode.Conflict, RegisterResponse(false, "El correo ya está registrado"))
                } else {
                    val success = authRepository.registerUser(user)
                    if (success) {
                        call.respond(RegisterResponse(true, "Usuario registrado con éxito"))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, RegisterResponse(false, "Error al guardar en la base de datos"))
                    }
                }
            }

            post("/recover") {
                val request = call.receive<RecoveryRequest>()
                val password = authRepository.getUserPassword(request.email)
                
                if (password != null) {
                    val emailSent = EmailService.sendPasswordRecoveryEmail(request.email, password)
                    if (emailSent) {
                        call.respond(RegisterResponse(true, "Correo enviado con éxito"))
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, RegisterResponse(false, "Error al enviar el correo"))
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, RegisterResponse(false, "El correo no está registrado"))
                }
            }
        }
    }
}
