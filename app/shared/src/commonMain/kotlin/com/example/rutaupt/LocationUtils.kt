package com.example.rutaupt

import kotlin.math.*

object LocationUtils {
    // Coordenadas base (Tulancingo / UPT) para evitar el mapamundi vacío
    const val DEFAULT_LAT = 20.1394
    const val DEFAULT_LON = -98.3190

    /**
     * Extrae coordenadas de CUALQUIER link de Google Maps.
     */
    fun extraerCoordenadas(link: String?): Pair<Double, Double>? {
        if (link.isNullOrBlank()) return null
        try {
            // 1. Patrón lat,lon (ej: 20.123, -98.456)
            val regexComma = Regex("([-+]?\\d+\\.\\d+),\\s*([-+]?\\d+\\.\\d+)")
            val matchComma = regexComma.findAll(link).lastOrNull()
            if (matchComma != null) {
                val lat = matchComma.groupValues[1].toDouble()
                val lon = matchComma.groupValues[2].toDouble()
                if (lat in -90.0..90.0 && lon in -180.0..180.0) return Pair(lat, lon)
            }

            // 2. Patrón de URL interna de Google (!3dLat!4dLon)
            val regexGoogle = Regex("!3d([-+]?\\d+\\.\\d+)!4d([-+]?\\d+\\.\\d+)")
            val matchGoogle = regexGoogle.find(link)
            if (matchGoogle != null) {
                val lat = matchGoogle.groupValues[1].toDouble()
                val lon = matchGoogle.groupValues[2].toDouble()
                if (lat in -90.0..90.0 && lon in -180.0..180.0) return Pair(lat, lon)
            }
            
            // 3. Patrón con @ (formato móvil)
            val regexAt = Regex("@([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
            val matchAt = regexAt.find(link)
            if (matchAt != null) {
                return Pair(matchAt.groupValues[1].toDouble(), matchAt.groupValues[2].toDouble())
            }
        } catch (e: Exception) {}
        return null
    }

    fun calcularDistanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Radio Tierra metros
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2).pow(2.0) + cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return r * c
    }

    fun formatoDistancia(metros: Double): String {
        return if (metros < 1000) "${metros.toInt()} m" else "${(metros / 1000.0).roundToOne(1)} km"
    }

    fun calcularTiempoMinutos(metros: Double): Int {
        return (metros / 80.0).toInt().coerceAtLeast(1)
    }

    private fun Double.roundToOne(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).toInt() / factor
    }
}
