package com.example.rutaupt

object LocationBridge {
    var onRequestPermission: ((onResult: (Boolean) -> Unit) -> Unit)? = null
    var hasPermission: (() -> Boolean)? = null
    
    // Obtener la ubicación una sola vez
    var getCurrentLocation: ((onResult: (Double, Double) -> Unit) -> Unit)? = null

    // Flujo para actualizaciones en tiempo real (Punto azul que se mueve)
    var onLocationUpdate: ((lat: Double, lon: Double) -> Unit)? = null
    var startLocationUpdates: (() -> Unit)? = null
    var stopLocationUpdates: (() -> Unit)? = null
}
