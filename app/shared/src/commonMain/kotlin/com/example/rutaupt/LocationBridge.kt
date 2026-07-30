package com.example.rutaupt

object LocationBridge {
    var onRequestPermission: ((onResult: (Boolean) -> Unit) -> Unit)? = null
    var hasPermission: (() -> Boolean)? = null
    
    var getCurrentLocation: ((onResult: (Double, Double) -> Unit) -> Unit)? = null

    var onLocationUpdate: ((lat: Double, lon: Double) -> Unit)? = null
    var startLocationUpdates: (() -> Unit)? = null
    var stopLocationUpdates: (() -> Unit)? = null
}
