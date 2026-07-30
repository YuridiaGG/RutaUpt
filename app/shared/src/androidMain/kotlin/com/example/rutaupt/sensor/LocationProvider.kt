package com.example.rutaupt.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.rutaupt.model.Ubicacion
import com.example.rutaupt.repository.SensorRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationProvider(private val context: Context) : SensorRepository {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getUbicacionActual(): Flow<Ubicacion> = callbackFlow {
        // 1. Intentar obtener la última ubicación conocida inmediatamente para rapidez
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                trySend(Ubicacion(it.latitude, it.longitude, it.time))
            }
        }

        // 2. Configuración de alta precisión (Ubicación Exacta)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateIntervalMillis(1000L) // Actualizaciones muy frecuentes
            .setWaitForAccurateLocation(true) // Esperar a que el GPS sea preciso
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(Ubicacion(it.latitude, it.longitude, it.time))
                }
            }
        }

        // Iniciar peticiones de actualización
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    override fun iniciarSeguimiento() {}
    override fun detenerSeguimiento() {}
}
