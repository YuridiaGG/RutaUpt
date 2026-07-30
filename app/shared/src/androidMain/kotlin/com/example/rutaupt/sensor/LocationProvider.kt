package com.example.rutaupt.sensor

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.example.rutaupt.model.Ubicacion
import com.example.rutaupt.repository.SensorRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationProvider(private val context: Context) : SensorRepository {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getUbicacionActual(): Flow<Ubicacion> = callbackFlow {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(Ubicacion(location.latitude, location.longitude, location.time))
            }
        }

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                10f,
                listener
            )
            
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                trySend(Ubicacion(it.latitude, it.longitude, it.time))
            }
        } catch (e: Exception) {
        }

        awaitClose { locationManager.removeUpdates(listener) }
    }

    override fun iniciarSeguimiento() {}
    override fun detenerSeguimiento() {}
}
