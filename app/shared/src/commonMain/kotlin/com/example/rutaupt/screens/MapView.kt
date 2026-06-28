package com.example.rutaupt.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapComponent(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    title: String = "Ubicación"
)
