package com.example.rutaupt.storage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object SessionManager {
    var nombreUsuario by mutableStateOf("")
    var apellidosUsuario by mutableStateOf("")
    var emailUsuario by mutableStateOf("")
    var passwordUsuario by mutableStateOf("")
    var rolUsuario by mutableStateOf("")
    var numeroUnidad by mutableStateOf("")
    var telefonoUsuario by mutableStateOf("")
    var edadUsuario by mutableStateOf("")
    var horarioUsuario by mutableStateOf("")
    
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

    fun iniciarSesion(
        nombre: String, 
        apellidos: String, 
        email: String, 
        rol: String,
        password: String? = null,
        unidad: String? = null,
        telefono: String? = null,
        edad: String? = null,
        horario: String? = null
    ) {
        nombreUsuario = nombre.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        apellidosUsuario = apellidos.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        emailUsuario = email
        passwordUsuario = password ?: ""
        rolUsuario = rol
        numeroUnidad = unidad ?: ""
        telefonoUsuario = telefono ?: ""
        edadUsuario = edad ?: ""
        horarioUsuario = horario ?: "Sin asignar"
        
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
        apellidosUsuario = ""
        emailUsuario = ""
        passwordUsuario = ""
        rolUsuario = ""
        numeroUnidad = ""
        telefonoUsuario = ""
        edadUsuario = ""
        horarioUsuario = ""
    }
}
