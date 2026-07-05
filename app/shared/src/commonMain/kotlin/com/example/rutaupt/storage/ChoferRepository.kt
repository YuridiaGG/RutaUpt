package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.model.Chofer
import com.example.rutaupt.model.User
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.api.AuthApiService

object ChoferRepository {
    val choferes = mutableStateListOf<Chofer>()
    private val api = RutaApiService()
    private val authApi = AuthApiService()

    /**
     * Registra un nuevo chofer en el servidor y lo agrega a la lista local
     */
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

    /**
     * Elimina el chofer tanto localmente como en la base de datos
     */
    suspend fun eliminarChofer(id: Int): Boolean {
        val success = api.eliminarUsuario(id)
        if (success) {
            choferes.removeAll { it.id == id }
        }
        return success
    }
    
    /**
     * Carga la lista actualizada desde el servidor (Railway)
     */
    suspend fun cargarDesdeServidor() {
        try {
            // Usamos el servicio unificado
            val lista = api.obtenerUsuariosPorRol("chofer")
            
            // Actualizamos la lista observable de forma que Compose lo detecte
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
                    horario = "A.M." // Por defecto, se gestionará en la pantalla de horarios
                ))
            }
        } catch (e: Exception) {
            println("ERROR_REPOSITORY: ${e.message}")
        }
    }
}
