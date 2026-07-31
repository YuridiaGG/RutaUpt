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
    val userLocation = LatLng(latitude, longitude)
    val scope = rememberCoroutineScope()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }

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
            mapToolbarEnabled = true,
            compassEnabled = true
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
                val distResults = FloatArray(1)
                Location.distanceBetween(latitude, longitude, coords.first, coords.second, distResults)
                val metros = distResults[0]
                val tiempoMin = (metros / 80).toInt().coerceAtLeast(1) // Estimación caminando (80m/min)
                
                val distStr = if (metros < 1000) "${metros.toInt()} m" else "${(metros/1000).toInt()}.${((metros/100)%10).toInt()} km"
                
                Marker(
                    state = MarkerState(position = stopLoc),
                    title = parada.nombre,
                    snippet = "A $distStr - Aprox $tiempoMin min",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    onClick = {
                        onParadaSelected(parada)
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(stopLoc, 16f))
                        }
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
        // Regex robusto para capturar lat,lon en links de Google Maps
        val regex = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
        val matches = regex.findAll(link).toList()
        if (matches.isNotEmpty()) {
            val lastMatch = matches.last()
            return Pair(lastMatch.groupValues[1].toDouble(), lastMatch.groupValues[2].toDouble())
        }
    } catch (e: Exception) {}
    return null
}
