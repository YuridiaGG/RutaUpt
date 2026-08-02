package com.example.rutaupt.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
enum class ReporteTipo {
    ALERTA, INFORMACION
}

@Serializable
data class ReporteUnidad(
    val id: Long = 0, // El servidor suele asignar este ID
    val unidad: String? = null,
    val mensaje: String? = null,
    val tiempo: String? = null,
    val tipo: ReporteTipo = ReporteTipo.INFORMACION,
    val imagen: String? = null,
    val estado: String? = null,
    var validacionAdmin: String? = null,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds() // Campo real para la fecha
)
