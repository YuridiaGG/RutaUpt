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
    
    LaunchedEffect(Unit) {
        try {
            MapsInitializer.initialize(context)
        } catch (e: Exception) {}
    }

    val safeLat = if (latitude != 0.0) latitude else LocationUtils.DEFAULT_LAT
    val safeLon = if (longitude != 0.0) longitude else LocationUtils.DEFAULT_LON
    val currentLatLng = LatLng(safeLat, safeLon)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
    }

    LaunchedEffect(safeLat, safeLon) {
        if (latitude != 0.0 && longitude != 0.0) {
            if (cameraPositionState.cameraMoveStartedReason != CameraMoveStartedReason.GESTURE) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLng(currentLatLng))
            }
        }
    }

    val canShowLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng -> if (gesturesEnabled) onMapClick(latLng.latitude, latLng.longitude) },
        properties = MapProperties(
            isMyLocationEnabled = canShowLocation,
            mapType = MapType.NORMAL
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,        // Botones +/- para facilitar zoom si el pellizco falla por el scroll parent
            zoomGesturesEnabled = true,        // Habilita zoom por pellizco siempre
            scrollGesturesEnabled = gesturesEnabled,
            tiltGesturesEnabled = gesturesEnabled,
            rotationGesturesEnabled = gesturesEnabled,
            myLocationButtonEnabled = canShowLocation && gesturesEnabled,
            compassEnabled = true,
            mapToolbarEnabled = true
        )
    ) {
        if (pickedLocation == null) {
            Marker(
                state = rememberMarkerState(key = "main", position = currentLatLng),
                title = title,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }

        // Marcadores de paradas estables
        paradas.forEach { parada ->
            LocationUtils.extraerCoordenadas(parada.ubicacion)?.let { coords ->
                Marker(
                    state = rememberMarkerState(key = "stop_${parada.id ?: parada.nombre}", position = LatLng(coords.first, coords.second)),
                    title = parada.nombre,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                    onClick = { 
                        if (gesturesEnabled) onParadaSelected(parada)
                        false 
                    }
                )
            }
        }

        // Marcador de selección del Admin
        pickedLocation?.let { (lat, lon) ->
            Marker(
                state = rememberMarkerState(key = "picked", position = LatLng(lat, lon)),
                title = "Ubicación seleccionada",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }
    }
}
