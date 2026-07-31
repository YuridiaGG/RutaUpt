package com.example.rutaupt.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.rutaupt.api.Parada

@Composable
expect fun MapComponent(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    title: String = "Mi Ubicación",
    paradas: List<Parada> = emptyList(),
    onParadaSelected: (Parada) -> Unit = {},
    onMapClick: (Double, Double) -> Unit = { _, _ -> }
)
