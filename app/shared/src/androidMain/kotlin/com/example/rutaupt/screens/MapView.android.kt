package com.example.rutaupt.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.rutaupt.api.Parada
import com.example.rutaupt.LocationUtils
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
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLocation, 15f)
    }

    // Centrar el mapa si cambian las coordenadas (ej: al seleccionar una parada desde la lista o al recibir GPS real)
    LaunchedEffect(latitude, longitude) {
        // Evitamos centrar en el mapamundi (0,0)
        if (latitude != 0.0 && longitude != 0.0) {
            // Solo animamos si el usuario no está moviendo el mapa manualmente
            if (cameraPositionState.cameraMoveStartedReason != CameraMoveStartedReason.GESTURE) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(centerLocation),
                    durationMs = 1000
                )
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true, // Punto azul de ubicación real
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
            val coords = LocationUtils.extraerCoordenadas(parada.ubicacion)
            if (coords != null) {
                val stopLoc = LatLng(coords.first, coords.second)
                
                // Calculamos distancia para el marcador
                val metros = LocationUtils.calcularDistanciaMetros(latitude, longitude, coords.first, coords.second)
                val distStr = LocationUtils.formatoDistancia(metros)
                val tiempoMin = LocationUtils.calcularTiempoMinutos(metros)
                
                Marker(
                    state = MarkerState(position = stopLoc),
                    title = parada.nombre,
                    snippet = "A $distStr - Aprox $tiempoMin min",
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
