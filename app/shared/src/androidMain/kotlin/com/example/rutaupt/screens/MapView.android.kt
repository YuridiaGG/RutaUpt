package com.example.rutaupt.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.rutaupt.api.Parada
import com.example.rutaupt.LocationUtils
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch

@Composable
actual fun MapComponent(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    title: String,
    paradas: List<Parada>,
    onParadaSelected: (Parada) -> Unit,
    onMapClick: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val centerLocation = LatLng(latitude, longitude)
    val scope = rememberCoroutineScope()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerLocation, 15f)
    }

    // Inicialización segura del motor de mapas
    LaunchedEffect(Unit) {
        try {
            MapsInitializer.initialize(context)
        } catch (e: Throwable) {}
    }

    // Centrado seguro de la cámara
    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            try {
                if (cameraPositionState.cameraMoveStartedReason != CameraMoveStartedReason.GESTURE) {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLng(centerLocation),
                        durationMs = 1000
                    )
                }
            } catch (e: Throwable) {}
        }
    }

    // VERIFICACIÓN DE PERMISOS PARA EVITAR SecurityException (Causante de los cierres)
    val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val canShowLocation = hasFineLocation || hasCoarseLocation

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            onMapClick(latLng.latitude, latLng.longitude)
        },
        properties = MapProperties(
            // Solo activamos la capa de ubicación si Android confirma que tenemos el permiso
            isMyLocationEnabled = canShowLocation,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = canShowLocation,
            zoomControlsEnabled = false,
            mapToolbarEnabled = true,
            compassEnabled = true
        )
    ) {
        paradas.forEach { parada ->
            val coords = LocationUtils.extraerCoordenadas(parada.ubicacion)
            if (coords != null) {
                val stopLoc = LatLng(coords.first, coords.second)
                
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
                            try {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(stopLoc, 17f))
                            } catch (e: Exception) {}
                        }
                        false
                    }
                )
            }
        }
    }
}
