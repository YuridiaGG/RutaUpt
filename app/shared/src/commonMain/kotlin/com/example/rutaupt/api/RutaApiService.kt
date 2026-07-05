package com.example.rutaupt.api

import com.example.rutaupt.model.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RutaApiService {

    companion object {
        private const val BASE_URL = "https://rutaupt-production.up.railway.app"
    }

    private val client = HttpClient {
        expectSuccess = false 
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun obtenerUsuariosPorRol(rol: String): List<User> {
        return try {
            val response = client.get("$BASE_URL/api/admin/users/$rol")
            if (response.status.isSuccess()) {
                response.body<List<User>>()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerEstadisticasAdmin(): Map<String, Int> {
        return try {
            val response = client.get("$BASE_URL/api/admin/stats")
            if (response.status.isSuccess()) {
                response.body()
            } else {
                mapOf("estudiantes" to 0, "choferes" to 0, "rutas" to 0)
            }
        } catch (e: Exception) {
            mapOf("estudiantes" to 0, "choferes" to 0, "rutas" to 0)
        }
    }

    suspend fun eliminarUsuario(id: Int): Boolean {
        return try {
            val response = client.delete("$BASE_URL/api/admin/users/$id")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun actualizarUsuario(user: User): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/auth/update") { // Cambiado a un endpoint de actualización si existe
                contentType(ContentType.Application.Json)
                setBody(user)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    fun getBaseUrl() = BASE_URL
}
