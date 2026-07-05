package com.example.rutaupt.storage

import androidx.compose.runtime.mutableStateListOf
import com.example.rutaupt.api.AuthApiService
import com.example.rutaupt.model.User

object EstudianteRepository {
    val estudiantes = mutableStateListOf<User>()
    private val authApi = AuthApiService()

    suspend fun cargarDesdeServidor() {
        try {
            val lista = authApi.obtenerUsuariosPorRol("estudiante")
            estudiantes.clear()
            estudiantes.addAll(lista)
            println("DEBUG: Estudiantes cargados: ${estudiantes.size}")
        } catch (e: Exception) {
            println("Error al cargar estudiantes: ${e.message}")
        }
    }

    suspend fun eliminarEstudiante(id: Int): Boolean {
        val exito = authApi.eliminarUsuario(id)
        if (exito) {
            estudiantes.removeAll { it.id == id }
        }
        return exito
    }
}
