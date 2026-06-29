package com.example.rutaupt.api

import com.example.rutaupt.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthApiService {
    private val baseUrl = "https://rutaupt-production.up.railway.app/api"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun login(email: String, pass: String): LoginResponse {
        return try {
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, pass))
            }
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body()
            } else {
                LoginResponse(false, "Servidor: Error de autenticación (${response.status.value})")
            }
        } catch (e: Exception) {
            LoginResponse(false, "Error de conexión: Verifique su internet")
        }
    }

    suspend fun register(user: User): RegisterResponse {
        return try {
            val response = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body()
            } else {
                RegisterResponse(false, "Error del servidor: El servicio de registro no respondió correctamente.")
            }
        } catch (e: Exception) {
            RegisterResponse(false, "Fallo al comunicar con el servidor")
        }
    }

    suspend fun recoverPassword(email: String): RegisterResponse {
        return try {
            val response = client.post("$baseUrl/auth/recover") {
                contentType(ContentType.Application.Json)
                setBody(RecoveryRequest(email))
            }
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                response.body()
            } else {
                RegisterResponse(false, "Error: No se pudo procesar la recuperación (Status: ${response.status.value})")
            }
        } catch (e: Exception) {
            RegisterResponse(false, "Error de red al recuperar contraseña")
        }
    }

    /**
     * NUEVA FUNCIÓN: Trae los choferes desde Railway para el panel de administración
     */
    suspend fun obtenerChoferes(): List<User> {
        return try {
            val response = client.get("$baseUrl/usuarios/choferes")
            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("Error al obtener choferes en AuthApiService: ${e.message}")
            emptyList()
        }
    }
}