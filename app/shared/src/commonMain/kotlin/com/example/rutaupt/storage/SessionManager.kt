package com.example.rutaupt.storage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rutaupt.getPlatform
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
    
    var notificacionesVistasCount by mutableStateOf(0)
    var reportesEnviadosHoy by mutableStateOf(0)
    var fechaUltimoReporte by mutableStateOf("")

    private val platform = getPlatform()

    private fun obtenerFechaActual(): String {
        return try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            "${now.year}-${now.monthNumber}-${now.dayOfMonth}"
        } catch (e: Exception) { "2024-01-01" }
    }

    fun cargarSesionPersistida() {
        rolUsuario = platform.getString("rol_usuario") ?: ""
        if (rolUsuario.isNotEmpty()) {
            nombreUsuario = platform.getString("nombre_usuario") ?: ""
            apellidosUsuario = platform.getString("apellidos_usuario") ?: ""
            emailUsuario = platform.getString("email_usuario") ?: ""
            passwordUsuario = platform.getString("password_usuario") ?: ""
            numeroUnidad = platform.getString("numero_unidad") ?: ""
            telefonoUsuario = platform.getString("telefono_usuario") ?: ""
            edadUsuario = platform.getString("edad_usuario") ?: ""
            horarioUsuario = platform.getString("horario_usuario") ?: "Sin asignar"
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
        
        // Persistir datos localmente
        platform.saveString("nombre_usuario", nombreUsuario)
        platform.saveString("apellidos_usuario", apellidosUsuario)
        platform.saveString("email_usuario", emailUsuario)
        platform.saveString("password_usuario", passwordUsuario)
        platform.saveString("rol_usuario", rolUsuario)
        platform.saveString("numero_unidad", numeroUnidad)
        platform.saveString("telefono_usuario", telefonoUsuario)
        platform.saveString("edad_usuario", edadUsuario)
        platform.saveString("horario_usuario", horarioUsuario)

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
        notificacionesVistasCount = 0
        
        // Limpiar persistencia local
        platform.clearSettings()
    }
}
