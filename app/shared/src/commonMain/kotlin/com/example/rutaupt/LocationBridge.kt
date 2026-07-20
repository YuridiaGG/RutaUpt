package com.example.rutaupt

object LocationBridge {
    var onRequestPermission: ((onResult: (Boolean) -> Unit) -> Unit)? = null
    var hasPermission: (() -> Boolean)? = null
    
    // Añadimos soporte para obtener la ubicación actual
    var getCurrentLocation: ((onResult: (Double, Double) -> Unit) -> Unit)? = null
}
