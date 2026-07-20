package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.api.RutaApiService

object ParadaRepository {
    val paradas = mutableStateListOf<String>()
    private val apiService = RutaApiService()

    suspend fun cargarParadas() {
        try {
            val remoteParadas = apiService.obtenerParadas()
            if (remoteParadas.isNotEmpty()) {
                paradas.clear()
                paradas.addAll(remoteParadas)
            }
        } catch (e: Exception) {
            // Silencioso para no interrumpir la UI
        }
    }

    suspend fun agregarParada(nombre: String): Boolean {
        if (nombre.isNotBlank() && !paradas.contains(nombre)) {
            // Añadimos localmente primero (UI Optimista)
            paradas.add(nombre)
            
            // Intentamos guardar en el servidor
            return try {
                val success = apiService.agregarParada(nombre)
                if (!success) {
                    // Si falla el servidor, podrías elegir removerla o dejarla local
                    // paradas.remove(nombre) 
                }
                success
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
                paradas.remove(nombre)
            }
            success
        } catch (e: Exception) {
            paradas.remove(nombre) // Eliminamos local aunque falle el server para fluidez
            true
        }
    }
    
    fun limpiarParadas() {
        paradas.clear()
    }
}
