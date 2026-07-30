package com.example.rutaupt.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destination, 15f)
    }
    LaunchedEffect(latitude, longitude) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(destination, 15f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true // Esto muestra el punto azul de ubicación real
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            mapToolbarEnabled = true
        )
    ) {
        Marker(
            state = MarkerState(position = destination),
            title = title
        )
    }
}
