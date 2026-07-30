package com.example.rutaupt.api

import com.example.rutaupt.model.User
import com.example.rutaupt.model.ReporteUnidad
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Parada(
    val id: Int? = null,
    val nombre: String,
    val ubicacion: String? = null
)

@Serializable
data class ValidacionRequest(
    val estado: String
)

@Serializable
data class UbicacionVehiculo(
    val unidad: String,
    val latitud: Double,
    val longitud: Double,
    val ultimaActualizacion: Long = 0L
)

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

    // --- Usuarios ---
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

    // --- Paradas ---
    suspend fun obtenerParadas(): List<Parada> {
        return try {
            val response = client.get("$BASE_URL/api/paradas")
            if (response.status.isSuccess()) {
                response.body<List<Parada>>()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarParada(nombre: String, ubicacion: String? = null): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/paradas") {
                contentType(ContentType.Application.Json)
                setBody(Parada(nombre = nombre, ubicacion = ubicacion))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarParada(nombre: String): Boolean {
        return try {
            val response = client.delete("$BASE_URL/api/paradas/$nombre")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    // --- Reportes ---
    suspend fun obtenerReportes(): List<ReporteUnidad> {
        return try {
            val response = client.get("$BASE_URL/api/reportes")
            if (response.status.isSuccess()) {
                response.body<List<ReporteUnidad>>()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun enviarReporte(reporte: ReporteUnidad): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/reportes") {
                contentType(ContentType.Application.Json)
                setBody(reporte)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun validarReporte(id: Long, estado: String): Boolean {
        return try {
            val response = client.put("$BASE_URL/api/reportes/$id/validar") {
                contentType(ContentType.Application.Json)
                setBody(ValidacionRequest(estado))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarReporte(id: Long): Boolean {
        return try {
            val response = client.delete("$BASE_URL/api/reportes/$id")
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    // --- Ubicación en Tiempo Real ---
    suspend fun actualizarUbicacion(unidad: String, lat: Double, lon: Double): Boolean {
        return try {
            val response = client.post("$BASE_URL/api/ubicacion") {
                contentType(ContentType.Application.Json)
                setBody(UbicacionVehiculo(unidad, lat, lon))
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerUbicaciones(): List<UbicacionVehiculo> {
        return try {
            val response = client.get("$BASE_URL/api/ubicacion")
            if (response.status.isSuccess()) {
                response.body<List<UbicacionVehiculo>>()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerUbicacionUnidad(unidad: String): UbicacionVehiculo? {
        return try {
            val response = client.get("$BASE_URL/api/ubicacion/$unidad")
            if (response.status.isSuccess()) {
                response.body<UbicacionVehiculo>()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // --- Otros ---
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
            val response = client.post("$BASE_URL/api/auth/update") {
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
