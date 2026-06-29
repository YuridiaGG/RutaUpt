package com.example.rutaupt.api

import com.example.rutaupt.model.Route
import com.example.rutaupt.model.User // Aseguramos la importación de tu modelo de Usuario
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RutaApiService {

    companion object {
        private const val BASE_URL = "https://rutaupt-production.up.railway.app"
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
    }

    /**
     * Obtiene las rutas desde el backend en Railway
     */
    suspend fun obtenerRutas(): List<Route> {
        return try {
            client.get("$BASE_URL/api/rutas").body()
        } catch (e: Exception) {
            println("Error al conectar con la API de rutas: ${e.message}")
            emptyList()
        }
    }

    /**
     * Obtiene los usuarios con rol de chofer desde el backend en Railway
     */
    suspend fun obtenerChoferes(): List<User> {
        return try {
            client.get("$BASE_URL/api/usuarios/choferes").body()
        } catch (e: Exception) {
            println("Error al obtener choferes de la API: ${e.message}")
            emptyList()
        }
    }

    fun getBaseUrl() = BASE_URL
}