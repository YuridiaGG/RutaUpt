package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.api.RutaApiService

object ReporteRepository {
    val reportes = mutableStateListOf<ReporteUnidad>()
    private val apiService = RutaApiService()
    
    // Almacenamos el último ID notificado aquí para que persista al cambiar de pantalla
    var ultimoIdNotificado = 0L

    suspend fun cargarReportes() {
        try {
            val remoteReportes = apiService.obtenerReportes()
            if (remoteReportes.isEmpty() && reportes.isNotEmpty()) return

            // Actualización inteligente: Evita limpiar y recargar todo para no perder estados visuales
            val nuevosIds = remoteReportes.map { it.id }.toSet()
            
            // 1. Eliminar los que ya no están en el servidor
            val idsAQuitar = reportes.map { it.id }.filter { it !in nuevosIds }
            reportes.removeAll { it.id in idsAQuitar }

            // 2. Actualizar o Añadir
            remoteReportes.forEach { remote ->
                val index = reportes.indexOfFirst { it.id == remote.id }
                if (index != -1) {
                    // Actualizamos si algo cambió (especialmente validacionAdmin)
                    if (reportes[index] != remote) {
                        reportes[index] = remote
                    }
                } else {
                    // Es nuevo, lo añadimos al inicio (asumiendo orden cronológico)
                    reportes.add(0, remote)
                }
            }
            
            // Ordenar por ID descendente para asegurar cronología
            reportes.sortByDescending { it.id }
            
        } catch (e: Exception) {
            // Silencioso
        }
    }

    suspend fun agregarReporte(reporte: ReporteUnidad): Boolean {
        return try {
            val success = apiService.enviarReporte(reporte)
            if (success) {
                // No lo añadimos manualmente si el loop de carga lo va a traer, 
                // pero lo hacemos por feedback instantáneo
                if (reportes.none { it.id == reporte.id }) {
                    reportes.add(0, reporte)
                }
            }
            success
        } catch (e: Exception) {
            false
        }
    }

    suspend fun actualizarValidacion(id: Long, estado: String): Boolean {
        return try {
            val success = apiService.validarReporte(id, estado)
            if (success) {
                val index = reportes.indexOfFirst { it.id == id }
                if (index != -1) {
                    reportes[index] = reportes[index].copy(validacionAdmin = estado)
                }
                return true
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarReporte(id: Long): Boolean {
        return try {
            val success = apiService.eliminarReporte(id)
            if (success) {
                reportes.removeAll { it.id == id }
                return true
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun limpiarReportes() {
        reportes.clear()
        ultimoIdNotificado = 0L
    }
}
