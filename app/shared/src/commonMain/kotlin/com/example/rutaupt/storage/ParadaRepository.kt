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
                // apiService.agregarParada ya resuelve internamente los links cortos
                // de Google Maps y regresa la Parada con el link final/expandido.
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

    suspend fun eliminarParada(nombre: String): Boolean {
        return try {
            val success = apiService.eliminarParada(nombre)
            if (success) {
                paradas.removeAll { it.nombre == nombre }
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