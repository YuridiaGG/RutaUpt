package com.example.rutaupt.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
actual fun MapComponent(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    title: String
) {
    val location = LatLng(latitude, longitude)
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                onResume()
                getMapAsync { googleMap ->
                    googleMap.addMarker(MarkerOptions().position(location).title(title))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                    googleMap.uiSettings.isZoomControlsEnabled = true
                }
            }
        },
        update = { mapView ->
            mapView.getMapAsync { googleMap ->
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(location).title(title))
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(location))
            }
        }
    )
}
