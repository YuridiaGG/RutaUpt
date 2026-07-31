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

@Composable
actual fun MapComponent(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    title: String,
    paradas: List<Parada>,
    onParadaSelected: (Parada) -> Unit
) {
    val userLocation = LatLng(latitude, longitude)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }

    // Efecto para centrar la cámara cuando cambia la ubicación del usuario (solo si no hay interacción manual)
    LaunchedEffect(latitude, longitude) {
        if (cameraPositionState.cameraMoveStartedReason != CameraMoveStartedReason.GESTURE) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(userLocation),
                durationMs = 1000
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false,
            mapToolbarEnabled = true
        )
    ) {
        // Marcador del Estudiante (Azul)
        Marker(
            state = MarkerState(position = userLocation),
            title = title,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )

        // Marcadores para las paradas (Rojo)
        paradas.forEach { parada ->
            val coords = extraerCoordenadas(parada.ubicacion)
            if (coords != null) {
                val stopLoc = LatLng(coords.first, coords.second)
                val distStr = calcularDistanciaTexto(latitude, longitude, coords.first, coords.second)
                
                Marker(
                    state = MarkerState(position = stopLoc),
                    title = parada.nombre,
                    snippet = "A $distStr de ti",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    onClick = {
                        onParadaSelected(parada)
                        // Centrar la cámara suavemente en la parada al tocar el marcador
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(stopLoc, 16f))
                        false
                    }
                )
            }
        }
    }
}

private fun extraerCoordenadas(link: String?): Pair<Double, Double>? {
    if (link.isNullOrBlank()) return null
    try {
        // Soporte para múltiples formatos de Google Maps:
        // 1. @20.123,-98.456
        // 2. q=20.123,-98.456
        // 3. place/20.123,-98.456
        val regex = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
        val matches = regex.findAll(link).toList()
        
        // Generalmente la ubicación exacta es la última que aparece en el link de destino
        if (matches.isNotEmpty()) {
            val lastMatch = matches.last()
            val lat = lastMatch.groupValues[1].toDouble()
            val lon = lastMatch.groupValues[2].toDouble()
            
            // Validación básica para evitar capturar el zoom (ej. 17z) si el regex fuera muy permisivo
            if (lat in -90.0..90.0 && lon in -180.0..180.0) {
                return Pair(lat, lon)
            }
        }
    } catch (e: Exception) {
        println("ERROR_MAP_COORDS: ${e.message}")
    }
    return null
}

private fun calcularDistanciaTexto(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    val metros = results[0]
    return if (metros < 1000) {
        "${metros.toInt()} metros"
    } else {
        val km = metros / 1000
        "${km.toInt()}.${((km % 1) * 10).toInt()} km"
    }
}
