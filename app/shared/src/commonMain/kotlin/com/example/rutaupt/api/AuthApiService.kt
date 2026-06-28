package com.example.rutaupt.api

import com.example.rutaupt.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthApiService {
    private val baseUrl = "https://rutaupt-production.up.railway.app/api/auth"

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
            val response = client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, pass))
            }
            response.body()
        } catch (e: Exception) {
            LoginResponse(false, "Error de conexión: ${e.message}")
        }
    }

    suspend fun register(user: User): RegisterResponse {
        return try {
            val response = client.post("$baseUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            response.body()
        } catch (e: Exception) {
            RegisterResponse(false, "Error de registro: ${e.message}")
        }
    }

    suspend fun recoverPassword(email: String): RegisterResponse {
        return try {
            val response = client.post("$baseUrl/recover") {
                contentType(ContentType.Application.Json)
                setBody(RecoveryRequest(email))
            }
            response.body()
        } catch (e: Exception) {
            RegisterResponse(false, "Error de conexión: ${e.message}")
        }
    }
}
