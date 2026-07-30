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
        // Marcador de usuario (si no se usa isMyLocationEnabled o para personalizarlo)
        Marker(
            state = MarkerState(position = userLocation),
            title = title,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )

        // Marcadores para las paradas
        paradas.forEach { parada ->
            val coords = extraerCoordenadas(parada.ubicacion)
            if (coords != null) {
                val distStr = calcularDistanciaTexto(latitude, longitude, coords.first, coords.second)
                Marker(
                    state = MarkerState(position = LatLng(coords.first, coords.second)),
                    title = parada.nombre,
                    snippet = "A $distStr de ti",
                    onClick = {
                        onParadaSelected(parada)
                        false
                    }
                )
            }
        }
    }
}

private fun extraerCoordenadas(link: String?): Pair<Double, Double>? {
    if (link == null) return null
    try {
        // Formato: ...query=lat,lon o ...@lat,lon...
        val regex = Regex("([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)")
        val match = regex.find(link)
        if (match != null) {
            val lat = match.groupValues[1].toDouble()
            val lon = match.groupValues[2].toDouble()
            return Pair(lat, lon)
        }
    } catch (e: Exception) {}
    return null
}

private fun calcularDistanciaTexto(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    val metros = results[0]
    return if (metros < 1000) {
        "${metros.toInt()} metros"
    } else {
        "${(metros / 1000).format(1)} km"
    }
}

private fun Float.format(digits: Int) = "%.${digits}f".format(this)
