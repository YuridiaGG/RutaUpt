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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationProvider: LocationProvider
    private val apiService = RutaApiService()
    private val notifiedUnits = mutableSetOf<String>()
    private val CHANNEL_ID = "ruta_upt_notifications"

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
                    // Lógica para chofer: Actualizar ubicación en el servidor
                    apiService.actualizarUbicacion(unidadChofer, ubicacion.latitud, ubicacion.longitud)
                } else if (rol == "estudiante") {
                    // Lógica para estudiante: Verificar cercanía de unidades
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

            // Si está a menos de 500 metros (0.5 km)
            if (distancia < 0.5) {
                if (!notifiedUnits.contains(unidad.unidad)) {
                    mostrarNotificacion(unidad.unidad)
                    notifiedUnits.add(unidad.unidad)
                }
            } else {
                // Si se aleja, permitimos volver a notificar después
                notifiedUnits.remove(unidad.unidad)
            }
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radio de la tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun mostrarNotificacion(numeroUnidad: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Deberías usar un icono de tu app
            .setContentTitle("Ruta UPT")
            .setContentText("Unidad $numeroUnidad, esta cerca de ti. :)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(numeroUnidad.hashCode(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Avisos de Proximidad"
            val descriptionText = "Notificaciones cuando una unidad está cerca"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
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
