package com.example.rutaupt.api

import com.example.rutaupt.model.Route
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RutaApiService {
    private val baseUrl = "https://rutaupt-production.up.railway.app/api"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignora campos extras que envíe el servidor
                prettyPrint = true
                isLenient = true
            })
        }
    }

    /**
     * Obtiene las rutas reales desde el backend en Railway.
     */
    suspend fun obtenerRutas(): List<Route> {
        return try {
            client.get("$baseUrl/rutas").body()
        } catch (e: Exception) {
            println("Error al conectar con la API: ${e.message}")
            emptyList() // Devuelve lista vacía en caso de error
        }
    }
    
    fun getBaseUrl() = baseUrl
}
