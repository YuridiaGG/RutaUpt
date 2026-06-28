package com.example.rutaupt.storage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object SessionManager {
    var nombreUsuario by mutableStateOf("")
    var emailUsuario by mutableStateOf("")
    var rolUsuario by mutableStateOf("")
    
    // Lógica para 2 reportes al día
    var reportesEnviadosHoy by mutableStateOf(0)
    var fechaUltimoReporte by mutableStateOf("")

    private fun obtenerFechaActual(): String {
        return try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            "${now.year}-${now.monthNumber}-${now.dayOfMonth}"
        } catch (e: Exception) {
            "2024-01-01" // Fallback seguro
        }
    }

    fun iniciarSesion(nombre: String, email: String, rol: String) {
        // Priorizamos el nombre proporcionado. 
        // Si el nombre parece un correo (contiene @), extraemos el prefijo.
        // Si no, lo usamos tal cual (ej. "Ejemplo")
        nombreUsuario = if (nombre.contains("@")) {
            nombre.substringBefore("@")
                .replace(".", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        } else {
            nombre.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
        
        emailUsuario = email
        rolUsuario = rol
        
        // Verificar si debemos reiniciar el contador diario
        val hoy = obtenerFechaActual()
        if (fechaUltimoReporte != hoy) {
            reportesEnviadosHoy = 0
            fechaUltimoReporte = hoy
        }
    }

    fun puedeEnviarReporte(): Boolean {
        val hoy = obtenerFechaActual()
        if (fechaUltimoReporte != hoy) {
            reportesEnviadosHoy = 0
            fechaUltimoReporte = hoy
        }
        return reportesEnviadosHoy < 2
    }

    fun registrarEnvioReporte() {
        if (puedeEnviarReporte()) {
            reportesEnviadosHoy++
            fechaUltimoReporte = obtenerFechaActual()
        }
    }

    fun cerrarSesion() {
        nombreUsuario = ""
        emailUsuario = ""
        rolUsuario = ""
        // Mantener reportesEnviadosHoy para persistencia de sesión en el mismo día
    }
}
