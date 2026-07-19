package com.example.rutaupt.sensor

import android.annotation.SuppressLint
import android.content.Context
import com.example.rutaupt.model.Ubicacion
import com.example.rutaupt.repository.SensorRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationProvider(context: Context) : SensorRepository {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getUbicacionActual(): Flow<Ubicacion> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    trySend(Ubicacion(it.latitude, it.longitude, it.time))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    override fun iniciarSeguimiento() {
        // Implementación de inicio
    }

    override fun detenerSeguimiento() {
        // Implementación de detención
    }
}
