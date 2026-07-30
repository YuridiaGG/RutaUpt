package com.example.rutaupt.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
actual fun MapComponent(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    title: String
) {
    val destination = LatLng(latitude, longitude)
    
    // Usamos rememberCameraPositionState para mantener el estado de la cámara (zoom, rotación, etc.)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destination, 15f)
    }

    // Efecto para seguir la ubicación sin "saltos" bruscos ni resetear el zoom
    LaunchedEffect(latitude, longitude) {
        // Solo movemos la cámara automáticamente si el usuario NO está interactuando con el mapa
        // y si el movimiento no fue iniciado por un gesto (para evitar el snap-back)
        if (cameraPositionState.cameraMoveStartedReason != CameraMoveStartedReason.GESTURE) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(destination),
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
        Marker(
            state = MarkerState(position = destination),
            title = title
        )
    }
}
