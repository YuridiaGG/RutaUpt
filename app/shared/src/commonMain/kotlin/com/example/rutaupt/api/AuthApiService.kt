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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class VerifyCodeRequest(
    val email: String,
    val code: String
)

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
        install(HttpTimeout) {
            requestTimeoutMillis = 60000 
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
            // El servidor ahora generará un código de 6 dígitos y lo enviará al correo
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
            RegisterResponse(false, "Error de conexión o tiempo excedido.")
        }
    }

    suspend fun verifyCode(email: String, code: String): LoginResponse {
        return try {
            val response = client.post("$baseUrl/auth/verify-code") {
                contentType(ContentType.Application.Json)
                setBody(VerifyCodeRequest(email, code))
            }
            if (response.status.isSuccess()) {
                response.body()
            } else {
                val error = try { response.body<LoginResponse>() } catch(e: Exception) { null }
                LoginResponse(false, error?.message ?: "Código incorrecto o expirado")
            }
        } catch (e: Exception) {
            LoginResponse(false, "Error al verificar el código")
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
