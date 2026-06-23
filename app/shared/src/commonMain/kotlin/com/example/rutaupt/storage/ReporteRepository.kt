package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.screens.ReporteUnidad
import com.example.rutaupt.screens.ReporteTipo

object ReporteRepository {
    val reportes = mutableStateListOf<ReporteUnidad>(
        ReporteUnidad("UPT-05", "Unidad llena en Parada La Joya", "Hace 10 minutos", ReporteTipo.ALERTA),
        ReporteUnidad("UPT-12", "Retraso por tráfico intenso", "Hace 25 minutos", ReporteTipo.INFORMACION)
    )

    fun agregarReporte(reporte: ReporteUnidad) {
        reportes.add(0, reporte)
    }
}
