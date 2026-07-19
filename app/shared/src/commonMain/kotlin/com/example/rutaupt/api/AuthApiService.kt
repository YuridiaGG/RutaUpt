package com.example.rutaupt.api

import com.example.rutaupt.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
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
        // Agregamos tiempo de espera extendido para el envío de correos
        install(HttpTimeout) {
            requestTimeoutMillis = 60000 // 60 segundos
            connectTimeoutMillis = 15000
            socketTimeoutMillis = 60000
        }
    }

    suspend fun login(email: String, pass: String): LoginResponse {
        return try {
            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, pass))
            }
            if (response.status.isSuccess() || response.status == HttpStatusCode.Unauthorized) {
                response.body()
            } else {
                LoginResponse(false, "Error: ${response.status.value}")
            }
        } catch (e: Exception) {
            LoginResponse(false, "Error de conexión: ${e.message}")
        }
    }

    suspend fun register(user: User): RegisterResponse {
        return try {
            val response = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            response.body()
        } catch (e: Exception) {
            RegisterResponse(false, "Error de conexión")
        }
    }

    suspend fun recoverPassword(email: String): RegisterResponse {
        return try {
            val response = client.post("$baseUrl/auth/recover") {
                contentType(ContentType.Application.Json)
                setBody(RecoveryRequest(email))
            }
            if (response.status.isSuccess()) {
                response.body()
            } else {
                val error = try { response.body<RegisterResponse>() } catch(e: Exception) { null }
                RegisterResponse(false, error?.message ?: "Error al recuperar (${response.status.value})")
            }
        } catch (e: Exception) {
            RegisterResponse(false, "Error de conexión o tiempo excedido. El servidor tardó demasiado en enviar el correo.")
        }
    }

    suspend fun obtenerUsuariosPorRol(rol: String): List<User> {
        return try {
            val response = client.get("$baseUrl/admin/users/$rol")
            if (response.status.isSuccess()) {
                response.body<List<User>>()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun eliminarUsuario(id: Int): Boolean {
        return try {
            val response = client.delete("$baseUrl/admin/users/$id")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerEstadisticasAdmin(): Map<String, Int> {
        return try {
            val response = client.get("$baseUrl/admin/stats")
            if (response.status.isSuccess()) response.body()
            else mapOf("estudiantes" to 0, "choferes" to 0, "rutas" to 0)
        } catch (e: Exception) {
            mapOf("estudiantes" to 0, "choferes" to 0, "rutas" to 0)
        }
    }
}
