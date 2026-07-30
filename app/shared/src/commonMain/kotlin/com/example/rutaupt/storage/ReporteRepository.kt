package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.api.RutaApiService

object ReporteRepository {
    val reportes = mutableStateListOf<ReporteUnidad>()
    private val apiService = RutaApiService()

    suspend fun cargarReportes() {
        try {
            val remoteReportes = apiService.obtenerReportes()
            reportes.clear()
            reportes.addAll(remoteReportes)
        } catch (e: Exception) {
            // Error al cargar
        }
    }

    suspend fun agregarReporte(reporte: ReporteUnidad): Boolean {
        return try {
            val success = apiService.enviarReporte(reporte)
            if (success) {
                reportes.add(0, reporte)
            }
            success
        } catch (e: Exception) {
            reportes.add(0, reporte) // Local fallback
            true
        }
    }

    suspend fun actualizarValidacion(id: Long, estado: String): Boolean {
        return try {
            val success = apiService.validarReporte(id, estado)
            if (success) {
                val index = reportes.indexOfFirst { it.id == id }
                if (index != -1) {
                    val reporteActual = reportes[index]
                    reportes[index] = reporteActual.copy(validacionAdmin = estado)
                }
            }
            success
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarReporte(id: Long): Boolean {
        return try {
            val success = apiService.eliminarReporte(id)
            if (success) {
                reportes.removeAll { it.id == id }
            }
            success
        } catch (e: Exception) {
            false
        }
    }

    fun limpiarReportes() {
        reportes.clear()
    }
}
