package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.model.User
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.api.AuthApiService
import com.example.rutaupt.model.ReporteUnidad
import com.example.rutaupt.model.ReporteTipo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ChoferRepository {
    val choferes = mutableStateListOf<Chofer>()
    private val api = RutaApiService()
    private val authApi = AuthApiService()

    suspend fun registrarChofer(chofer: Chofer): Boolean {
        val user = User(
            nombre = chofer.nombre,
            apellidos = chofer.apellidos,
            email = chofer.email,
            password = chofer.contrasena,
            rol = "chofer",
            numeroUnidad = chofer.numeroUnidad,
            edad = chofer.edad,
            telefono = chofer.telefono
        )
        
        val response = authApi.register(user)
        if (response.success) {
            cargarDesdeServidor() 
            return true
        }
        return false
    }

    suspend fun eliminarChofer(id: Int): Boolean {
        val success = api.eliminarUsuario(id)
        if (success) {
            choferes.removeAll { it.id == id }
        }
        return success
    }

    suspend fun actualizarHorario(choferId: Int, nuevoHorario: String): Boolean {
        val choferActual = choferes.find { it.id == choferId } ?: return false
        
        val userUpdate = User(
            id = choferId,
            nombre = choferActual.nombre,
            apellidos = choferActual.apellidos,
            email = choferActual.email,
            rol = "chofer",
            numeroUnidad = choferActual.numeroUnidad,
            edad = choferActual.edad,
            telefono = choferActual.telefono,
            horario = nuevoHorario
        )

        val success = api.actualizarUsuario(userUpdate)
        if (success) {
            // Actualizar localmente
            val index = choferes.indexOfFirst { it.id == choferId }
            if (index != -1) {
                choferes[index] = choferActual.copy(horario = nuevoHorario)
            }
            
            // Notificación para el chofer
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val tiempo = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
            
            ReporteRepository.agregarReporte(
                ReporteUnidad(
                    unidad = choferActual.numeroUnidad,
                    mensaje = "Tu nuevo horario asignado es: $nuevoHorario",
                    tiempo = tiempo,
                    tipo = ReporteTipo.INFORMACION,
                    estado = "HorarioAsignado"
                )
            )
        }
        return success
    }
    
    suspend fun cargarDesdeServidor() {
        try {
            val lista = api.obtenerUsuariosPorRol("chofer")
            choferes.clear()
            lista.forEach { user ->
                choferes.add(Chofer(
                    id = user.id ?: 0,
                    nombre = user.nombre,
                    apellidos = user.apellidos,
                    email = user.email,
                    numeroUnidad = user.numeroUnidad ?: "N/A",
                    edad = user.edad ?: "",
                    telefono = user.telefono ?: "",
                    contrasena = "",
                    horario = user.horario ?: "Sin asignar"
                ))
            }
        } catch (e: Exception) {
            println("ERROR_REPOSITORY: ${e.message}")
        }
    }
}
