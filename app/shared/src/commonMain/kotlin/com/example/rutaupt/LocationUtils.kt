package com.example.rutaupt

import kotlin.math.*

object LocationUtils {
    // Tulancingo / UPT como punto de referencia
    const val DEFAULT_LAT = 20.1394
    const val DEFAULT_LON = -98.3190

    /**
     * Extrae coordenadas con alta precisión para Tulancingo/México.
     * Soporta diversos formatos de Google Maps.
     */
    fun extraerCoordenadas(link: String?): Pair<Double, Double>? {
        if (link.isNullOrBlank()) return null
        
        try {
            // 1. Patrón !3d...!4d... (Google Maps Desktop - Muy exacto)
            val regexGoogle = Regex("!3d([-+]?\\d+\\.\\d+)!4d([-+]?\\d+\\.\\d+)")
            regexGoogle.find(link)?.let { match ->
                val lat = match.groupValues[1].toDouble()
                val lon = match.groupValues[2].toDouble()
                if (lat in 14.0..33.0 && lon in -118.0..-86.0) return Pair(lat, lon)
            }

            // 2. Patrón @lat,lon (Google Maps Mobile)
            val regexAt = Regex("@([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
            regexAt.find(link)?.let { match ->
                val lat = match.groupValues[1].toDouble()
                val lon = match.groupValues[2].toDouble()
                if (lat in 14.0..33.0 && lon in -118.0..-86.0) return Pair(lat, lon)
            }

            // 3. Patrón q=lat,lon o daddr=lat,lon
            val regexQ = Regex("[qd](?:addr|)=([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
            regexQ.find(link)?.let { match ->
                val lat = match.groupValues[1].toDouble()
                val lon = match.groupValues[2].toDouble()
                if (lat in 14.0..33.0 && lon in -118.0..-86.0) return Pair(lat, lon)
            }
            
            // 4. Patrón genérico lat,lon
            val regexComma = Regex("([-+]?\\d+\\.\\d+),\\s*([-+]?\\d+\\.\\d+)")
            val matches = regexComma.findAll(link).toList()
            for (match in matches) {
                val lat = match.groupValues[1].toDouble()
                val lon = match.groupValues[2].toDouble()
                // Prioridad a lo que esté cerca de Tulancingo
                if (lat in 19.5..20.5 && lon in -99.0..-98.0) return Pair(lat, lon)
            }
            
            for (match in matches) {
                val lat = match.groupValues[1].toDouble()
                val lon = match.groupValues[2].toDouble()
                if (lat in 14.0..33.0 && lon in -118.0..-86.0) return Pair(lat, lon)
            }
        } catch (e: Exception) {}
        return null
    }

    fun calcularDistanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2).pow(2.0) + cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return r * c
    }

    fun formatoDistancia(metros: Double): String {
        return if (metros < 1000) "${metros.toInt()} m" else "${(metros / 1000.0).roundTo(1)} km"
    }

    fun calcularTiempoMinutos(metros: Double): Int {
        // Asumiendo velocidad promedio de micro en ciudad (aprox 30km/h -> 0.5km/min -> 500m/min)
        // Pero para estudiante caminando o esperando, usemos algo más conservador
        return (metros / 80.0).toInt().coerceAtLeast(1)
    }

    private fun Double.roundTo(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).toInt() / factor
    }
}
