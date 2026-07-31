package com.example.rutaupt.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.rutaupt.api.Parada
import android.location.Location
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch

@Composable
actual fun MapComponent(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    title: String,
    paradas: List<Parada>,
    onParadaSelected: (Parada) -> Unit
) {
    val centerLocation = LatLng(latitude, longitude)
    val scope = rememberCoroutineScope()
    
    // El cameraPositionState se encarga de gestionar la vista del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLocation, 15f)
    }

    // Efecto para centrar el mapa cuando cambian las coordenadas (ej: al seleccionar una parada)
    LaunchedEffect(latitude, longitude) {
        // Solo animamos si el usuario no está moviendo el mapa manualmente
        if (cameraPositionState.cameraMoveStartedReason != CameraMoveStartedReason.GESTURE) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(centerLocation),
                durationMs = 1000
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true, // Muestra el punto azul de ubicación real de Android
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false,
            mapToolbarEnabled = true,
            compassEnabled = true
        )
    ) {
        // Marcadores para las paradas (Puntos Rojos)
        paradas.forEach { parada ->
            val coords = extraerCoordenadas(parada.ubicacion)
            if (coords != null) {
                val stopLoc = LatLng(coords.first, coords.second)
                
                // Cálculo de distancia para el snippet del marcador
                val distResults = FloatArray(1)
                Location.distanceBetween(latitude, longitude, coords.first, coords.second, distResults)
                val metros = distResults[0]
                val tiempoMin = (metros / 80).toInt().coerceAtLeast(1)
                
                val distStr = if (metros < 1000) "${metros.toInt()} m" else "${(metros/1000).toInt()}.${((metros/100)%10).toInt()} km"
                
                Marker(
                    state = MarkerState(position = stopLoc),
                    title = parada.nombre,
                    snippet = "A $distStr - Aprox $tiempoMin min de ti",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    onClick = {
                        onParadaSelected(parada)
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(stopLoc, 17f))
                        }
                        false
                    }
                )
            }
        }
    }
}

// Función robusta para sacar coordenadas de cualquier link de Google Maps
private fun extraerCoordenadas(link: String?): Pair<Double, Double>? {
    if (link.isNullOrBlank()) return null
    try {
        // Regex para capturar pares de decimales (lat,lon)
        val regex = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
        val matches = regex.findAll(link).toList()
        if (matches.isNotEmpty()) {
            // El último match suele ser la ubicación de destino real
            val lastMatch = matches.last()
            val lat = lastMatch.groupValues[1].toDouble()
            val lon = lastMatch.groupValues[2].toDouble()
            
            // Validar que sean coordenadas geográficas reales
            if (lat in -90.0..90.0 && lon in -180.0..180.0) {
                return Pair(lat, lon)
            }
        }
    } catch (e: Exception) {}
    return null
}
