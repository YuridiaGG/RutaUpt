package com.example.rutaupt.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.rutaupt.sensor.LocationProvider
import com.example.rutaupt.storage.SessionManager
import com.example.rutaupt.api.RutaApiService
import com.example.rutaupt.model.Ubicacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.*

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationProvider: LocationProvider
    private val apiService = RutaApiService()
    private val notifiedUnits = mutableSetOf<String>()
    private val CHANNEL_ID = "ruta_upt_proximity_v2"

    override fun onCreate() {
        super.onCreate()
        locationProvider = LocationProvider(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val rol = SessionManager.rolUsuario.lowercase()
        val unidadChofer = SessionManager.numeroUnidad

        serviceScope.launch {
            locationProvider.getUbicacionActual().collect { ubicacion ->
                if (rol == "chofer" && unidadChofer.isNotEmpty()) {
                    apiService.actualizarUbicacion(unidadChofer, ubicacion.latitud, ubicacion.longitud)
                } else if (rol == "estudiante") {
                    verificarUnidadesCercanas(ubicacion)
                }
            }
        }
        return START_STICKY
    }

    private suspend fun verificarUnidadesCercanas(miUbicacion: Ubicacion) {
        val unidades = apiService.obtenerUbicaciones()
        for (unidad in unidades) {
            val distancia = calcularDistancia(
                miUbicacion.latitud, miUbicacion.longitud,
                unidad.latitud, unidad.longitud
            )

            // Si está a menos de 400 metros
            if (distancia < 0.4) {
                if (!notifiedUnits.contains(unidad.unidad)) {
                    mostrarNotificacion(unidad.unidad)
                    notifiedUnits.add(unidad.unidad)
                }
            } else if (distancia > 0.8) {
                // Se alejó lo suficiente para permitir una nueva notificación en el futuro
                notifiedUnits.remove(unidad.unidad)
            }
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radio de la tierra en km
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2).pow(2.0) + cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return r * c
    }

    private fun mostrarNotificacion(numeroUnidad: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Ruta UPT")
            .setContentText("Unidad $numeroUnidad esta cerca de ti") // Texto exacto solicitado
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)

        notificationManager.notify(numeroUnidad.hashCode(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Avisos de Proximidad"
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones cuando una unidad está cerca"
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
