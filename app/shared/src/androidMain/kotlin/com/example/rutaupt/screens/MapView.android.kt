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
    pickedLocation: Pair<Double, Double>?,
    gesturesEnabled: Boolean,
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
            isMyLocationEnabled = canShowLocation,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = canShowLocation,
            zoomControlsEnabled = false,
            mapToolbarEnabled = true,
            compassEnabled = true,
            scrollGesturesEnabled = gesturesEnabled,
            zoomGesturesEnabled = gesturesEnabled,
            tiltGesturesEnabled = gesturesEnabled,
            rotationGesturesEnabled = gesturesEnabled
        )
    ) {
        // Marcadores de paradas existentes
        paradas.forEach { parada ->
            val coords = LocationUtils.extraerCoordenadas(parada.ubicacion)
            if (coords != null) {
                val stopLoc = LatLng(coords.first, coords.second)
                Marker(
                    state = MarkerState(position = stopLoc),
                    title = parada.nombre,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    onClick = {
                        onParadaSelected(parada)
                        false
                    }
                )
            }
        }

        // Marcador de ubicación seleccionada (para agregar nueva parada)
        pickedLocation?.let { (lat, lon) ->
            Marker(
                state = MarkerState(position = LatLng(lat, lon)),
                title = "Nueva Parada",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
    }
}
