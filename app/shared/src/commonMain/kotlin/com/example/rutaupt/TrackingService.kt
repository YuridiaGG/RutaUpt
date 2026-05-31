package com.example.rutaupt

import kotlin.math.abs

class TrackingService {

    /**
     * Calcula de forma simple el Tiempo Estimado de Llegada (ETA) en minutos.
     * No requiere internet ni API, procesa la lógica localmente.
     */
    fun calcularTiempoEstimado(
        usuarioLat: Double,
        usuarioLon: Double,
        colectivaLat: Double,
        colectivaLon: Double
    ): Int {
        val diferenciaLat = abs(usuarioLat - colectivaLat)
        val diferenciaLon = abs(usuarioLon - colectivaLon)
        val distanciaTotal = diferenciaLat + diferenciaLon

        // Cada 0.001 grados de diferencia equivale aproximadamente a 1 minuto de trayecto en Tulancingo
        val minutosEstimados = (distanciaTotal / 0.001).toInt()

        return if (minutosEstimados < 1) 1 else minutosEstimados
    }

    /**
     * Valida si el reporte introducido por un estudiante o chofer es válido
     */
    fun validarReporte(tipoReporte: String, comentario: String): Boolean {
        if (tipoReporte.isBlank()) return false
        if (comentario.length > 200) return false
        return true
    }
}