package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.api.Parada

object ParadaRepository {
    val paradas = mutableStateListOf<Parada>()
    private val apiService = RutaApiService()

    suspend fun cargarParadas() {
        try {
            val remoteParadas = apiService.obtenerParadas()
            paradas.clear()
            paradas.addAll(remoteParadas)
        } catch (e: Exception) {
            // Silencioso
        }
    }

    suspend fun agregarParada(nombre: String, ubicacion: String? = null): Boolean {
        if (nombre.isNotBlank()) {
            return try {
                val paradaGuardada = apiService.agregarParada(nombre, ubicacion)
                if (paradaGuardada != null) {
                    paradas.add(paradaGuardada)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    suspend fun actualizarParada(parada: Parada): Boolean {
        return try {
            val success = apiService.actualizarParada(parada)
            if (success) {
                val index = paradas.indexOfFirst { it.id == parada.id }
                if (index != -1) {
                    paradas[index] = parada
                }
            }
            success
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarParada(id: Long): Boolean {
        return try {
            val success = apiService.eliminarParada(id)
            if (success) {
                paradas.removeAll { it.id == id }
            }
            success
        } catch (e: Exception) {
            false
        }
    }

    fun limpiarParadas() {
        paradas.clear()
    }
}
