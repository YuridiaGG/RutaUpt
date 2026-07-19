package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.rutaupt.api.RutaApiService

object ParadaRepository {
    // Exponemos el SnapshotStateList directamente para asegurar reactividad en Compose
    val paradas = mutableStateListOf<String>()
    
    private val apiService = RutaApiService()

    suspend fun cargarParadas() {
        try {
            val remoteParadas = apiService.obtenerParadas()
            paradas.clear()
            paradas.addAll(remoteParadas)
        } catch (e: Exception) {
            // Manejar error si es necesario
        }
    }

    suspend fun agregarParada(nombre: String): Boolean {
        if (nombre.isNotBlank() && !paradas.contains(nombre)) {
            val success = apiService.agregarParada(nombre)
            if (success) {
                // Si el backend responde éxito, lo añadimos localmente
                paradas.add(nombre)
                return true
            }
        }
        return false
    }

    suspend fun eliminarParada(nombre: String): Boolean {
        val success = apiService.eliminarParada(nombre)
        if (success) {
            paradas.remove(nombre)
            return true
        }
        return false
    }
    
    fun limpiarParadas() {
        paradas.clear()
    }
}
