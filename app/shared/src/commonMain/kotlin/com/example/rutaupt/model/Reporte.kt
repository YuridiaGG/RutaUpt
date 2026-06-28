package com.example.rutaupt.model

import kotlinx.datetime.Clock

enum class ReporteTipo {
    ALERTA, INFORMACION
}

data class ReporteUnidad(
    val id: Long = Clock.System.now().toEpochMilliseconds(),
    val unidad: String,
    val mensaje: String,
    val tiempo: String,
    val tipo: ReporteTipo,
    val imagen: String? = null,
    val estado: String? = null,
    var validacionAdmin: String? = null // "VALIDADO", "RECHAZADO" o null
)
