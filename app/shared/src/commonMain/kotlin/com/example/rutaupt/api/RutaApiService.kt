package com.example.rutaupt.api
class RutaApiService {

    private val baseUrl = "https://rutaupt-production.up.railway.app/api"

    suspend fun obtenerRutas(): String {
        return """
            [
                {"id_ruta": 1, "nombre_ruta": "Ruta Centro - UPT", "color": "#FF5733", "activa": true},
                {"id_ruta": 2, "nombre_ruta": "Ruta Central - UPT", "color": "#33FF57", "activa": true}
            ]
        """.trimIndent()
    }
}