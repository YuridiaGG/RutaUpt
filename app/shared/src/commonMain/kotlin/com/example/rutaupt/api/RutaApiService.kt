package com.example.rutaupt.api

// Aquí irían las librerías de Ktor una vez configuradas en el build.gradle
class RutaApiService {

    // URL base de tu backend en Railway
    private val baseUrl = "https://rutaupt-production.up.railway.app/api"

    /**
     * Simula u organiza la petición HTTP GET para obtener las rutas desde Railway
     */
    suspend fun obtenerRutas(): String {
        // En una implementación completa aquí se usa: client.get("$baseUrl/rutas")
        // Por ahora, devolvemos un JSON simulado para comprobar que la lógica funciona
        return """
            [
                {"id_ruta": 1, "nombre_ruta": "Ruta Centro - UPT", "color": "#FF5733", "activa": true},
                {"id_ruta": 2, "nombre_ruta": "Ruta Central - UPT", "color": "#33FF57", "activa": true}
            ]
        """.trimIndent()
    }
}