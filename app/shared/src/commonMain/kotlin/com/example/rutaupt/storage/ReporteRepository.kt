package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.model.ReporteUnidad

object ReporteRepository {
    val reportes = mutableStateListOf<ReporteUnidad>()

    fun agregarReporte(reporte: ReporteUnidad) {
        reportes.add(0, reporte)
    }

    fun actualizarValidacion(id: Long, estado: String) {
        val index = reportes.indexOfFirst { it.id == id }
        if (index != -1) {
            val reporteActual = reportes[index]
            reportes[index] = reporteActual.copy(validacionAdmin = estado)
        }
    }

    fun eliminarReporte(id: Long) {
        reportes.removeAll { it.id == id }
    }

    fun limpiarReportes() {
        reportes.clear()
    }
}
