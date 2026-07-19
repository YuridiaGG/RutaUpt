package com.example.rutaupt.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.rutaupt.sensor.LocationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var locationProvider: LocationProvider

    override fun onCreate() {
        super.onCreate()
        locationProvider = LocationProvider(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceScope.launch {
            locationProvider.getUbicacionActual().collect { ubicacion ->
                // Aquí se enviaría la ubicación al servidor o se procesaría
                println("Ubicación recibida: ${ubicacion.latitud}, ${ubicacion.longitud}")
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
