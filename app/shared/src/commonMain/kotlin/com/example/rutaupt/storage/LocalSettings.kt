package com.example.rutaupt.storage

class LocalSettings {

    private val localStorage = mutableMapOf<String, String>()

    fun guardarRolUsuario(idRol: Int) {
        localStorage["id_rol"] = idRol.toString()
    }

    fun obtenerRolUsuario(): Int {
        val rol = localStorage["id_rol"] ?: "0"
        return rol.toInt()
    }

    fun limpiarSesion() {
        localStorage.clear()
    }
}