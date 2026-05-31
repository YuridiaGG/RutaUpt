package com.example.rutaupt.storage

class LocalSettings {

    // Simulación de un almacenamiento en memoria (SharedPreferences local)
    private val localStorage = mutableMapOf<String, String>()

    /**
     * Guarda el ID del rol (Estudiante, Chofer, Admin) localmente
     */
    fun guardarRolUsuario(idRol: Int) {
        localStorage["id_rol"] = idRol.toString()
    }

    /**
     * Obtiene el rol guardado para saber qué pantalla mostrar al abrir la app
     */
    fun obtenerRolUsuario(): Int {
        val rol = localStorage["id_rol"] ?: "0"
        return rol.toInt()
    }

    /**
     * Borra los datos locales cuando el usuario cierra sesión
     */
    fun limpiarSesion() {
        localStorage.clear()
    }
}