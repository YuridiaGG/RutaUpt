package com.example.rutaupt

import kotlin.math.abs

class TrackingService {


    fun calcularTiempoEstimado(
        usuarioLat: Double,
        usuarioLon: Double,
        colectivaLat: Double,
        colectivaLon: Double
    ): Int {
        val diferenciaLat = abs(usuarioLat - colectivaLat)
        val diferenciaLon = abs(usuarioLon - colectivaLon)
        val distanciaTotal = diferenciaLat + diferenciaLon

        val minutosEstimados = (distanciaTotal / 0.001).toInt()

        return if (minutosEstimados < 1) 1 else minutosEstimados
    }


    fun validarReporte(tipoReporte: String, comentario: String): Boolean {
        if (tipoReporte.isBlank()) return false
        if (comentario.length > 200) return false
        return true
    }
}